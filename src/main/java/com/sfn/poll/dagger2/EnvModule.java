package com.sfn.poll.dagger2;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

@Module
public interface EnvModule {

    @Named("PULL_TX_SFN_ARN")
    @Provides
    static String providePullTxSfnArn() {
        return "arn:aws:states:us-east-2:397250182609:stateMachine:PullTxStateMachinePullTransactionsStateMachine71A6EE1C-HwAiTPssR6MA";
    }

}
