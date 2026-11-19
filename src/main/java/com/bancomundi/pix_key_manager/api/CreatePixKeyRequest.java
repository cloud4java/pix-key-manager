package com.bancomundi.pix_key_manager.api;

import com.bancomundi.pix_key_manager.domain.PixKeyType;

public record CreatePixKeyRequest(PixKeyType type, String value) {
}
