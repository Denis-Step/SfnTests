package com.sfn.parallel;

import com.sfn.data.PollExecutionRequest;

public final class SfnTaskMonitor {

    public void publish(PollExecutionRequest pollExecutionRequest) {
        System.out.println("Successful invoc: " + pollExecutionRequest);
    }
}
