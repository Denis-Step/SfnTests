package com.sfn.clients;

import com.sfn.data.ImmutablePollExecutionRequest;
import com.sfn.data.TestExecutionRequest;
import com.sfn.data.PollExecutionRequest;
import com.sfn.match.sfn.SfnSuccessMatcher;
import software.amazon.awssdk.services.sfn.model.DescribeExecutionResponse;
import software.amazon.awssdk.services.sfn.model.GetExecutionHistoryResponse;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class SfnExecutionRunner {

  private static final long DEFAULT_POLL_DELAY_MS = 1000L;
  private static final long DEFAULT_STAGGER_MS = 500L;

  private final StepFunctionInvoker stepFunctionInvoker;
  private final ThreadPoolExecutor threadPoolExecutor;

  // Still subject to memory inconsistency issues with interleaved API Calls. This is expected to be fixed with a new
  // internal API for pooling and scheduling requests.
  private volatile Instant lastApiCall;

  @Inject
  public SfnExecutionRunner(StepFunctionInvoker stepFunctionInvoker) {
    this.stepFunctionInvoker = stepFunctionInvoker;
    this.threadPoolExecutor = new ThreadPoolExecutor(10, 20,
        20000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
  }

  public void runExecutions(List<TestExecutionRequest> requests) {
    requests.stream()
        .map(this::startExecutionRunnable)
        .forEach(threadPoolExecutor::execute);
  }

  private Runnable startExecutionRunnable(TestExecutionRequest request) {
    return () -> {
      checkTime();
      System.out.println("Invoking SFN");
      String execArn = stepFunctionInvoker.invoke(request.payload());

      // Need two variables because the time of the api call needs to be used in this execution later, but might
      // be modified by another thread before the next pollExecutionRequest is built.
      Instant apiCallFinished = Instant.now();
      lastApiCall = apiCallFinished;

      threadPoolExecutor.execute(pollExecutionRunnable(
          pollExecutionRequest(execArn, apiCallFinished, request)));
    };
  }

  private Runnable pollExecutionRunnable(PollExecutionRequest request) {
    return () -> {
      checkTime();
      System.out.println("Polling " + request.executionArn());
      GetExecutionHistoryResponse response = stepFunctionInvoker
          .pollExecution(request.executionArn());

      // Need two variables because the time of the api call needs to be used in this execution later, but might
      // be modified by another thread before the next pollExecutionRequest is built.
      Instant apiCallFinished = Instant.now();
      lastApiCall = apiCallFinished;

      if (runMatcher(response, request.testExecutionRequest().matcher())) {
        System.out.println("Matcher successful for exec: " + request.executionArn());
        runTestFunction(response, request.testExecutionRequest().testFunction());
      } else {
        if (new SfnSuccessMatcher().apply(response)) {
        }

        threadPoolExecutor
            .execute(pollExecutionRunnable(ImmutablePollExecutionRequest.copyOf(request)
                .withLastChecked(apiCallFinished)));
      }
    };
  }

  private boolean runMatcher(GetExecutionHistoryResponse historyResponse,
      Function<GetExecutionHistoryResponse, Boolean> matcherFunction) {
    return matcherFunction.apply(historyResponse);
  }

  private void runTestFunction(GetExecutionHistoryResponse historyResponse,
      BiConsumer<GetExecutionHistoryResponse, DescribeExecutionResponse> testFunction) {
    testFunction.accept(historyResponse, null);
  }

  private PollExecutionRequest pollExecutionRequest(String execArn,
      Instant lastChecked,
      TestExecutionRequest testExecutionRequest) {
    return ImmutablePollExecutionRequest.builder()
        .testExecutionRequest(testExecutionRequest)
        .executionArn(execArn)
        .lastChecked(lastChecked)
        .build();
  }

  private void checkTime() {
    if (lastApiCall == null) {
      return;
    }

    if (Instant.now().minus(DEFAULT_STAGGER_MS, ChronoUnit.MILLIS).isBefore(lastApiCall)) {
      try {
        Thread.sleep(DEFAULT_STAGGER_MS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      checkTime();
    }
  }

}
