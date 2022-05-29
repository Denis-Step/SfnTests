package com.sfn.data;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;
import software.amazon.awssdk.services.sfn.model.DescribeExecutionResponse;
import software.amazon.awssdk.services.sfn.model.GetExecutionHistoryResponse;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Value.Immutable
@Value.Style(stagedBuilder = true)
public interface TestExecutionRequest {

    String payload();

    Function<GetExecutionHistoryResponse, Boolean> matcher();

    BiConsumer<GetExecutionHistoryResponse, @Nullable DescribeExecutionResponse> testFunction();

}
