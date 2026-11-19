package com.bancomundi.pix_key_manager.repository;

import com.bancomundi.pix_key_manager.domain.PixKeyRecord;
import com.bancomundi.pix_key_manager.domain.PixKeyType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Objects;

@Repository
public class JdbcPixKeyRepository implements PixKeyRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPixKeyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PixKeyRecord save(PixKeyType type, String value) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(
                    "INSERT INTO pix_key_records (key_type, key_value) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, type.name());
            statement.setString(2, value);
            return statement;
        }, keyHolder);

        Number generatedId = (Number) Objects.requireNonNull(
                keyHolder.getKeyList().getFirst().get("ID"),
                "Database did not return the generated record ID");
        long id = generatedId.longValue();
        return new PixKeyRecord(id, type, value, LocalDateTime.now());
    }
}
