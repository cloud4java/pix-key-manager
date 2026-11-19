package com.bancomundi.pix_key_manager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PixKeyRegistrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registersKeyAsInternalRecordAndMasksTheValue() throws Exception {
        mockMvc.perform(post("/api/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"CPF","value":"12345678901"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.type").value("CPF"))
                .andExpect(jsonPath("$.maskedValue").value("***01"))
                .andExpect(jsonPath("$.maskedValue", not(containsString("12345678901"))))
                .andExpect(jsonPath("$.status").value("INTERNAL_RECORD"));
    }

    @Test
    void rejectsInvalidKeyFormatWithoutCreatingRecord() throws Exception {
        mockMvc.perform(post("/api/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"EMAIL","value":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "O valor informado não corresponde ao formato do tipo de chave."));
    }

    @Test
    void rejectsMissingRequiredValue() throws Exception {
        mockMvc.perform(post("/api/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"PHONE","value":" "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("O valor da chave é obrigatório."));
    }

    @Test
    void rejectsUnknownKeyType() throws Exception {
        mockMvc.perform(post("/api/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"UNKNOWN","value":"value"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }
}
