package com.ajit.kvstore.cluster;

import com.ajit.kvstore.config.NodeConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClusterService {

    private final List<String> nodeUrls;
    private final List<String> nodeIds;
    private final NodeConfig nodeConfig;

    public ClusterService(NodeConfig nodeConfig) {
        this.nodeConfig = nodeConfig;

        // MUST be same order everywhere
        this.nodeUrls = nodeConfig.getClusterNodes();
        this.nodeIds = List.of("node-1", "node-2", "node-3");
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % nodeUrls.size();
    }

    public String getOwnerNodeId(String key) {
        return nodeIds.get(hash(key));
    }

    public String getOwnerNodeUrl(String key) {
        return nodeUrls.get(hash(key));
    }

    public String getReplicaNodeId(String key) {
        return nodeIds.get((hash(key) + 1) % nodeUrls.size());
    }

    public String getReplicaNodeUrl(String key) {
        return nodeUrls.get((hash(key) + 1) % nodeUrls.size());
    }

    public String getCurrentNodeId() {
        return nodeConfig.getNodeId();
    }
}
