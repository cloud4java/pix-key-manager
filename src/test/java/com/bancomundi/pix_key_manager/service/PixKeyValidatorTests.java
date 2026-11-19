package com.bancomundi.pix_key_manager.service;

import com.bancomundi.pix_key_manager.domain.PixKeyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PixKeyValidatorTests {

    private final PixKeyValidator validator = new PixKeyValidator();

    @DisplayName("Aceita valores em formatos válidos para cada tipo")
    @ParameterizedTest(name = "{0} com valor {1}")
    @CsvSource(delimiter = '|', value = {
            "CPF|11111111111",
            "CPF|111.111.111-11",
            "CNPJ|00000000000000",
            "CNPJ|00.000.000/0000-00",
            "EMAIL|fulano@exemplo.com",
            "PHONE|+5511999990000",
            "PHONE|(11) 99999-0000",
            "RANDOM|123e4567-e89b-12d3-a456-426614174000"
    })
    void validate_formatoValido_naoLancaExcecao(PixKeyType type, String value) {
        assertDoesNotThrow(() -> validator.validate(type, value));
    }

    @DisplayName("Rejeita valores fora do formato do tipo")
    @ParameterizedTest(name = "{0} inválido: {1}")
    @CsvSource(delimiter = '|', value = {
            "CPF|1111111111",
            "CPF|11111111111a",
            "CPF|111.111.111-1",
            "CNPJ|0000000000000",
            "CNPJ|00.000.000/0000-0",
            "EMAIL|fulano@exemplo",
            "EMAIL|fulano exemplo@x.com",
            "PHONE|1234567",
            "PHONE|abc12345678",
            "PHONE|(((((((((",
            "RANDOM|123e4567e89b12d3a456426614174000",
            "RANDOM|123e4567-e89b-12d3-a456-42661417400Z"
    })
    void validate_formatoInvalido_lancaExcecaoDeFormato(PixKeyType type, String value) {
        InvalidPixKeyException exception = assertThrows(InvalidPixKeyException.class,
                () -> validator.validate(type, value));
        assertEquals("O valor informado não corresponde ao formato do tipo de chave.", exception.getMessage());
    }

    @Test
    @DisplayName("Rejeita tipo nulo com mensagem de campo obrigatório")
    void validate_tipoNulo_lancaExcecao() {
        InvalidPixKeyException exception = assertThrows(InvalidPixKeyException.class,
                () -> validator.validate(null, "11111111111"));
        assertEquals("O tipo da chave é obrigatório.", exception.getMessage());
    }

    @ParameterizedTest(name = "valor vazio ou em branco: [{0}]")
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Rejeita valor vazio ou em branco com mensagem de campo obrigatório")
    void validate_valorVazioOuEmBranco_lancaExcecao(String value) {
        InvalidPixKeyException exception = assertThrows(InvalidPixKeyException.class,
                () -> validator.validate(PixKeyType.CPF, value));
        assertEquals("O valor da chave é obrigatório.", exception.getMessage());
    }

    @Test
    @DisplayName("Rejeita valor nulo com mensagem de campo obrigatório")
    void validate_valorNulo_lancaExcecao() {
        InvalidPixKeyException exception = assertThrows(InvalidPixKeyException.class,
                () -> validator.validate(PixKeyType.EMAIL, null));
        assertEquals("O valor da chave é obrigatório.", exception.getMessage());
    }
}
