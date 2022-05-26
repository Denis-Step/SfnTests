//package com.sfn.parallel.run;
//
//import com.sfn.clients.SfnExecutionRunner;
//import com.sfn.clients.StepFunctionInvoker;
//import com.sfn.data.ImmutablePollExecutionRequest;
//import com.sfn.data.PollExecutionRequest;
//
//import java.time.Instant;
//
//public final class StartExecutionRunnable implements Runnable {
//
//    private final SfnExecutionRunner runner;
//    private final String payload;
//
//    public StartExecutionRunnable(StepFunctionInvoker invoker,
//                                  String payload,) {
//        this.runner = runner;
//        this.payload = payload;
//    }
//
//    @Override
//    public void run() {
//        String execArn = this.runner.newExecution(payload);
//        PollExecutionRequest newInvocationStatus = newInvocationStatus(execArn);
//        monitor.publish(newInvocationStatus);
//    }
//
//    private PollExecutionRequest newInvocationStatus(String executionArn) {
//        return ImmutablePollExecutionRequest.builder()
//                .executionArn(executionArn)
//                .lastChecked(Instant.now())
//                .complete(false)
//                .build();
//    }
//
//}
