package com.sfn.data;

import org.immutables.value.Value;

import java.time.Instant;

@Value.Immutable
@Value.Style(stagedBuilder = true)
public interface PollExecutionRequest {

    String executionArn();
    Instant lastChecked();
}

