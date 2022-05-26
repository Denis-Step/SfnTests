package com.sfn.poll.dagger2;

import com.sfn.clients.SfnExecutionRunner;
import dagger.Component;

@Component(modules = {
        EnvModule.class,
        SfnClientModule.class})
public interface MainComponent {
    SfnExecutionRunner createSfnPoller();
}
