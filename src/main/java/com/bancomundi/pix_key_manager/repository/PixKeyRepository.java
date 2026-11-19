package com.bancomundi.pix_key_manager.repository;

import com.bancomundi.pix_key_manager.domain.PixKeyRecord;
import com.bancomundi.pix_key_manager.domain.PixKeyType;

public interface PixKeyRepository {

    PixKeyRecord save(PixKeyType type, String value);
}
