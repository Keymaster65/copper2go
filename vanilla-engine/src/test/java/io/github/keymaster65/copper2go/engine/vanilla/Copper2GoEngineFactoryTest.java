package io.github.keymaster65.copper2go.engine.vanilla;

import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;

class Copper2GoEngineFactoryTest {

    @Example
    void create() {
        Assertions.assertThatCode(() ->
                Copper2GoEngineFactory.create(
                        Mockito.mock(ReplyChannelStoreImpl.class),
                        Mockito.mock(RequestChannelStore.class),
                        Mockito.mock(EventChannelStore.class),
                        Mockito.mock(WorkflowStore.class)
                )
        ).doesNotThrowAnyException();
    }

}