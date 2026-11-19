package com.bancomundi.pix_key_manager.service;

import com.bancomundi.pix_key_manager.domain.PixKeyType;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PixKeyValidator {

    private static final Pattern CPF = Pattern.compile("(?:\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})");
    private static final Pattern CNPJ = Pattern.compile("(?:\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9() .-]{8,20}$");
    private static final Pattern RANDOM = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public void validate(PixKeyType type, String value) {
        if (type == null) {
            throw new InvalidPixKeyException("O tipo da chave é obrigatório.");
        }
        if (value == null || value.isBlank()) {
            throw new InvalidPixKeyException("O valor da chave é obrigatório.");
        }

        boolean valid = switch (type) {
            case CPF -> CPF.matcher(value).matches();
            case CNPJ -> CNPJ.matcher(value).matches();
            case EMAIL -> EMAIL.matcher(value).matches();
            case PHONE -> PHONE.matcher(value).matches()
                    && value.chars().filter(Character::isDigit).count() >= 8;
            case RANDOM -> RANDOM.matcher(value).matches();
        };
        if (!valid) {
            throw new InvalidPixKeyException("O valor informado não corresponde ao formato do tipo de chave.");
        }
    }
}
