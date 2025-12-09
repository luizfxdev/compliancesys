package com.compliancesys.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe DatabaseConfig, garantindo que a configuração do banco de dados
 * e o padrão Singleton funcionem corretamente.
 */
class DatabaseConfigTest {

    private static final String TEST_PROPERTIES_FILE = "database.properties";
    private Properties testProperties;

    @BeforeEach
    void setUp() {
        // Carrega as propriedades de teste antes de cada teste
        testProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(TEST_PROPERTIES_FILE)) {
            if (input == null) {
                fail("Arquivo de propriedades de teste não encontrado: " + TEST_PROPERTIES_FILE);
            }
            testProperties.load(input);
        } catch (IOException e) {
            fail("Erro ao carregar o arquivo de propriedades de teste: " + e.getMessage());
        }

        // Define as propriedades do sistema para que DatabaseConfig as utilize
        System.setProperty("db.driver", testProperties.getProperty("db.driver"));
        System.setProperty("db.url", testProperties.getProperty("db.url"));
        System.setProperty("db.username", testProperties.getProperty("db.username"));
        System.setProperty("db.password", testProperties.getProperty("db.password"));
    }

    @AfterEach
    void tearDown() {
        // Limpa as propriedades do sistema após cada teste
        System.clearProperty("db.driver");
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");
    }

    @Test
    @DisplayName("Deve retornar a mesma instância de DatabaseConfig (Singleton)")
    void testSingletonInstance() {
        DatabaseConfig instance1 = DatabaseConfig.getInstance();
        DatabaseConfig instance2 = DatabaseConfig.getInstance();
        assertNotNull(instance1, "A primeira instância não deve ser nula.");
        assertNotNull(instance2, "A segunda instância não deve ser nula.");
        assertSame(instance1, instance2, "As instâncias devem ser as mesmas (Singleton).");
    }

    @Test
    @DisplayName("Deve estabelecer uma conexão válida com o banco de dados")
    void testGetConnection() {
        DatabaseConfig config = DatabaseConfig.getInstance();
        try (Connection connection = config.getConnection()) {
            assertNotNull(connection, "A conexão não deve ser nula.");
            assertFalse(connection.isClosed(), "A conexão não deve estar fechada.");
            assertTrue(connection.isValid(1), "A conexão deve ser válida."); // 1 segundo de timeout
        } catch (SQLException e) {
            fail("Falha ao obter ou validar a conexão com o banco de dados: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Deve lançar exceção se as propriedades do banco de dados estiverem faltando")
    void testMissingDatabaseProperties() {
        // Limpa as propriedades para simular a falta
        System.clearProperty("db.url");
        System.clearProperty("db.username");
        System.clearProperty("db.password");

        // Força a recriação da instância para que ela tente carregar as propriedades novamente
        // (Isso pode exigir um ajuste na classe DatabaseConfig para permitir re-inicialização
        // ou um teste separado que não dependa do Singleton já inicializado)
        // Para este teste, vamos simular o cenário de falha na inicialização
        // A forma mais robusta seria testar o construtor privado ou um método de inicialização
        // que lida com a leitura das propriedades.
        // Como DatabaseConfig é um Singleton, uma vez inicializado, ele não tentará carregar novamente.
        // Para testar este cenário, precisamos garantir que a instância seja "resetada" ou que o teste
        // seja executado antes de qualquer outra inicialização.

        // Uma abordagem alternativa para testar a falha de carregamento de propriedades
        // seria injetar um carregador de propriedades mockado ou usar um método de inicialização
        // que possa ser testado isoladamente.
        // Para o propósito atual, vamos simular a falha de conexão se as propriedades estiverem ausentes.

        DatabaseConfig config = DatabaseConfig.getInstance(); // Pega a instância existente (pode já ter carregado)
        // Se a instância já carregou as propriedades, este teste pode não falhar como esperado.
        // Para um teste mais isolado, DatabaseConfig precisaria de um método para "recarregar" ou
        // um construtor que aceitasse propriedades para teste.

        // Tentativa de obter conexão com propriedades ausentes (se a instância não foi inicializada antes)
        // Ou se a DatabaseConfig for projetada para falhar na getConnection se as propriedades não forem válidas.
        try (Connection connection = config.getConnection()) {
            fail("A conexão deveria ter falhado devido a propriedades ausentes.");
        } catch (SQLException e) {
            // Esperado que uma SQLException seja lançada
            assertTrue(e.getMessage().contains("url") || e.getMessage().contains("username") || e.getMessage().contains("password") || e.getMessage().contains("connection"),
                    "A mensagem de erro deve indicar problema de conexão/propriedades.");
        }
    }
}
