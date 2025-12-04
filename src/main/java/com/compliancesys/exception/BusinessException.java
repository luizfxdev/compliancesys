package com.compliancesys.exception;

/**
 * Exceção de negócio customizada para o sistema ComplianceSys.
 * Esta exceção é utilizada para sinalizar erros que ocorrem devido a violações
 * de regras de negócio ou validações de dados, e que devem ser tratadas
 * de forma específica pela camada de apresentação ou pelos clientes da API.
 *
 * Ao invés de lançar exceções genéricas como IllegalArgumentException ou
 * IllegalStateException em cenários de negócio, BusinessException oferece
 * um tipo mais semântico e fácil de identificar.
 */
public class BusinessException extends RuntimeException {

    /**
     * Construtor padrão para BusinessException.
     * @param message A mensagem detalhada da exceção, descrevendo o erro de negócio.
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Construtor para BusinessException com uma causa raiz.
     * Útil quando a exceção de negócio é disparada por outra exceção subjacente.
     * @param message A mensagem detalhada da exceção.
     * @param cause A causa raiz da exceção.
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    // Você pode adicionar outros construtores ou métodos se precisar de mais detalhes,
    // como códigos de erro específicos, por exemplo:
    // private String errorCode;
    // public BusinessException(String message, String errorCode) {
    //     super(message);
    //     this.errorCode = errorCode;
    // }
    // public String getErrorCode() {
    //     return errorCode;
    // }
}
