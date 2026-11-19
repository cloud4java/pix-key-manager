package com.bancomundi.pix_key_manager.service;

import com.bancomundi.pix_key_manager.domain.PixKeyRecord;
import com.bancomundi.pix_key_manager.domain.PixKeyType;
import com.bancomundi.pix_key_manager.repository.PixKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PixKeyService {

    private final PixKeyRepository repository;
    private final PixKeyValidator validator;

    public PixKeyService(PixKeyRepository repository, PixKeyValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Transactional
    public PixKeyRecord register(PixKeyType type, String value) {
        validator.validate(type, value);
        return repository.save(type, value);
    }
}
