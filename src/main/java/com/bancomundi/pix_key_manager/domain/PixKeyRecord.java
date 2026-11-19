package com.bancomundi.pix_key_manager.domain;

import java.time.LocalDateTime;

public record PixKeyRecord(
        long id,
        PixKeyType type,
        String value,
        LocalDateTime createdAt) {
}
