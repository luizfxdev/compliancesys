package com.compliancesys.util.impl;

import com.compliancesys.util.PasswordUtil;
import org.mindrot.jbcrypt.BCrypt; // Necessário adicionar a dependência no build.gradle

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface PasswordUtil utilizando o algoritmo BCrypt para hashing de senhas.
 * BCrypt é um algoritmo de hashing de senha robusto e recomendado para aplicações modernas.
 */
public class PasswordUtilImpl implements PasswordUtil {

    private static final Logger LOGGER = Logger.getLogger(PasswordUtilImpl.class.getName());
    private static final int BCRYPT_SALT_ROUNDS = 12; // Custo do hash, quanto maior, mais seguro (e mais lento)

    @Override
    public String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            LOGGER.log(Level.WARNING, "Tentativa de hash de senha nula ou vazia.");
            return null;
        }
        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_SALT_ROUNDS));
            LOGGER.log(Level.FINE, "Senha hashed com sucesso.");
            return hashedPassword;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao gerar hash da senha: " + e.getMessage(), e);
            throw new RuntimeException("Falha ao gerar hash da senha.", e);
        }
    }

    @Override
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            LOGGER.log(Level.WARNING, "Tentativa de verificar senha com valores nulos.");
            return false;
        }
        try {
            boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
            if (!matches) {
                LOGGER.log(Level.WARNING, "Falha na verificação de senha para hash.");
            } else {
                LOGGER.log(Level.FINE, "Senha verificada com sucesso.");
            }
            return matches;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar senha: " + e.getMessage(), e);
            throw new RuntimeException("Falha ao verificar senha.", e);
        }
    }
}
