package com.ajit.kvstore.controller;

import com.ajit.kvstore.cluster.ClusterService;
import com.ajit.kvstore.config.NodeConfig;
import com.ajit.kvstore.model.ValueResponse;
import com.ajit.kvstore.service.KeyValueStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Map;

@RestController
@RequestMapping("/kv")
public class KeyValueController {

    private final KeyValueStoreService service;
    private final NodeConfig nodeConfig;
    private final ClusterService clusterService;
    private final WebClient webClient;

    public KeyValueController(KeyValueStoreService service,
                              NodeConfig nodeConfig,
                              ClusterService clusterService,
                              WebClient webClient) {
        this.service = service;
        this.nodeConfig = nodeConfig;
        this.clusterService = clusterService;
        this.webClient = webClient;
    }


    @PutMapping("/{key}")
    public ResponseEntity<Void> put(@PathVariable String key,
                                    @RequestBody Map<String, String> body) {

        String value = body.get("value");
        String ownerNode = clusterService.getOwnerNode(key);
        String currentNode = clusterService.getCurrentNodeUrl();

        if (ownerNode.equals(currentNode)) {
            service.put(key, value);
            return ResponseEntity.ok().build();
        }

        // Forward PUT to owner node
        webClient.put()
                .uri(ownerNode + "/kv/" + key)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{key}")
    public ResponseEntity<ValueResponse> get(@PathVariable String key) {

        String ownerNode = clusterService.getOwnerNode(key);
        String currentNode = "http://localhost:" + nodeConfig.getPort();

        // If this node is the owner
        if (ownerNode.equals(currentNode)) {
            if (!service.containsKey(key)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(
                    new ValueResponse(service.get(key))
            );
        }

        // Forward GET request to owner node
        ValueResponse response = webClient.get()
                .uri(ownerNode + "/kv/" + key)
                .retrieve()
                .bodyToMono(ValueResponse.class)
                .block();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/node/info")
    public Map<String, Object> nodeInfo() {
        return Map.of(
                "nodeId", nodeConfig.getNodeId(),
                "clusterNodes", nodeConfig.getClusterNodes()
        );
    }

}
