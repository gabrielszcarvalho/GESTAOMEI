package gestaoempresa.dao;

import gestaoempresa.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private static final String URL_BASE = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String URL_DB = "jdbc:mysql://localhost:3306/gestao_empresa?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    public ClienteDAO() {
        criarBancoSeNaoExistir();
        criarTabelaSeNaoExistir();
    }

    // --- Criar banco se não existir ---
    private void criarBancoSeNaoExistir() {
        try (Connection conn = DriverManager.getConnection(URL_BASE, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS gestao_empresa");
            System.out.println("Banco de dados 'gestao_empresa' verificado/criado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar banco: " + e.getMessage());
        }
    }

    // --- Criar tabela se não existir ---
    private void criarTabelaSeNaoExistir() {
        String sql = """
            CREATE TABLE IF NOT EXISTS cliente (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nome VARCHAR(100) NOT NULL,
                telefone VARCHAR(20),
                email VARCHAR(100),
                tipo VARCHAR(10)
            )
        """;

        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Tabela 'cliente' verificada/criada com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    // --- Conexão normal ---
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_DB, USER, PASS);
    }

    // --- Inserir cliente ---
    public void inserirCliente(Cliente c) {
        String sql = "INSERT INTO cliente(nome, telefone, email, tipo) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getTelefone());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getTipo());

            System.out.println("Executando INSERT: " +
                c.getNome() + ", " + c.getTelefone() + ", " + c.getEmail() + ", " + c.getTipo());

            int linhas = stmt.executeUpdate();
            System.out.println("Linhas afetadas: " + linhas);

        } catch (SQLException e) {
            System.err.println("Erro ao inserir cliente: " + e.getMessage());
        }
    }

    // --- Listar clientes ---
    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setTelefone(rs.getString("telefone"));
                c.setEmail(rs.getString("email"));
                c.setTipo(rs.getString("tipo"));
                clientes.add(c);
            }

            System.out.println("Total de clientes encontrados: " + clientes.size());

        } catch (SQLException e) {
            System.err.println("Erro ao listar clientes: " + e.getMessage());
        }

        return clientes;
    }
    public void excluirCliente(int id) {
    String sql = "DELETE FROM cliente WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        stmt.executeUpdate();
        System.out.println("Cliente com ID " + id + " excluído com sucesso!");
    } catch (SQLException e) {
        System.err.println("Erro ao excluir cliente: " + e.getMessage());
    }
}

    
    public static void main(String[] args) {
    ClienteDAO dao = new ClienteDAO();
    var lista = dao.listarClientes();
    System.out.println("Clientes no banco: " + lista.size());
    for (Cliente c : lista) {
        System.out.println(c.getNome() + " - " + c.getEmail());
    }
    
}
    public void atualizarCliente(Cliente cliente) {
    String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ?, tipo = ? WHERE id = ?";
    try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASS);
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getTelefone());
        stmt.setString(3, cliente.getEmail());
        stmt.setString(4, cliente.getTipo());
        stmt.setInt(5, cliente.getId());
        stmt.executeUpdate();
        System.out.println("Cliente atualizado com sucesso: " + cliente.getNome());
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}

