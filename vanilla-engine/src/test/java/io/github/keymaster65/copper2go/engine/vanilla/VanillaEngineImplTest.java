package io.github.keymaster65.copper2go.engine.vanilla;


import io.github.keymaster65.copper2go.api.workflow.EventChannelStore;
import io.github.keymaster65.copper2go.api.workflow.RequestChannelStore;
import io.github.keymaster65.copper2go.engine.ReplyChannelStoreImpl;
import net.jqwik.api.Example;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

class VanillaEngineImplTest {

    public static final String UUID = "uuid";
    public static final String REPLY = "reply";
    public static final String CHANNEL_NAME = "channelName";
    public static final String REQUEST = "request";
    public static final String CORRELATIONID = "correlationid";
    public static final String RESPONSE = "response";


    @Example
    void event() {
        final EventChannelStore eventChannelStore = Mockito.mock(EventChannelStore.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                eventChannelStore,
                Mockito.mock(ExecutorService.class),
                Mockito.mock(ContinuationStore.class)
        );

        engine.event(UUID, REPLY);

        Mockito.verify(eventChannelStore).event(UUID, REPLY);
    }

    @Example
    void request() {
        final RequestChannelStore requestChannelStore = Mockito.mock(RequestChannelStore.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                requestChannelStore,
                Mockito.mock(EventChannelStore.class),
                Mockito.mock(ExecutorService.class),
                Mockito.mock(ContinuationStore.class)
        );

        engine.request(CHANNEL_NAME, REQUEST);

        Mockito.verify(requestChannelStore).request(Mockito.eq(CHANNEL_NAME), Mockito.eq(REQUEST), Mockito.anyString());
    }

    @Example
    void reply() {
        final ReplyChannelStoreImpl replyChannelStore = Mockito.mock(ReplyChannelStoreImpl.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                replyChannelStore,
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                Mockito.mock(ExecutorService.class),
                Mockito.mock(ContinuationStore.class)
        );

        engine.reply(UUID, REPLY);

        Mockito.verify(replyChannelStore).reply(UUID, REPLY);
    }

    @Example
    void continueAsync() {
        final ContinuationStore continuationStore = Mockito.mock(ContinuationStore.class);
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                Mockito.mock(ExecutorService.class),
                continuationStore
        );
        @SuppressWarnings("unchecked") final Consumer<String> consumer = Mockito.mock(Consumer.class);

        engine.continueAsync(CORRELATIONID, consumer);

        Mockito.verify(continuationStore).addExpectedResponse(CORRELATIONID, new Continuation(consumer));
    }

    @Example
    void continueAsyncEarlyResponse() {
        final ContinuationStore continuationStore = Mockito.mock(ContinuationStore.class);
        final ExecutorService executorService = ExecutorServices.start();
        final VanillaEngineImpl engine = new VanillaEngineImpl(
                Mockito.mock(ReplyChannelStoreImpl.class),
                Mockito.mock(RequestChannelStore.class),
                Mockito.mock(EventChannelStore.class),
                executorService,
                continuationStore
        );
        @SuppressWarnings("unchecked") final Consumer<String> consumer = Mockito.mock(Consumer.class);
        final Continuation continuation = new Continuation(consumer);
        final Continuation earlyResponseContinuation = new Continuation(RESPONSE);
        Mockito
                .when(continuationStore.addExpectedResponse(CORRELATIONID, continuation))
                .thenReturn(earlyResponseContinuation);

        engine.continueAsync(CORRELATIONID, consumer);
        ExecutorServices.stop(executorService);

        Mockito.verify(continuationStore).addExpectedResponse(CORRELATIONID, continuation);
        Mockito.verify(continuationStore).removeExpectedResponse(CORRELATIONID);
        Mockito.verify(continuationStore).addFuture(Mockito.any(), Mockito.eq(earlyResponseContinuation));
        Mockito.verify(consumer).accept(RESPONSE);
    }
}