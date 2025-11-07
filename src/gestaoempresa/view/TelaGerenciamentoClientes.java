package gestaoempresa.view;

import gestaoempresa.dao.ClienteDAO;
import gestaoempresa.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaGerenciamentoClientes extends javax.swing.JFrame {
    private boolean modoEdicaoAtivo = false;

    public TelaGerenciamentoClientes() {
        initComponents();
        configurarTabelaAcoes();
        carregarClientes();
        setLocationRelativeTo(null);
        setTitle("Gerenciamento de Clientes");
        tableClientes.removeColumn(tableClientes.getColumnModel().getColumn(0)); // esconde ID
    }

    private void configurarTabelaAcoes() {
        DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();
        tableClientes.setRowHeight(38);
        tableClientes.setDefaultEditor(Object.class, null); // bloqueia edição inicial

        // Ícone de exclusão
        java.net.URL deleteURL = getClass().getResource("/icons/delete.png");
        final Icon deleteIcon = (deleteURL != null)
                ? new ImageIcon(new ImageIcon(deleteURL).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH))
                : new ImageIcon();

        int colAcoes = modelo.getColumnCount() - 1;
        tableClientes.getColumnModel().getColumn(colAcoes).setPreferredWidth(100);
        tableClientes.getColumnModel().getColumn(colAcoes).setMaxWidth(110);

        // Renderer para botão de excluir
        tableClientes.getColumnModel().getColumn(colAcoes).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);

            JButton btnExcluir = criarBotao(deleteIcon);
            btnExcluir.addActionListener(e -> {
                int modelRow = table.convertRowIndexToModel(row);
                excluirCliente(modelRow);
            });

            panel.add(btnExcluir);
            panel.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
            if (isSelected) panel.setBackground(table.getSelectionBackground());

            return panel;
        });

        // Editor para botão
        tableClientes.getColumnModel().getColumn(colAcoes).setCellEditor(new DefaultCellEditor(new JTextField()) {
            private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            private final JButton btnExcluir = criarBotao(deleteIcon);

            {
                btnExcluir.addActionListener(e -> {
                    int row = tableClientes.convertRowIndexToModel(tableClientes.getEditingRow());
                    fireEditingStopped();
                    if (row >= 0) excluirCliente(row);
                });
                panel.add(btnExcluir);
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                panel.setBackground(table.getSelectionBackground());
                return panel;
            }

            @Override
            public Object getCellEditorValue() {
                return null;
            }
        });
    }

    private JButton criarBotao(Icon icon) {
        JButton botao = new JButton(icon);
        botao.setPreferredSize(new Dimension(28, 28));
        botao.setFocusable(false);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color hoverColor = new Color(220, 220, 220);
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setOpaque(true);
                botao.setBackground(hoverColor);
                botao.setContentAreaFilled(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botao.setOpaque(false);
                botao.setContentAreaFilled(false);
            }
        });
        return botao;
    }

    private void carregarClientes() {
        ClienteDAO dao = new ClienteDAO();
        List<Cliente> clientes = dao.listarClientes();

        DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();
        modelo.setRowCount(0);

        for (Cliente c : clientes) {
            modelo.addRow(new Object[]{c.getId(), c.getNome(), c.getTelefone(), c.getEmail(), c.getTipo(), "Ações"});
        }
        tableClientes.setDefaultEditor(Object.class, null);
    }

    private void excluirCliente(int linha) {
        DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();
        int id = Integer.parseInt(modelo.getValueAt(linha, 0).toString());
        String nome = (String) modelo.getValueAt(linha, 1);

        int resp = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o cliente \"" + nome + "\"?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (resp == JOptionPane.YES_OPTION) {
            try {
                ClienteDAO dao = new ClienteDAO();
                dao.excluirCliente(id);
                modelo.removeRow(linha);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + e.getMessage());
            }
        }
    }

    private void salvarAlteracoes() {
        if (tableClientes.isEditing()) {
            tableClientes.getCellEditor().stopCellEditing();
        }
        try {
            DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();
            ClienteDAO dao = new ClienteDAO();

            for (int i = 0; i < modelo.getRowCount(); i++) {
                int id = Integer.parseInt(modelo.getValueAt(i, 0).toString());
                String nome = modelo.getValueAt(i, 1).toString();
                String telefone = modelo.getValueAt(i, 2).toString();
                String email = modelo.getValueAt(i, 3).toString();
                String tipo = modelo.getValueAt(i, 4).toString();

                Cliente cliente = dao.buscarPorId(id);
                if (cliente != null) {
                    cliente.setNome(nome);
                    cliente.setTelefone(telefone);
                    cliente.setEmail(email);
                    cliente.setTipo(tipo);
                    dao.atualizarCliente(cliente);
                }
            }

            JOptionPane.showMessageDialog(this, "Alterações salvas com sucesso!");
            carregarClientes();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar alterações: " + e.getMessage());
        }
    }

    public void adicionarClienteNaTabela(Cliente c) {
        DefaultTableModel model = (DefaultTableModel) tableClientes.getModel();
        model.addRow(new Object[]{c.getId(), c.getNome(), c.getTelefone(), c.getEmail(), c.getTipo(), "Ações"});
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableClientes = new javax.swing.JTable();
        btnAdicionarCliente = new javax.swing.JButton();
        btnVoltar = new javax.swing.JButton();
        btnEditarTabela = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("GERENCIAMENTO DE CLIENTES");

        tableClientes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tableClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Telefone", "Email", "Tipo", "Ações"
            }
        ));
        tableClientes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tableClientes.setName("tableClientes"); // NOI18N
        jScrollPane1.setViewportView(tableClientes);

        btnAdicionarCliente.setLabel("Adicionar Cliente");
        btnAdicionarCliente.setName("btnAdicionarCliente"); // NOI18N
        btnAdicionarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarClienteActionPerformed(evt);
            }
        });

        btnVoltar.setText("<");
        btnVoltar.setName("btnAdicionarCliente"); // NOI18N
        btnVoltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoltarActionPerformed(evt);
            }
        });

        btnEditarTabela.setText("Editar");
        btnEditarTabela.setToolTipText("");
        btnEditarTabela.setName("btnAdicionarCliente"); // NOI18N
        btnEditarTabela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarTabelaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(728, 728, 728)
                        .addComponent(btnEditarTabela)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdicionarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1052, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVoltar)
                    .addComponent(btnAdicionarCliente)
                    .addComponent(btnEditarTabela))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarClienteActionPerformed
     
        new TelaCadastroCliente(this).setVisible(true);
    }//GEN-LAST:event_btnAdicionarClienteActionPerformed

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
// Fecha a tela atual
    this.dispose();
    
    // Abre a tela principal
    TelaPrincipal telaPrincipal = new TelaPrincipal();
    telaPrincipal.setVisible(true);
    }//GEN-LAST:event_btnVoltarActionPerformed

    private void btnEditarTabelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarTabelaActionPerformed
        
        if (!modoEdicaoAtivo) {
        modoEdicaoAtivo = true;
        btnEditarTabela.setText("Salvar Alterações");

        // Ativa edição apenas nas colunas certas (não edita ID nem Ações)
        tableClientes.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean isCellEditable(java.util.EventObject e) {
                int col = tableClientes.getSelectedColumn();
                // Permite editar apenas colunas 1, 2, 3, 4 (Nome, Telefone, Email, Tipo)
                // Não edita coluna 0 (ID oculto) nem última coluna (Ações)
                return col >= 1 && col <= 4;
            }
        });

        JOptionPane.showMessageDialog(this, "Modo de edição ativado!");

    } else {
        salvarAlteracoes();
        modoEdicaoAtivo = false;
        btnEditarTabela.setText("Editar");

        // Desativa edição novamente
        tableClientes.setDefaultEditor(Object.class, null);
    }

    }//GEN-LAST:event_btnEditarTabelaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TelaGerenciamentoClientes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarCliente;
    private javax.swing.JButton btnEditarTabela;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableClientes;
    // End of variables declaration//GEN-END:variables
}
