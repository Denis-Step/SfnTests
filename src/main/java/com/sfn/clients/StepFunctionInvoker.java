package com.sfn.clients;

import org.jetbrains.annotations.Nullable;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

import javax.inject.Inject;
import java.util.UUID;

public final class StepFunctionInvoker {

    private static final int MAX_HISTORY_RESULTS = 1000;

    private final SfnClient sfnClient;
    private final String stepFunctionArn;

    @Inject
    public StepFunctionInvoker(SfnClient sfnClient,
                               String stepFunctionArn) {
        this.sfnClient = sfnClient;
        this.stepFunctionArn = stepFunctionArn;
    }

    public String invoke(String payload, String executionName) {
        return this.sfnClient.startExecution(createStartExecutionRequest(payload, executionName))
                .executionArn();
    }

    public String invoke(String payload) {
        return this.sfnClient.startExecution(createStartExecutionRequest(payload, null))
                .executionArn();
    }

    public GetExecutionHistoryResponse pollExecution(String executionArn) {
        return this.sfnClient.getExecutionHistory(createGetExecutionHistoryRequest(executionArn));
    }

    private GetExecutionHistoryRequest createGetExecutionHistoryRequest(String executionArn) {
        return GetExecutionHistoryRequest.builder()
                .executionArn(executionArn)
                .reverseOrder(true)
                .maxResults(MAX_HISTORY_RESULTS)
                .build();
    }

    private StartExecutionRequest createStartExecutionRequest(String input,
                                                              @Nullable String name) {
        String executionArn = name != null ? name : newExecutionName();

        return StartExecutionRequest.builder()
                .stateMachineArn(this.stepFunctionArn)
                .name(executionArn)
                .input(input)
                .build();
    }


    private String newExecutionName() {
        return UUID.randomUUID().toString();
    }

}
