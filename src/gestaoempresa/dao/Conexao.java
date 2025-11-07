package gestaoempresa.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/gestao_empresa?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // padrão do XAMPP
    private static final String PASS = "";     // geralmente vazio no XAMPP

    public static Connection getConnection() throws SQLException {   
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
