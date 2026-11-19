package com.bancomundi.pix_key_manager.service;

import com.bancomundi.pix_key_manager.domain.PixKeyRecord;
import com.bancomundi.pix_key_manager.domain.PixKeyType;
import com.bancomundi.pix_key_manager.repository.PixKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PixKeyServiceTests {

    @Mock
    private PixKeyRepository repository;

    private PixKeyService service;

    @BeforeEach
    void setUp() {
        service = new PixKeyService(repository, new PixKeyValidator());
    }

    @Test
    @DisplayName("Persiste e retorna o registro quando a chave é válida")
    void register_chaveValida_persisteERetornaRegistro() {
        PixKeyRecord saved = new PixKeyRecord(1L, PixKeyType.EMAIL, "fulano@exemplo.com", LocalDateTime.now());
        when(repository.save(PixKeyType.EMAIL, "fulano@exemplo.com")).thenReturn(saved);

        PixKeyRecord result = service.register(PixKeyType.EMAIL, "fulano@exemplo.com");

        assertEquals(saved, result);
        verify(repository).save(PixKeyType.EMAIL, "fulano@exemplo.com");
    }

    @Test
    @DisplayName("Não persiste quando o formato da chave é inválido")
    void register_formatoInvalido_naoPersiste() {
        assertThrows(InvalidPixKeyException.class,
                () -> service.register(PixKeyType.EMAIL, "fulano-sem-arroba"));

        verify(repository, never()).save(any(), anyString());
    }

    @Test
    @DisplayName("Não persiste quando o tipo é nulo")
    void register_tipoNulo_naoPersiste() {
        assertThrows(InvalidPixKeyException.class,
                () -> service.register(null, "11111111111"));

        verify(repository, never()).save(any(), anyString());
    }
}
