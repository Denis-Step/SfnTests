package com.sfn.poll.dagger2;

import com.sfn.clients.StepFunctionInvoker;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sfn.SfnClient;

import javax.inject.Named;
import java.util.concurrent.ThreadPoolExecutor;

@Module
public interface SfnClientModule {

    @Provides
    static AwsCredentialsProvider provideAwsCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }

    @Provides
    static SfnClient provideSfnClient(AwsCredentialsProvider awsCredentialsProvider) {
        return SfnClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Provides
    static StepFunctionInvoker providePullTxInvoker(SfnClient sfnClient,
                                                    @Named("PULL_TX_SFN_ARN") String pullTxSfnArn) {
        return new StepFunctionInvoker(sfnClient, pullTxSfnArn);
    }


}
