package com.test.temporaldemo.temporal.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface GreetingWorkflow {

    @WorkflowMethod
    String greet(String name);

    // Signal: sends data into the running workflow (async, recorded in history)
    @SignalMethod
    void approve(String approver);

    // Query: reads current state (sync, read-only, not recorded in history)
    @QueryMethod
    String getStatus();
}
