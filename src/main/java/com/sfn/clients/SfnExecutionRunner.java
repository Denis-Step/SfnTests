package com.sfn.clients;

import com.sfn.data.ImmutablePollExecutionRequest;
import com.sfn.data.TestExecutionRequest;
import com.sfn.data.PollExecutionRequest;
import software.amazon.awssdk.services.sfn.model.GetExecutionHistoryResponse;
import software.amazon.awssdk.services.sfn.model.HistoryEventType;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SfnExecutionRunner {

    private static final long DEFAULT_POLL_DELAY_MS = 1000L;

    private static final List<HistoryEventType> TERMINAL_EXECUTION_STATE_TYPES = List.of(
            HistoryEventType.EXECUTION_ABORTED,
            HistoryEventType.EXECUTION_FAILED,
            HistoryEventType.EXECUTION_STARTED,
            HistoryEventType.EXECUTION_SUCCEEDED,
            HistoryEventType.EXECUTION_TIMED_OUT);

    private final StepFunctionInvoker stepFunctionInvoker;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Inject
    public SfnExecutionRunner(StepFunctionInvoker stepFunctionInvoker) {
        this.stepFunctionInvoker = stepFunctionInvoker;
        this.threadPoolExecutor = new ThreadPoolExecutor(10, 20,
                20000L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());
    }

    public void runExecutions(List<TestExecutionRequest> requests) {
        requests.stream()
                .map(this::startExecutionRunnable)
                .forEach(threadPoolExecutor::execute);
    }

    private Map<String, TestExecutionRequest> createExecutionMap(List<TestExecutionRequest> testExecutionRequests) {
        return testExecutionRequests.stream()
        .map(t -> {
            String execArn = stepFunctionInvoker.invoke(t.payload());
            return Map.entry(execArn, t); })
        .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue
        ));
    }

    private Runnable startExecutionRunnable(TestExecutionRequest request) {
        return () -> {
            String execArn = stepFunctionInvoker.invoke(request.payload());
            threadPoolExecutor.execute(pollExecutionRunnable(
                    pollExecutionRequest(execArn)));
        };
    }

    private Runnable pollExecutionRunnable(PollExecutionRequest request) {
        return () -> {
            System.out.println("Polling " + request.executionArn());
            GetExecutionHistoryResponse response = stepFunctionInvoker.pollExecution(request.executionArn());
            if (TERMINAL_EXECUTION_STATE_TYPES.contains(response.events().get(0))) {
                System.out.println("Finished exec: " + request.executionArn());
            } else {
                threadPoolExecutor.execute(pollExecutionRunnable(
                        pollExecutionRequest(request.executionArn())));
            }
        };
    }

    private PollExecutionRequest pollExecutionRequest(String execArn) {
        return ImmutablePollExecutionRequest.builder()
                .executionArn(execArn)
                .lastChecked(Instant.now())
                .build();
    }


    private void pollForWork() {


    }

}
