package com.compliancesys.config;

import java.io.IOException;         // Importa para lidar com exceções de I/O.
import java.io.InputStream;         // Importa para ler o arquivo de propriedades.
import java.sql.Connection;         // Importa para a interface Connection do JDBC.
import java.sql.DriverManager;      // Importa para gerenciar drivers JDBC.
import java.sql.ResultSet;          // Importa para a interface ResultSet do JDBC.
import java.sql.SQLException;       // Importa para lidar com exceções SQL.
import java.sql.Statement;          // Importa para a interface Statement do JDBC.
import java.util.Properties;        // Importa para trabalhar com arquivos de propriedades.
import java.util.logging.Level;     // Importa para níveis de log.
import java.util.logging.Logger;    // Importa para logging.

/**
 * Classe de configuração do banco de dados para o sistema ComplianceSys.
 * Gerencia o carregamento das propriedades de conexão e o estabelecimento de conexões.
 */
public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName()); // Logger para a classe.
    private static final Properties properties = new Properties(); // Objeto Properties para armazenar as configurações.
    private static final String PROPERTIES_FILE = "database.properties"; // Nome do arquivo de propriedades.

    // Bloco estático para carregar as propriedades do banco de dados uma vez.
    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                LOGGER.log(Level.SEVERE, "Desculpe, não foi possível encontrar " + PROPERTIES_FILE);
                throw new IOException("Arquivo de propriedades do banco de dados não encontrado: " + PROPERTIES_FILE);
            }
            properties.load(input); // Carrega as propriedades do arquivo.
            LOGGER.log(Level.INFO, "Propriedades do banco de dados carregadas com sucesso.");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar o arquivo de propriedades do banco de dados: " + PROPERTIES_FILE, ex);
            // Re-lança a exceção como uma RuntimeException para falha na inicialização da aplicação.
            throw new RuntimeException("Falha ao inicializar a configuração do banco de dados.", ex);
        }
    }

    /**
     * Retorna uma nova conexão com o banco de dados.
     * Este método agora é estático para ser acessado diretamente pela classe.
     * @return Uma conexão com o banco de dados.
     * @throws SQLException Se ocorrer um erro de conexão.
     */
    public static Connection getConnection() throws SQLException { // CORRIGIDO: Método agora é estático
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        LOGGER.log(Level.FINE, "Tentando conectar ao banco de dados: {0}", url);
        Connection connection = DriverManager.getConnection(url, user, password);
        LOGGER.log(Level.FINE, "Conexão com o banco de dados estabelecida com sucesso.");
        return connection;
    }

    /**
     * Fecha os recursos do banco de dados (Connection, Statement, ResultSet) de forma segura.
     * @param conn A conexão a ser fechada.
     * @param stmt O statement a ser fechado.
     * @param rs O result set a ser fechado.
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao fechar recursos do banco de dados.", ex);
        }
    }

    /**
     * Sobrecarga para fechar Connection e Statement.
     * @param conn A conexão a ser fechada.
     * @param stmt O statement a ser fechado.
     */
    public static void closeResources(Connection conn, Statement stmt) {
        closeResources(conn, stmt, null);
    }

    /**
     * Sobrecarga para fechar apenas a Connection.
     * @param conn A conexão a ser fechada.
     */
    public static void closeResources(Connection conn) {
        closeResources(conn, null, null);
    }
}
