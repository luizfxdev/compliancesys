package com.compliancesys.util;

/**
 * Interface para utilitários de segurança de senha.
 * Define métodos para hash e verificação de senhas.
 */
public interface PasswordUtil {

    /**
     * Gera um hash seguro para uma senha.
     * @param password A senha em texto claro.
     * @return O hash da senha.
     * @throws RuntimeException Se ocorrer um erro durante a geração do hash.
     */
    String hashPassword(String password);

    /**
     * Verifica se uma senha em texto claro corresponde a um hash fornecido.
     * @param password A senha em texto claro.
     * @param hashedPassword O hash da senha a ser comparado.
     * @return true se a senha corresponde ao hash, false caso contrário.
     */
    boolean verifyPassword(String password, String hashedPassword);
}
