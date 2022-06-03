package com.sfn.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
import software.amazon.awssdk.services.sfn.model.StartExecutionResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public final class StepFunctionInvokerTest {

  private static final String SFN_ARN = "SFN_ARN";

  @Mock
  private SfnClient sfnClient;

  private StepFunctionInvoker stepFunctionInvoker;

  public StepFunctionInvokerTest() {
    this.stepFunctionInvoker = new StepFunctionInvoker(sfnClient, SFN_ARN);
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    this.stepFunctionInvoker = new StepFunctionInvoker(sfnClient, SFN_ARN);
  }

  @Test
  public void newExecutionRequestWithNamePassedToSfnClient() {
    String payload = "{}";
    String executionName = "EXECUTION_NAME";
    StartExecutionRequest request = createStartExecutionRequest(payload, executionName);
    StartExecutionResponse response = mock(StartExecutionResponse.class);
    when(response.executionArn()).thenReturn("EXEC_ARN");

    when(sfnClient.startExecution(eq(request))).thenReturn(response);
    ArgumentCaptor<StartExecutionRequest> argumentCaptor = ArgumentCaptor
        .forClass(StartExecutionRequest.class);
    stepFunctionInvoker.invoke(payload, executionName);
    verify(sfnClient).startExecution(argumentCaptor.capture());

    assertEquals(createStartExecutionRequest(payload, executionName), argumentCaptor.getValue());
  }

  @Test
  public void newExecutionRequestWithNoNamePassedToSfnClient() {
    String payload = "{}";
    StartExecutionResponse response = mock(StartExecutionResponse.class);
    when(response.executionArn()).thenReturn("EXEC_ARN");

    when(sfnClient.startExecution((StartExecutionRequest) any())).thenReturn(response);

    ArgumentCaptor<StartExecutionRequest> argumentCaptor = ArgumentCaptor
        .forClass(StartExecutionRequest.class);
    stepFunctionInvoker.invoke(payload);
    verify(sfnClient).startExecution(argumentCaptor.capture());

    assertEquals(payload, argumentCaptor.getValue().input());
  }

  private StartExecutionRequest createStartExecutionRequest(String input, String name) {
    return StartExecutionRequest.builder()
        .stateMachineArn(this.SFN_ARN)
        .name(name)
        .input(input)
        .build();
  }

}
