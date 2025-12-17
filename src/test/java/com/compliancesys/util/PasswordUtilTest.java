package com.compliancesys.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.compliancesys.util.impl.PasswordUtilImpl;

class PasswordUtilTest {

    private PasswordUtil passwordUtil;

    @BeforeEach
    void setUp() {
        passwordUtil = new PasswordUtilImpl();
    }

    // ==================== Testes de hashPassword ====================

    @Test
    @DisplayName("Deve gerar hash para senha válida")
    void hashPassword_ValidPassword_ReturnsHash() {
        String password = "SenhaSegura123!";

        String hash = passwordUtil.hashPassword(password);

        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
    }

    @Test
    @DisplayName("Deve gerar hashes diferentes para mesma senha")
    void hashPassword_SamePassword_ReturnsDifferentHashes() {
        String password = "SenhaSegura123!";

        String hash1 = passwordUtil.hashPassword(password);
        String hash2 = passwordUtil.hashPassword(password);

        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Deve retornar null para senha nula")
    void hashPassword_NullPassword_ReturnsNull() {
        String hash = passwordUtil.hashPassword(null);
        assertNull(hash);
    }

    @Test
    @DisplayName("Deve retornar null para senha vazia")
    void hashPassword_EmptyPassword_ReturnsNull() {
        String hash = passwordUtil.hashPassword("");
        assertNull(hash);
    }

    @Test
    @DisplayName("Deve gerar hash para senha com caracteres especiais")
    void hashPassword_SpecialCharacters_ReturnsHash() {
        String password = "Senh@#$%^&*()123!";

        String hash = passwordUtil.hashPassword(password);

        assertNotNull(hash);
    }

    @Test
    @DisplayName("Deve gerar hash para senha longa")
    void hashPassword_LongPassword_ReturnsHash() {
        String password = "UmaSenhaMuitoLongaQueTemMaisDe50CaracteresParaTestar123!";

        String hash = passwordUtil.hashPassword(password);

        assertNotNull(hash);
    }

    // ==================== Testes de checkPassword ====================

    @Test
    @DisplayName("Deve verificar senha correta")
    void checkPassword_CorrectPassword_ReturnsTrue() {
        String password = "SenhaSegura123!";
        String hash = passwordUtil.hashPassword(password);

        boolean result = passwordUtil.checkPassword(password, hash);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve rejeitar senha incorreta")
    void checkPassword_IncorrectPassword_ReturnsFalse() {
        String password = "SenhaSegura123!";
        String hash = passwordUtil.hashPassword(password);

        boolean result = passwordUtil.checkPassword("SenhaErrada456!", hash);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false para senha nula")
    void checkPassword_NullPlainPassword_ReturnsFalse() {
        String hash = passwordUtil.hashPassword("SenhaSegura123!");

        boolean result = passwordUtil.checkPassword(null, hash);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false para hash nulo")
    void checkPassword_NullHash_ReturnsFalse() {
        boolean result = passwordUtil.checkPassword("SenhaSegura123!", null);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar false para ambos nulos")
    void checkPassword_BothNull_ReturnsFalse() {
        boolean result = passwordUtil.checkPassword(null, null);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve verificar senha com caracteres especiais")
    void checkPassword_SpecialCharacters_ReturnsTrue() {
        String password = "Senh@#$%^&*()123!";
        String hash = passwordUtil.hashPassword(password);

        boolean result = passwordUtil.checkPassword(password, hash);

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve diferenciar maiúsculas e minúsculas")
    void checkPassword_CaseSensitive_ReturnsFalse() {
        String password = "SenhaSegura123!";
        String hash = passwordUtil.hashPassword(password);

        boolean result = passwordUtil.checkPassword("senhasegura123!", hash);

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve verificar senha com espaços")
    void checkPassword_WithSpaces_ReturnsTrue() {
        String password = "Senha Com Espaços 123!";
        String hash = passwordUtil.hashPassword(password);

        boolean result = passwordUtil.checkPassword(password, hash);

        assertTrue(result);
    }

    // ==================== Testes de Ida e Volta ====================

    @Test
    @DisplayName("Deve funcionar corretamente em ciclo hash-verify")
    void roundTrip_HashAndVerify_WorksCorrectly() {
        String[] passwords = {
            "simple",
            "Complex123!",
            "WithSpaces Here",
            "Símbolos@#$%",
            "12345678",
            "VeryLongPasswordThatExceedsNormalLengthsUsedInMostApplications!"
        };

        for (String password : passwords) {
            String hash = passwordUtil.hashPassword(password);
            assertTrue(passwordUtil.checkPassword(password, hash), 
                "Falha para senha: " + password);
        }
    }
}