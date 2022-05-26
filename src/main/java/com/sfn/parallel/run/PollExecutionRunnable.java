//package com.sfn.parallel.run;
//
//import com.sfn.clients.StepFunctionInvoker;
//
//public final class PollExecutionRunnable implements Runnable {
//
//    private final StepFunctionInvoker invoker;
//    private final String payload;
//
//    public StartExecutionRunnable(StepFunctionInvoker invoker,
//                                  String payload) {
//        this.invoker = invoker;
//        this.payload = payload;
//    }
//
//    @Override
//    public void run() {
//        this.invoker.invoke(payload);
//    }
//
//
//
//}
