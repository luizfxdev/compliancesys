package com.compliancesys.util;

/**
 * Interface para utilitários de manipulação de senhas, como hashing e verificação.
 */
public interface PasswordUtil {

    /**
     * Gera um hash seguro para uma senha.
     *
     * @param password A senha em texto puro.
     * @return O hash da senha.
     */
    String hashPassword(String password);

    /**
     * Verifica se uma senha em texto puro corresponde a um hash fornecido.
     *
     * @param plainPassword A senha em texto puro.
     * @param hashedPassword O hash da senha a ser comparado.
     * @return true se a senha corresponder ao hash, false caso contrário.
     */
    boolean checkPassword(String plainPassword, String hashedPassword);
}
