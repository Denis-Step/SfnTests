package com.sfn.match.sfn;

import software.amazon.awssdk.services.sfn.model.GetExecutionHistoryResponse;
import software.amazon.awssdk.services.sfn.model.HistoryEventType;

import java.util.List;
import java.util.function.Function;

/**
 * Checks to see if the Sfn has completed execution, not necessarily successfully. This is the default matcher.
 * @implNote Check entire history because order of event types cannot be guaranteed. Performance may suffer, this is a TODO
 * to fix in the future.
 */
public final class SfnCompleteMatcher implements Function<GetExecutionHistoryResponse, Boolean> {

    private static final List<HistoryEventType> TERMINAL_EXECUTION_STATES = List.of(
            HistoryEventType.EXECUTION_ABORTED,
            HistoryEventType.EXECUTION_FAILED,
            HistoryEventType.EXECUTION_SUCCEEDED,
            HistoryEventType.EXECUTION_TIMED_OUT);

    @Override
    public Boolean apply(GetExecutionHistoryResponse historyResponse) {
        return historyResponse.events().stream()
                .anyMatch(historyEvent -> TERMINAL_EXECUTION_STATES.contains(historyEvent.type()));
    }
}
