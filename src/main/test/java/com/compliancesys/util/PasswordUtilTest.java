package com.compliancesys.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt; // Usando jBCrypt como uma implementação comum para testes

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    // Implementação concreta simples da interface PasswordUtil usando jBCrypt para fins de teste
    private static class PasswordUtilImpl implements PasswordUtil {
        private static final int BCRYPT_LOG_ROUNDS = 12; // Custo padrão para BCrypt

        @Override
        public String hashPassword(String password) {
            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("A senha não pode ser nula ou vazia.");
            }
            try {
                return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
            } catch (Exception e) {
                throw new RuntimeException("Erro ao gerar hash da senha", e);
            }
        }

        @Override
        public boolean verifyPassword(String password, String hashedPassword) {
            if (password == null || hashedPassword == null) {
                return false; // Senhas nulas não podem ser verificadas
            }
            try {
                return BCrypt.checkpw(password, hashedPassword);
            } catch (Exception e) {
                // Logar a exceção se necessário, mas não relançar para evitar falha na verificação
                System.err.println("Erro durante a verificação da senha: " + e.getMessage());
                return false;
            }
        }
    }

    private PasswordUtil passwordUtil;

    @BeforeEach
    void setUp() {
        // Inicializa a implementação concreta do PasswordUtil antes de cada teste
        passwordUtil = new PasswordUtilImpl();
    }

    @Test
    void testHashPasswordGeneratesValidHash() {
        String password = "minhaSenhaSegura123";
        String hashedPassword = passwordUtil.hashPassword(password);

        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isEmpty());
        // BCrypt hashes sempre começam com "$2a$", "$2b$" ou "$2y$"
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$"));
        // O hash deve ter um comprimento razoável (jBCrypt geralmente 60 caracteres)
        assertTrue(hashedPassword.length() > 50);
    }

    @Test
    void testHashPasswordThrowsExceptionForNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordUtil.hashPassword(null);
        });
    }

    @Test
    void testHashPasswordThrowsExceptionForEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            passwordUtil.hashPassword("");
        });
    }

    @Test
    void testVerifyPasswordMatchesCorrectPassword() {
        String password = "senhaParaTeste123!";
        String hashedPassword = passwordUtil.hashPassword(password);

        assertTrue(passwordUtil.verifyPassword(password, hashedPassword));
    }

    @Test
    void testVerifyPasswordDoesNotMatchIncorrectPassword() {
        String correctPassword = "senhaCorreta";
        String incorrectPassword = "senhaIncorreta";
        String hashedPassword = passwordUtil.hashPassword(correctPassword);

        assertFalse(passwordUtil.verifyPassword(incorrectPassword, hashedPassword));
    }

    @Test
    void testVerifyPasswordWithDifferentHashesForSamePassword() {
        String password = "outraSenhaForte";
        String hashedPassword1 = passwordUtil.hashPassword(password);
        String hashedPassword2 = passwordUtil.hashPassword(password); // Deve ser diferente devido ao salt aleatório

        assertNotEquals(hashedPassword1, hashedPassword2); // Os hashes devem ser diferentes
        assertTrue(passwordUtil.verifyPassword(password, hashedPassword1));
        assertTrue(passwordUtil.verifyPassword(password, hashedPassword2));
    }

    @Test
    void testVerifyPasswordWithNullPassword() {
        String hashedPassword = passwordUtil.hashPassword("anyPassword");
        assertFalse(passwordUtil.verifyPassword(null, hashedPassword));
    }

    @Test
    void testVerifyPasswordWithNullHashedPassword() {
        assertFalse(passwordUtil.verifyPassword("anyPassword", null));
    }

    @Test
    void testVerifyPasswordWithEmptyPassword() {
        String hashedPassword = passwordUtil.hashPassword("anyPassword");
        assertFalse(passwordUtil.verifyPassword("", hashedPassword));
    }

    @Test
    void testVerifyPasswordWithEmptyHashedPassword() {
        assertFalse(passwordUtil.verifyPassword("anyPassword", ""));
    }

    @Test
    void testVerifyPasswordWithMalformedHashedPassword() {
        String password = "testPassword";
        String malformedHash = "notAValidHash"; // Não é um hash BCrypt válido
        assertFalse(passwordUtil.verifyPassword(password, malformedHash));

        String anotherMalformedHash = "$2a$10$invalidhashformat.thisisnotvalid"; // Formato inválido
        assertFalse(passwordUtil.verifyPassword(password, anotherMalformedHash));
    }
}
