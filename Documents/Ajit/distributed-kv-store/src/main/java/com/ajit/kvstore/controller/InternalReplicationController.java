package com.ajit.kvstore.controller;

import com.ajit.kvstore.service.KeyValueStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/replica")
public class InternalReplicationController {

    private final KeyValueStoreService service;

    public InternalReplicationController(KeyValueStoreService service) {
        this.service = service;
    }

    @PutMapping("/{key}")
    public ResponseEntity<Void> replicate(
            @PathVariable String key,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-REPLICA-WRITE") String ignored) {

        service.put(key, body.get("value"));
        return ResponseEntity.ok().build();
    }
}
