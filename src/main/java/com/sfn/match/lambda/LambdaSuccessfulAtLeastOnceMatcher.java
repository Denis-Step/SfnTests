package com.sfn.match.lambda;

import software.amazon.awssdk.services.sfn.model.GetExecutionHistoryResponse;
import software.amazon.awssdk.services.sfn.model.HistoryEvent;
import software.amazon.awssdk.services.sfn.model.HistoryEventType;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Assumes GetExecutionHistoryResponse passed in reverse-order. Lambda resource information is not available
 * on success states, it is only available in the scheduled states. Thus, to validate a Lambda execution:
 * <ol>
 *     <li>Find a LAMBDA_FUNCTION_SCHEDULED event indicating the lambda was scheduled.</li>
 *     <li>Find the corresponding LAMBDA_FUNCTION_SUCCEEDED event that should be 2 events immediately after
 *     this lambda.</li>
 * </ol>
 * This matcher asserts that a specific Lambda was scheduled and succeeded AT LEAST ONCE. The lambda may have
 * failed 0 or more times and this will be ignored as long as it was successful once.
 */
public final class LambdaSuccessfulAtLeastOnceMatcher implements Function<GetExecutionHistoryResponse, Boolean> {

    HistoryEventType LAMBDA_FUNCTION_SCHEDULED_STATE = HistoryEventType.LAMBDA_FUNCTION_SCHEDULED;
    HistoryEventType LAMBDA_FUNCTION_SUCCESS_STATE = HistoryEventType.LAMBDA_FUNCTION_SUCCEEDED;

    private final String lambdaArn;

    public LambdaSuccessfulAtLeastOnceMatcher(String lambdaArn) {
        this.lambdaArn = lambdaArn;
    }

    @Override
    public Boolean apply(GetExecutionHistoryResponse historyResponse) {
        List<Integer> lambdaScheduledIdxs = IntStream.range(0, historyResponse.events().size())
                .filter(idx -> {
                    HistoryEvent historyEvent = historyResponse.events().get(idx);
                    return historyEvent.type().equals(LAMBDA_FUNCTION_SCHEDULED_STATE) &&
                            historyEvent.lambdaFunctionScheduledEventDetails().resource().equals(lambdaArn);
                })
                .boxed()
                .collect(Collectors.toList());

        // Look for matching LAMBDA_FUNCTION_SUCCEEDED HistoryEventType that should be 2 events after a
        // LAMBDA_FUNCTION_SCHEDULED task.
        for (int idx : lambdaScheduledIdxs) {
            if (idx + 2 < historyResponse.events().size() &&
                    historyResponse.events().get(idx + 2).type().equals(LAMBDA_FUNCTION_SUCCESS_STATE)) {
                return true;
            }
        }
        return false;
    }
}
