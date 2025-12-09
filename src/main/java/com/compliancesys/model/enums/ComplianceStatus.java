    // ComplianceStatus.java
    package com.compliancesys.model.enums;

    /**
     * Enumeração para o status de conformidade de uma jornada de trabalho.
     */
    public enum ComplianceStatus {
        CONFORME("Conforme"),
        NAO_CONFORME("Não Conforme"),
        ALERTA("Alerta"),
        PENDING("Pendente"),
        EM_ANDAMENTO("Em Andamento"),
        INVALIDO("Inválido"),
        COMPLIANT("Compliant"),
        NON_COMPLIANT("Non-Compliant"),
        UNKNOWN("Desconhecido"); // Adicionado para resolver o erro

        private final String description;

        /**
         * Construtor para ComplianceStatus.
         * @param description Descrição do status.
         */
        ComplianceStatus(String description) {
            this.description = description;
        }

        /**
         * Retorna a descrição legível do status de conformidade.
         * @return Descrição do status.
         */
        public String getDescription() {
            return description;
        }
    }
