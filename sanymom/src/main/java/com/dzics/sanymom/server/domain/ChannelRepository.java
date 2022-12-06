package com.dzics.sanymom.server.domain;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 把Channel存储 HashMap 中 ，方便查找
 */
public class ChannelRepository {
    private ConcurrentMap<String, Channel> channelCache = new ConcurrentHashMap<>();

    public void put(String key, Channel value) {
        channelCache.put(key, value);
    }

    public Channel get(String key) {
        return channelCache.get(key);
    }

    public void remove(String key) { this.channelCache.remove(key); }

    public int size() {
        return this.channelCache.size();
    }
}