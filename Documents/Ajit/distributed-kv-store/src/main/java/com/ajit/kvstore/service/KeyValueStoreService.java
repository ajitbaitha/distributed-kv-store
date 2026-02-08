package com.ajit.kvstore.service;

import com.ajit.kvstore.config.NodeConfig;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class KeyValueStoreService {

    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();
    private final NodeConfig nodeConfig;

    public KeyValueStoreService(NodeConfig nodeConfig) {
        this.nodeConfig = nodeConfig;
    }

    public void put(String key, String value) {
        System.out.println("STORE on " + nodeConfig.getNodeId());
        store.put(key, value);
    }

    public String get(String key) {
        return store.get(key);
    }

    public boolean containsKey(String key) {
        return store.containsKey(key);
    }
}
