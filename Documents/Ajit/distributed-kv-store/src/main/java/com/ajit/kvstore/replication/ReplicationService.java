package com.ajit.kvstore.replication;

import com.ajit.kvstore.cluster.ClusterService;
import com.ajit.kvstore.config.NodeConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ReplicationService {

    private final ClusterService clusterService;
    private final NodeConfig nodeConfig;
    private final WebClient webClient;

    public ReplicationService(ClusterService clusterService,
                              NodeConfig nodeConfig,
                              WebClient webClient) {
        this.clusterService = clusterService;
        this.nodeConfig = nodeConfig;
        this.webClient = webClient;
    }

    public void replicate(String key, String value) {

        String replicaNodeId = clusterService.getReplicaNodeId(key);
        String currentNodeId = nodeConfig.getNodeId();

        if (replicaNodeId.equals(currentNodeId)) {
            return;
        }

        String replicaNodeUrl = clusterService.getReplicaNodeUrl(key);

        webClient.put()
                .uri(replicaNodeUrl + "/internal/replica/" + key)
                .header("X-REPLICA-WRITE", "true")
                .bodyValue(Map.of("value", value))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
