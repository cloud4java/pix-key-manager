package com.bancomundi.pix_key_manager.api;

import com.bancomundi.pix_key_manager.domain.PixKeyRecord;
import com.bancomundi.pix_key_manager.domain.PixKeyType;
import com.bancomundi.pix_key_manager.service.PixKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pix-keys")
public class PixKeyController {

    private final PixKeyService service;

    public PixKeyController(PixKeyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PixKeyResponse> register(@RequestBody CreatePixKeyRequest request) {
        PixKeyRecord record = service.register(request.type(), request.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(new PixKeyResponse(
                record.id(),
                record.type(),
                mask(record.type(), record.value()),
                "INTERNAL_RECORD"));
    }

    private String mask(PixKeyType type, String value) {
        return switch (type) {
            case CPF, CNPJ -> "***" + value.substring(value.length() - 2);
            case EMAIL -> "***@***";
            case PHONE -> "***" + value.substring(value.length() - 2);
            case RANDOM -> "***" + value.substring(value.length() - 4);
        };
    }
}
