package de.wolfsvl.copper2go.impl;

import de.wolfsvl.copper2go.connector.http.vertx.VertxHttpClient;
import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.workflowapi.RequestChannel;
import de.wolfsvl.copper2go.workflowapi.RequestChannelStore;
import io.vertx.core.http.HttpMethod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestChannelStoreImpl implements RequestChannelStore {
    private static Map<String, RequestChannel> eventChannelMap = new ConcurrentHashMap<>();

    public RequestChannelStoreImpl(final Copper2GoEngine engine) {
        store("Pricing.centPerMinute", new HttpChannelImpl(HttpMethod.GET, new VertxHttpClient("raw.githubusercontent.com", 80, "/Keymaster65/copper2go-workflows/feature/1.mapping/src/workflow/resources/1.txt", engine)));
    }

    public void store(String name, RequestChannel requestChannel) {
        eventChannelMap.put(name, requestChannel);
    }

    @Override
    public void request(final String channelName, final String request, final String responseCorrelationId) {
        eventChannelMap.get(channelName).request(request, responseCorrelationId);
    }


}
