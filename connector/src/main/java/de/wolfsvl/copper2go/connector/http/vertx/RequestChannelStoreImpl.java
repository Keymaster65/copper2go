package de.wolfsvl.copper2go.connector.http.vertx;

import de.wolfsvl.copper2go.connector.http.HttpRequestChannelConfig;
import de.wolfsvl.copper2go.engine.Copper2GoEngine;
import de.wolfsvl.copper2go.impl.RequestChannel;
import de.wolfsvl.copper2go.workflowapi.RequestChannelStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestChannelStoreImpl implements RequestChannelStore {
    private static Map<String, RequestChannel> requestChannelMap = new ConcurrentHashMap<>();

    public RequestChannelStoreImpl(final Map<String, HttpRequestChannelConfig> httpRequestChannelConfigs, final Copper2GoEngine engine) {
        for (Map.Entry<String, HttpRequestChannelConfig> entry : httpRequestChannelConfigs.entrySet()) {
            HttpRequestChannelConfig config = entry.getValue();
            requestChannelMap.put(entry.getKey(),
                    new HttpRequestChannelImpl(
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
