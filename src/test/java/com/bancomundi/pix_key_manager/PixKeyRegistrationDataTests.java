package com.bancomundi.pix_key_manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class PixKeyRegistrationDataTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ParameterizedTest(name = "{0} mascarado como {2}")
    @CsvSource({
            "CPF,11111111111,***11",
            "CNPJ,00000000000000,***00",
            "EMAIL,fulano@exemplo.com,***@***",
            "PHONE,+5511999990000,***00",
            "RANDOM,123e4567-e89b-12d3-a456-426614174000,***4000"
    })
    @DisplayName("Mascara o valor de acordo com o tipo sem expô-lo na resposta")
    void register_tipoValido_mascaraValorSemExpo(String type, String value, String expectedMask) throws Exception {
        mockMvc.perform(post("/api/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"%s","value":"%s"}
                                """.formatted(type, value)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(type))
                .andExpect(jsonPath("$.maskedValue").value(expectedMask))
                .andExpect(jsonPath("$.status").value("INTERNAL_RECORD"))
                .andExpect(jsonPath("$.value").doesNotExist())
                .andExpect(jsonPath("$.maskedValue", not(containsString(value))));
    }

    @Test
    @DisplayName("Persiste o tipo e o valor original da chave no banco interno")
    void register_chaveValida_persisteRegistroNoBanco() throws Exception {
        mockMvc.perform(post("/api/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"CNPJ","value":"00.000.000/0000-00"}
                                """))
                .andExpect(status().isCreated());

        String storedType = jdbcTemplate.queryForObject(
                "SELECT key_type FROM pix_key_records WHERE key_value = ?",
                String.class,
                "00.000.000/0000-00");
        assertEquals("CNPJ", storedType);
    }

    @Test
    @DisplayName("Não cria registro no banco quando a validação falha")
    void register_chaveInvalida_naoPersisteRegistro() throws Exception {
        Integer before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pix_key_records", Integer.class);

        mockMvc.perform(post("/api/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"CPF","value":"123.456.789-0"}
                                """))
                .andExpect(status().isBadRequest());

        Integer after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pix_key_records", Integer.class);
        assertEquals(before, after);
    }

    @Test
    @DisplayName("Rejeita requisição sem campo tipo com mensagem de campo obrigatório")
    void register_semTipo_retornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":"11111111111"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("O tipo da chave é obrigatório."));
    }
}
