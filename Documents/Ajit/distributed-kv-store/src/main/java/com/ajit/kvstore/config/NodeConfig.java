package com.ajit.kvstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class NodeConfig {

    @Value("${node.id}")
    private String nodeId;

    @Value("${cluster.nodes}")
    private List<String> clusterNodes;

    @Value("${server.port}")
    private int port;

    public String getNodeId() {
        return nodeId;
    }

    public List<String> getClusterNodes() {
        return clusterNodes;
    }

    public int getPort() {
        return port;
    }
}
