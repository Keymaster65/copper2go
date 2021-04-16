package de.wolfsvl.copper2go.impl;

import de.wolfsvl.copper2go.workflowapi.EventChannelStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventChannelStoreImpl implements EventChannelStore {
    private static Map<String, EventChannel> eventChannelMap = new ConcurrentHashMap<>();

    public EventChannelStoreImpl() {
        eventChannelMap.put("System.stdout", new StdOutEventChannelImpl());
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
