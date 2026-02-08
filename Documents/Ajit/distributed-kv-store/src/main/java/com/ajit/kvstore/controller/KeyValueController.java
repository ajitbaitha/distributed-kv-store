package com.ajit.kvstore.controller;

import com.ajit.kvstore.cluster.ClusterService;
import com.ajit.kvstore.config.NodeConfig;
import com.ajit.kvstore.model.ValueResponse;
import com.ajit.kvstore.replication.ReplicationService;
import com.ajit.kvstore.service.KeyValueStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RestController
@RequestMapping("/kv")
public class KeyValueController {

    private final KeyValueStoreService service;
    private final ClusterService clusterService;
    private final ReplicationService replicationService;
    private final NodeConfig nodeConfig;
    private final WebClient webClient;

    public KeyValueController(KeyValueStoreService service,
                              ClusterService clusterService,
                              ReplicationService replicationService,
                              NodeConfig nodeConfig,
                              WebClient webClient) {

        this.service = service;
        this.clusterService = clusterService;
        this.replicationService = replicationService;
        this.nodeConfig = nodeConfig;
        this.webClient = webClient;
    }

    @PutMapping("/{key}")
    public ResponseEntity<Void> put(
            @PathVariable String key,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-REPLICA-WRITE", required = false) String replicaHeader) {

        String value = body.get("value");

        // Replica writes are terminal
        if ("true".equals(replicaHeader)) {
            service.put(key, value);
            return ResponseEntity.ok().build();
        }

        String ownerNodeId = clusterService.getOwnerNodeId(key);
        String currentNodeId = nodeConfig.getNodeId();

        if (!ownerNodeId.equals(currentNodeId)) {
            String ownerNodeUrl = clusterService.getOwnerNodeUrl(key);

            webClient.put()
                    .uri(ownerNodeUrl + "/kv/" + key)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return ResponseEntity.ok().build();
        }

        service.put(key, value);
        replicationService.replicate(key, value);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{key}")
    public ResponseEntity<ValueResponse> get(@PathVariable String key) {

        String ownerNodeId = clusterService.getOwnerNodeId(key);
        String currentNodeId = nodeConfig.getNodeId();

        if (ownerNodeId.equals(currentNodeId)) {
            if (!service.containsKey(key)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new ValueResponse(service.get(key)));
        }

        String ownerNodeUrl = clusterService.getOwnerNodeUrl(key);

        ValueResponse response = webClient.get()
                .uri(ownerNodeUrl + "/kv/" + key)
                .retrieve()
                .bodyToMono(ValueResponse.class)
                .block();

        return ResponseEntity.ok(response);
    }
}
