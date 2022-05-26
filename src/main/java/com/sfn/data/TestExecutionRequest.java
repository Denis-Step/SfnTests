package com.sfn.data;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;
import software.amazon.awssdk.services.sfn.model.DescribeExecutionResponse;
import software.amazon.awssdk.services.sfn.model.GetExecutionHistoryResponse;

import java.util.function.BiFunction;
import java.util.function.Function;

@Value.Immutable
@Value.Style(stagedBuilder = true)
public interface TestExecutionRequest {

    String payload();

    @Nullable
    Function<GetExecutionHistoryResponse, Boolean> matcher();

    @Nullable
    BiFunction<GetExecutionHistoryResponse, DescribeExecutionResponse, Void> testFunction();

}
