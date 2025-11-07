package gestaoempresa.dao;

import gestaoempresa.model.Cliente;

public class TestDAO {
    public static void main(String[] args) {
        ClienteDAO dao = new ClienteDAO();

        System.out.println("=== TESTANDO INSERÇÃO NO BANCO ===");

        Cliente c = new Cliente("Gabriel Teste", "99999-9999", "gabriel@teste.com", "Boa");

        dao.inserirCliente(c);

        System.out.println("\n=== CLIENTES NO BANCO ===");
        for (Cliente cli : dao.listarClientes()) {
            System.out.println(cli.getId() + " | " + cli.getNome() + " | " + cli.getEmail());
        }
    }
}
