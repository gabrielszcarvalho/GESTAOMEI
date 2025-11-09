package gestaoempresa.dao;

import java.sql.*;
import java.util.*;

public class RelatorioDAO {

    
    private static final String URL = "jdbc:mysql://localhost:3306/gestao_empresa?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public Map<String, Double> obterTotaisPorTipo(String data, String tipo) throws SQLException {
        Map<String, Double> resultado = new LinkedHashMap<>();
        String sql = "SELECT descricao, SUM(valor) AS total " +
                     "FROM receita_despesa " +
                     "WHERE (? = 'Ambos' OR tipo = ?) AND DATE(data) = ? " +
                     "GROUP BY descricao";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, tipo);
            ps.setString(3, data);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultado.put(rs.getString("descricao"), rs.getDouble("total"));
            }
        }

        return resultado;
    }

    // NOVO MÉTODO
    public List<Map<String, Object>> obterRegistrosPorTipo(String data, String tipo) throws SQLException {
        List<Map<String, Object>> registros = new ArrayList<>();
        String sql = "SELECT tipo, descricao, valor, data " +
                     "FROM receita_despesa " +
                     "WHERE (? = 'Ambos' OR tipo = ?) AND DATE(data) = ? " +
                     "ORDER BY data";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipo);
            ps.setString(2, tipo);
            ps.setString(3, data);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> linha = new HashMap<>();
                linha.put("tipo", rs.getString("tipo"));
                linha.put("descricao", rs.getString("descricao"));
                linha.put("valor", rs.getDouble("valor"));
                linha.put("data", rs.getDate("data"));
                registros.add(linha);
            }
        }

        return registros;
    }
}
