package com.ajit.kvstore.cluster;

import com.ajit.kvstore.config.NodeConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClusterService {

    private final NodeConfig nodeConfig;
    private final List<String> nodes;

    public ClusterService(NodeConfig nodeConfig) {
        this.nodeConfig = nodeConfig;
        this.nodes = nodeConfig.getClusterNodes();
    }

    public String getOwnerNode(String key) {
        int hash = Math.abs(key.hashCode());
        int index = hash % nodes.size();
        return nodes.get(index);
    }

    public String getCurrentNodeUrl() {
        return "http://localhost:" + nodeConfig.getPort();
    }
}
