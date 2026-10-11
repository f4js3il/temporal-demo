package com.test.temporaldemo.temporal.workflow.impl;

import com.test.temporaldemo.temporal.activity.GreetingActivity;
import com.test.temporaldemo.temporal.workflow.GreetingWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;

import java.time.Duration;

@WorkflowImpl(taskQueues = "greeting-queue")
public class GreetingWorkflowImpl implements GreetingWorkflow {

    private final GreetingActivity greetingActivity = Workflow.newActivityStub(
            GreetingActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder()
                            .setInitialInterval(Duration.ofSeconds(1))
                            .setBackoffCoefficient(2.0)
                            .setMaximumAttempts(3)
                            .build())
                    .build());

    private String status = "WAITING_FOR_APPROVAL";
    private String approver;

    @Override
    public String greet(String name) {
        // Durable wait: survives app restarts. Gives up after 5 minutes.
        boolean approved = Workflow.await(Duration.ofMinutes(5), () -> approver != null);
        if (!approved) {
            status = "REJECTED_TIMEOUT";
            return "No approval for " + name;
        }

        status = "COMPOSING";
        String greeting = greetingActivity.composeGreeting(name);

        status = "DONE";
        return greeting + " (approved by " + approver + ")";
    }

    @Override
    public void approve(String approver) {
        this.approver = approver;
    }

    @Override
    public String getStatus() {
        return status;
    }
}
