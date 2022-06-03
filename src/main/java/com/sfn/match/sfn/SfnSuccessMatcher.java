package com.sfn.match.sfn;

import software.amazon.awssdk.services.sfn.model.GetExecutionHistoryResponse;
import software.amazon.awssdk.services.sfn.model.HistoryEventType;

import java.util.List;
import java.util.function.Function;

/**
 * Check entire history because order of event types cannot be guaranteed. Performance may suffer, this is a TODO
 * to fix in the future.
 */
public final class SfnSuccessMatcher implements Function<GetExecutionHistoryResponse, Boolean> {

    List<HistoryEventType> TERMINAL_EXECUTION_SUCCESS_STATES = List.of(
            HistoryEventType.EXECUTION_SUCCEEDED);

    @Override
    public Boolean apply(GetExecutionHistoryResponse historyResponse) {
        return historyResponse.events().stream()
                .anyMatch(historyEvent -> TERMINAL_EXECUTION_SUCCESS_STATES.contains(historyEvent.type()));
    }
}
