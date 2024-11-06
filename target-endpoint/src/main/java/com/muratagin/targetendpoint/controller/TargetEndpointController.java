package com.muratagin.targetendpoint.controller;

import com.muratagin.targetendpoint.model.TargetEndpointModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/target")
public class TargetEndpointController {

    @PostMapping
    public ResponseEntity<?> targetEndpoint(@RequestBody TargetEndpointModel targetEndpointModel,
                                            @RequestHeader("Authorization") String token,
                                            @RequestHeader("Correlation-Id") String correlationId) {
        return ResponseEntity.ok(targetEndpointModel.toString());
    }

}
