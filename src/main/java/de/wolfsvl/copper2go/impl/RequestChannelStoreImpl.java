package de.wolfsvl.copper2go.impl;

import de.wolfsvl.copper2go.application.config.HttpRequestChannelConfig;
import de.wolfsvl.copper2go.connector.http.vertx.VertxHttpClient;
import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.workflowapi.RequestChannel;
import de.wolfsvl.copper2go.workflowapi.RequestChannelStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestChannelStoreImpl implements RequestChannelStore {
    private static Map<String, RequestChannel> requestChannelMap = new ConcurrentHashMap<>();

    public RequestChannelStoreImpl(final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs, final Copper2GoEngine engine) {
        for (String name : httpRequestChannelConfigs.keySet()) {
            HttpRequestChannelConfig config = httpRequestChannelConfigs.get(name);
            requestChannelMap.put(name,
                    new HttpChannelImpl(
                            config.getHttpMethod(),
                            new VertxHttpClient(
                                    config.host,
                                    config.port,
                                    config.path,
                                    engine
                            )));
        }
    }

    @Override
    public void request(final String channelName, final String request, final String responseCorrelationId) {
        requestChannelMap.get(channelName).request(request, responseCorrelationId);
    }


}
