package com.compliancesys.util;

import com.compliancesys.util.impl.PasswordUtilImpl; // Importa a implementação real
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt; // Usando jBCrypt para verificação, se a implementação real usar

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    private PasswordUtil passwordUtil;

    @BeforeEach
    void setUp() {
        // Instancia a implementação real da interface PasswordUtil
        passwordUtil = new PasswordUtilImpl();
    }

    @Test
    @DisplayName("Deve gerar um hash de senha válido")
    void testHashPasswordSuccess() {
        String password = "minhaSenhaSecreta123";
        String hashedPassword = passwordUtil.hashPassword(password);

        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isEmpty());
        // Verifica se o hash gerado é um hash BCrypt válido (começa com $2a$, $2b$ ou $2y$)
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$"));
        // Verifica se o hash pode ser verificado pelo próprio BCrypt (se a implementação usar BCrypt)
        assertTrue(BCrypt.checkpw(password, hashedPassword));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para senha nula ao fazer hash")
    void testHashPasswordWithNullPassword() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordUtil.hashPassword(null);
        });
        assertEquals("A senha não pode ser nula ou vazia.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para senha vazia ao fazer hash")
    void testHashPasswordWithEmptyPassword() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordUtil.hashPassword("");
        });
        assertEquals("A senha não pode ser nula ou vazia.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve verificar uma senha correta com sucesso")
    void testVerifyPasswordSuccess() {
        String password = "minhaSenhaSecreta123";
        String hashedPassword = passwordUtil.hashPassword(password); // Gerar um hash válido

        assertTrue(passwordUtil.verifyPassword(password, hashedPassword));
    }

    @Test
    @DisplayName("Não deve verificar uma senha incorreta")
    void testVerifyPasswordFailure() {
        String password = "minhaSenhaSecreta123";
        String wrongPassword = "senhaIncorreta";
        String hashedPassword = passwordUtil.hashPassword(password);

        assertFalse(passwordUtil.verifyPassword(wrongPassword, hashedPassword));
    }

    @Test
    @DisplayName("Não deve verificar senha nula")
    void testVerifyPasswordWithNullPassword() {
        String hashedPassword = passwordUtil.hashPassword("anyPassword");
        assertFalse(passwordUtil.verifyPassword(null, hashedPassword));
    }

    @Test
    @DisplayName("Não deve verificar senha vazia")
    void testVerifyPasswordWithEmptyPassword() {
        String hashedPassword = passwordUtil.hashPassword("anyPassword");
        assertFalse(passwordUtil.verifyPassword("", hashedPassword));
    }

    @Test
    @DisplayName("Não deve verificar com hash nulo")
    void testVerifyPasswordWithNullHashedPassword() {
        assertFalse(passwordUtil.verifyPassword("anyPassword", null));
    }

    @Test
    @DisplayName("Não deve verificar com hash vazio")
    void testVerifyPasswordWithEmptyHashedPassword() {
        assertFalse(passwordUtil.verifyPassword("anyPassword", ""));
    }

    @Test
    @DisplayName("Não deve verificar com hash malformado")
    void testVerifyPasswordWithMalformedHashedPassword() {
        String password = "testPassword";
        String malformedHash = "notAValidHash"; // Não é um hash BCrypt válido
        assertFalse(passwordUtil.verifyPassword(password, malformedHash));

        String anotherMalformedHash = "$2a$10$invalidhashformat.thisisnotvalid"; // Formato inválido
        assertFalse(passwordUtil.verifyPassword(password, anotherMalformedHash));
    }
}
