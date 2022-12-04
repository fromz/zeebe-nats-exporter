package io.camunda.zeebe.exporters.nats;

import io.camunda.zeebe.protocol.record.Record;
import io.camunda.zeebe.protocol.record.RecordType;
import io.camunda.zeebe.protocol.record.ValueType;

public class NatsConfiguration {
    /**
     * The URL of the NATS endpoint
     */
    public String url = "http://localhost:4222";

    /**
     * The name of the NATS subject to publish events to.
     */
    public String subject = "zeebe";

    /**
     * To configure the amount of records, which has to be reached before the
     * records are exported. Only counts the records which are in the end actually
     * exported.
     */
    public int batchSize = 100;

    /**
     * To configure the time in milliseconds, when the batch should be executed
     * regardless whether the batch size was reached or not.
     */
    public int batchTime = 300;

    public final IndexConfiguration record = new IndexConfiguration();

    public boolean shouldIndexRecord(final Record<?> record) {
        return shouldIndexRecordType(record.getRecordType())
            && shouldIndexValueType(record.getValueType());
      }
    
      public boolean shouldIndexValueType(final ValueType valueType) {
        switch (valueType) {
          case DEPLOYMENT:
            return record.deployment;
          case PROCESS:
            return record.process;
          case ERROR:
            return record.error;
          case INCIDENT:
            return record.incident;
          case JOB:
            return record.job;
          case JOB_BATCH:
            return record.jobBatch;
          case MESSAGE:
            return record.message;
          case MESSAGE_SUBSCRIPTION:
            return record.messageSubscription;
          case VARIABLE:
            return record.variable;
          case VARIABLE_DOCUMENT:
            return record.variableDocument;
          case PROCESS_INSTANCE:
            return record.processInstance;
          case PROCESS_INSTANCE_CREATION:
            return record.processInstanceCreation;
          case PROCESS_INSTANCE_MODIFICATION:
            return record.processInstanceModification;
          case PROCESS_MESSAGE_SUBSCRIPTION:
            return record.processMessageSubscription;
          case DECISION_REQUIREMENTS:
            return record.decisionRequirements;
          case DECISION:
            return record.decision;
          case DECISION_EVALUATION:
            return record.decisionEvaluation;
          case CHECKPOINT:
            return record.checkpoint;
          case TIMER:
            return record.timer;
          case MESSAGE_START_EVENT_SUBSCRIPTION:
            return record.messageStartEventSubscription;
          case PROCESS_EVENT:
            return record.processEvent;
          case DEPLOYMENT_DISTRIBUTION:
            return record.deploymentDistribution;
        //   case ESCALATION:
        //     return index.escalation;
          default:
            return false;
        }
      }
    
      public boolean shouldIndexRecordType(final RecordType recordType) {
        switch (recordType) {
          case EVENT:
            return record.event;
          case COMMAND:
            return record.command;
          case COMMAND_REJECTION:
            return record.rejection;
          default:
            return false;
        }
      }
    
      public static class IndexConfiguration {
        // prefix for index and templates
        public String prefix = "zeebe-record";
    
        // update index template on startup
        public boolean createTemplate = true;
    
        // record types to export
        public boolean command = false;
        public boolean event = true;
        public boolean rejection = false;
    
        // value types to export
        public boolean decision = true;
        public boolean decisionEvaluation = true;
        public boolean decisionRequirements = true;
        public boolean deployment = true;
        public boolean error = true;
        public boolean incident = true;
        public boolean job = true;
        public boolean jobBatch = false;
        public boolean message = true;
        public boolean messageSubscription = true;
        public boolean process = true;
        public boolean processInstance = true;
        public boolean processInstanceCreation = true;
        public boolean processInstanceModification = true;
        public boolean processMessageSubscription = true;
        public boolean variable = true;
        public boolean variableDocument = true;
    
        public boolean checkpoint = false;
        public boolean timer = true;
        public boolean messageStartEventSubscription = true;
        public boolean processEvent = false;
        public boolean deploymentDistribution = true;
        public boolean escalation = true;
    
        // index settings
        private Integer numberOfShards = null;
        private Integer numberOfReplicas = null;
    
        public Integer getNumberOfShards() {
          return numberOfShards;
        }
    
        public void setNumberOfShards(final Integer numberOfShards) {
          this.numberOfShards = numberOfShards;
        }
    
        public Integer getNumberOfReplicas() {
          return numberOfReplicas;
        }
    
        public void setNumberOfReplicas(final Integer numberOfReplicas) {
          this.numberOfReplicas = numberOfReplicas;
        }
    
        @Override
        public String toString() {
          return "IndexConfiguration{"
              + "indexPrefix='"
              + prefix
              + '\''
              + ", createTemplate="
              + createTemplate
              + ", command="
              + command
              + ", event="
              + event
              + ", rejection="
              + rejection
              + ", error="
              + error
              + ", deployment="
              + deployment
              + ", process="
              + process
              + ", incident="
              + incident
              + ", job="
              + job
              + ", message="
              + message
              + ", messageSubscription="
              + messageSubscription
              + ", variable="
              + variable
              + ", variableDocument="
              + variableDocument
              + ", processInstance="
              + processInstance
              + ", processInstanceCreation="
              + processInstanceCreation
              + ", processInstanceModification="
              + processInstanceModification
              + ", processMessageSubscription="
              + processMessageSubscription
              + ", decisionRequirements="
              + decisionRequirements
              + ", decision="
              + decision
              + ", decisionEvaluation="
              + decisionEvaluation
              + ", checkpoint="
              + checkpoint
              + ", timer="
              + timer
              + ", messageStartEventSubscription="
              + messageStartEventSubscription
              + ", processEvent="
              + processEvent
              + ", deploymentDistribution="
              + deploymentDistribution
              + ", escalation="
              + escalation
              + '}';
        }
      }    
}