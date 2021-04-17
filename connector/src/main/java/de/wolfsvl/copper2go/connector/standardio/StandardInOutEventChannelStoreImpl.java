package de.wolfsvl.copper2go.connector.standardio;

import de.wolfsvl.copper2go.engine.EventChannel;
import de.wolfsvl.copper2go.workflowapi.EventChannelStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardInOutEventChannelStoreImpl implements EventChannelStore {
    private static Map<String, EventChannel> eventChannelMap = new ConcurrentHashMap<>();

    public StandardInOutEventChannelStoreImpl() {
        eventChannelMap.put("System.stdout", new StandardOutEventChannelImpl());
    }

    @Override
    public void event(final String channelName, final String event) {
        eventChannelMap.get(channelName).event(event);
    }

    @Override
    public void errorEvent(final String channelName, final String event) {
        eventChannelMap.get(channelName).errorEvent(event);
    }
}
