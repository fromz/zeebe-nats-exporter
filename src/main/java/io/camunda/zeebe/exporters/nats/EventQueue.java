package io.camunda.zeebe.exporters.nats;

import io.camunda.zeebe.protocol.record.Record;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.JSONObject;
import java.util.LinkedList;

public class EventQueue {
    final LinkedList<ImmutablePair<Long, JSONObject>> queue = new LinkedList<>();

    void addEvent(Record<?> record) {
        final JSONObject json = new JSONObject(record.toJson());
        json.put("fullRecordType", String.format("%s_%s_%s", record.getRecordType(), record.getValueType(), record.getIntent()));
        json.put("eventId", createIdempotentEventId(record));
        queue.add(new ImmutablePair<>(record.getPosition(), json));
    }

    /**
     * Events passed to the exporter are at-least-once -- the same event may be seen twice.
     * Event Store guarantees idempotent event creation based on the eventId - which must be a UUID.
     * We use a seed UUID, and replace the last part with the position and partition to get
     * a UUID that is idempotent for an event on the broker.
     */
    private String createIdempotentEventId(Record<?> record) {
        String seed = "393d7039721342d6b619de6bff4ffd2e";
        String id = String.valueOf(record.getPosition()) + record.getPartitionId();
        StringBuilder sb = new StringBuilder(seed);
        sb.delete(31 - id.length(), 31);
        sb.append(id);
        sb.insert(8, "-");
        sb.insert(13, "-");
        sb.insert(18, "-");
        sb.insert(23, "-");
        return sb.toString();
    }
}