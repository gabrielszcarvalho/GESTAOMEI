package gestaoempresa.dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class RelatorioDAO {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/gestao_empresa", "root", "");
    }

    public RelatorioDAO() {
        criarTabela();
    }

    private void criarTabela() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS receita_despesa (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    tipo VARCHAR(20) NOT NULL,
                    descricao VARCHAR(255) NOT NULL,
                    valor DOUBLE NOT NULL,
                    data DATE NOT NULL
                )
            """);
            System.out.println("Tabela verificada/criada com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> obterTotaisPorTipo(String data, String filtro) {
        Map<String, Double> totais = new HashMap<>();
        String sql;

        if ("Ambos".equals(filtro))
            sql = "SELECT tipo, SUM(valor) as total FROM receita_despesa WHERE data = ? GROUP BY tipo";
        else
            sql = "SELECT tipo, SUM(valor) as total FROM receita_despesa WHERE data = ? AND tipo = ? GROUP BY tipo";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, data);
            if (!"Ambos".equals(filtro)) stmt.setString(2, filtro);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                totais.put(rs.getString("tipo"), rs.getDouble("total"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totais;
    }
}
