package com.edm.camellearning.components;

import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;

public interface ConsumerBridge {

  void readMessage(ProcessRecordsInput processRecordsInput);
}
