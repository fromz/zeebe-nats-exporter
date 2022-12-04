package io.camunda.zeebe.exporters.nats;

import io.camunda.zeebe.exporter.api.Exporter;
import io.camunda.zeebe.exporter.api.context.Context;
import io.camunda.zeebe.exporter.api.context.Controller;
import io.camunda.zeebe.protocol.record.Record;
import org.slf4j.Logger;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.io.IOException;
import java.time.Duration;

public class NatsExporter implements Exporter
{
    private Logger log;
    private NatsConfiguration configuration;
    private EventQueue eventQueue;
    private Batcher batcher;
    private Sender sender;
    private Controller controller;

  /**
   * Use the provided configuration at this point to configure your exporter.
   *
   * <p>This method is called in two difference contexts: 1. right before opening the exporter, to
   * configure the exporter 2. at startup, to allow validation of the exporter configuration.
   *
   * <p>To fail-fast at startup time (e.g. database endpoint is missing), for now you must throw an
   * exception.
   *
   * <p>Note that the instance configured at startup will be discarded immediately.
   *
   * @param context the exporter context
   */
    public void configure(final Context context) {
        configuration = context
                .getConfiguration()
                .instantiate(NatsConfiguration.class);

        this.log = context.getLogger();

        RecordFilter filter = new RecordFilter();
        context.setFilter(filter);
        log.debug("NATS Exporter configured with {}", configuration.record.toString());
    }

  /**
   * Hook to perform any setup for a given exporter. This method is the first method called during
   * the lifecycle of an exporter, and should be use to create, allocate or configure resources.
   * After this is called, records will be published to this exporter.
   *
   * @param controller specific controller for this exporter
   */
    public void open(final Controller controller) {
        NatsExporterContext context = new NatsExporterContext(controller, configuration, log);
        eventQueue = new EventQueue();
        batcher = new Batcher(context);
        try {
            sender = new Sender(context);
        } catch (IOException | InterruptedException e) {
            log.debug(e.getStackTrace().toString());
        }
        this.controller =  controller;
        controller.scheduleCancellableTask(Duration.ofMillis(batcher.batchPeriod), this::batchEvents);
        controller.scheduleCancellableTask(Duration.ofMillis(sender.sendPeriod), this::sendBatch);
        log.debug("NATS exporter started.");
    }

  /**
   * Hook to perform any tear down. This is method is called exactly once at the end of the
   * lifecycle of an exporter, and should be used to close and free any remaining resources.
   */
    public void close() {
        log.debug("Closing NATS Exporter");
        try {
            sender.close();
        } catch (TimeoutException | InterruptedException e) {
            log.error(e.toString());
        }
    }

  /**
   * Called at least once for every record to be exported. Once a record is guaranteed to have been
   * exported, implementations should call {@link Controller#updateLastExportedRecordPosition(long)}
   * to signal that this record should not be received here ever again.
   *
   * <p>Should the export method throw an unexpected {@link RuntimeException}, the method will be
   * called indefinitely until it terminates without any exception. It is up to the implementation
   * to handle errors properly, to implement retry strategies, etc.
   *
   * <p>Given Record just wraps the underlying internal buffer. This means if the implementation
   * needs to collect multiple records it either has to call {@link Record#toJson()} to get the
   * serialized version of the record or {@link Record#clone()} to get a deep copy.
   *
   * @param record the record to export
   */
    public void export(Record<?> record) {
        if (!configuration.shouldIndexRecord(record)) {
            return;
        }
        eventQueue.addEvent(record);
    }

    private void batchEvents() {
        batcher.batchFrom(eventQueue);
        controller.scheduleCancellableTask(Duration.ofMillis(batcher.batchPeriod), this::batchEvents);
    }

    private void sendBatch() {
        sender.sendFrom(batcher);
        controller.scheduleCancellableTask(Duration.ofMillis(sender.sendPeriod), this::sendBatch);
    }
}