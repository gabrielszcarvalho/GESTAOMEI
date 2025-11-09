package gestaoempresa.dao;

import gestaoempresa.model.ReceitaDespesa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceitaDespesaDAO {

    private final String url = "jdbc:mysql://localhost:3306/gestao_empresa?useSSL=false&serverTimezone=UTC";
    private final String user = "root";
    private final String password = ""; // ajuste se houver senha

    public ReceitaDespesaDAO() {
        criarBancoETabela();
    }

    // Conexão
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // Cria o banco e a tabela automaticamente
    private void criarBancoETabela() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC", user, password);
             Statement stmt = conn.createStatement()) {

            // Cria banco se não existir
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS gestao_empresa");

            // Cria tabela se não existir
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS gestao_empresa.receita_despesa (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    tipo VARCHAR(20) NOT NULL,
                    descricao VARCHAR(255) NOT NULL,
                    valor DOUBLE NOT NULL,
                    data DATE NOT NULL
                )
            """);

            System.out.println("Banco e tabela verificados/criados com sucesso.");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Inserir registro
    public void inserir(ReceitaDespesa rd) {
        String sql = "INSERT INTO receita_despesa(tipo, descricao, valor, data) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, rd.getTipo());
            stmt.setString(2, rd.getDescricao());
            stmt.setDouble(3, rd.getValor());
            stmt.setDate(4, new java.sql.Date(rd.getData().getTime()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    rd.setId(rs.getInt(1));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Listar todos
    public List<ReceitaDespesa> listarTodos() {
        List<ReceitaDespesa> lista = new ArrayList<>();
        String sql = "SELECT * FROM receita_despesa ORDER BY data DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReceitaDespesa rd = new ReceitaDespesa();
                rd.setId(rs.getInt("id"));
                rd.setTipo(rs.getString("tipo"));
                rd.setDescricao(rs.getString("descricao"));
                rd.setValor(rs.getDouble("valor"));
                rd.setData(rs.getDate("data"));
                lista.add(rd);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
    public double obterSaldoAtual() {
        String sql = "SELECT SUM(CASE WHEN tipo='Receita' THEN valor ELSE -valor END) FROM receita_despesa";
        try (Connection con = Conexao.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public double obterUltimoSaldo() {
    String sql = "SELECT saldoNoMomento FROM receita_despesa ORDER BY id DESC LIMIT 1";
    try (Connection con = Conexao.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            return rs.getDouble("saldoNoMomento");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0; // se não tiver registros, o saldo inicial é 0
}



    // Atualizar
    public void atualizar(ReceitaDespesa rd) {
        String sql = "UPDATE receita_despesa SET tipo=?, descricao=?, valor=?, data=? WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rd.getTipo());
            stmt.setString(2, rd.getDescricao());
            stmt.setDouble(3, rd.getValor());
            stmt.setDate(4, new java.sql.Date(rd.getData().getTime()));
            stmt.setInt(5, rd.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Excluir
    public void excluir(int id) {
        String sql = "DELETE FROM receita_despesa WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
