package com.bancomundi.pix_key_manager.api;

import com.bancomundi.pix_key_manager.domain.PixKeyType;

public record PixKeyResponse(long id, PixKeyType type, String maskedValue, String status) {
}
