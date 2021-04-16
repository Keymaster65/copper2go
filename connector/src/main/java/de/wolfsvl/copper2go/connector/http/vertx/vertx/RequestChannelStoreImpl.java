package de.wolfsvl.copper2go.connector.http.vertx.vertx;

import de.wolfsvl.copper2go.connector.http.vertx.HttpRequestChannelConfig;
import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.impl.RequestChannel;
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
                            config.method,
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
