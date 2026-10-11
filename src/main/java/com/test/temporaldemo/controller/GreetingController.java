package com.test.temporaldemo.controller;

import com.test.temporaldemo.temporal.workflow.GreetingWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/greet")
public class GreetingController {

    private static final String TASK_QUEUE = "greeting-queue";

    private final WorkflowClient workflowClient;

    public GreetingController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    // Starts the workflow and blocks until it completes
    @PostMapping("/{name}")
    public String greet(@PathVariable String name) {
        GreetingWorkflow workflow = newStub(name);
        return workflow.greet(name);
    }

    // Starts the workflow and returns its ID immediately
    @PostMapping("/async/{name}")
    public String greetAsync(@PathVariable String name) {
        GreetingWorkflow workflow = newStub(name);
        return WorkflowClient.start(workflow::greet, name).getWorkflowId();
    }

    // Fetches the result of a previously started workflow (waits if still running)
    @GetMapping("/result/{workflowId}")
    public String result(@PathVariable String workflowId) {
        return workflowClient.newUntypedWorkflowStub(workflowId).getResult(String.class);
    }

    // Sends the approve signal to a running workflow
    @PostMapping("/approve/{workflowId}/{approver}")
    public String approve(@PathVariable String workflowId, @PathVariable String approver) {
        workflowClient.newWorkflowStub(GreetingWorkflow.class, workflowId).approve(approver);
        return "Approval sent";
    }

    // Queries the current status of a workflow
    @GetMapping("/status/{workflowId}")
    public String status(@PathVariable String workflowId) {
        return workflowClient.newWorkflowStub(GreetingWorkflow.class, workflowId).getStatus();
    }

    private GreetingWorkflow newStub(String name) {
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TASK_QUEUE)
                .setWorkflowId("greet-" + name + "-" + UUID.randomUUID())
                .build();
        return workflowClient.newWorkflowStub(GreetingWorkflow.class, options);
    }
}
