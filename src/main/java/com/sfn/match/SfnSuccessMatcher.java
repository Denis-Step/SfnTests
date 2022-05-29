package com.sfn.match;

import software.amazon.awssdk.services.sfn.model.GetExecutionHistoryResponse;
import software.amazon.awssdk.services.sfn.model.HistoryEventType;

import java.util.List;

public interface SfnSuccessMatcher {

    List<HistoryEventType> TERMINAL_EXECUTION_SUCCESS_STATES = List.of(
            HistoryEventType.EXECUTION_SUCCEEDED);

    /**
     * Check entire history because order of event types cannot be guaranteed. Performance may suffer, this is a TODO
     * to fix in the future.
     * @param history GetExecutionHistoryResponse from AWS SDK client
     * @return Boolean indicating whether execution succeeded
     */
    static Boolean apply(GetExecutionHistoryResponse history) {
        return history.events().stream()
                .anyMatch(historyEvent -> TERMINAL_EXECUTION_SUCCESS_STATES.contains(historyEvent.type()));
    }
}
