package gestaoempresa.view;

import gestaoempresa.dao.ClienteDAO;
import gestaoempresa.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.table.TableCellEditor;

public class TelaGerenciamentoClientes extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaGerenciamentoClientes.class.getName());

    public TelaGerenciamentoClientes() {
        initComponents();
        configurarTabelaAcoes();
        carregarClientes();
        if (!java.beans.Beans.isDesignTime()) {
            setLocationRelativeTo(null);
            setTitle("Gerenciamento de Clientes");
        }
    }

    private void configurarTabelaAcoes() {
        DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();

        // garante a coluna de ações
        boolean temAcoes = false;
        for (int i = 0; i < modelo.getColumnCount(); i++) {
            if ("Ações".equalsIgnoreCase(modelo.getColumnName(i))) {
                temAcoes = true;
                break;
            }
        }
        if (!temAcoes) modelo.addColumn("Ações");

        tableClientes.setRowHeight(38);
        tableClientes.setDefaultEditor(Object.class, null);
        
        // Impede edição da coluna de ID (coluna 0)
        DefaultTableModel model = (DefaultTableModel) tableClientes.getModel();
        tableClientes.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
        @Override
        public boolean isCellEditable(java.util.EventObject e) {
            int row = tableClientes.getSelectedRow();
            int col = tableClientes.getSelectedColumn();
            // Bloqueia a primeira coluna (ID) e a coluna de ações
            return col != 0 && col != model.getColumnCount() - 1;
        }
        });
    

        // ícones
        java.net.URL editURL = getClass().getResource("/icons/edit.png");
        java.net.URL deleteURL = getClass().getResource("/icons/delete.png");

        final Icon editIcon = (editURL != null)
                ? new ImageIcon(new ImageIcon(editURL).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH))
                : new ImageIcon();
        final Icon deleteIcon = (deleteURL != null)
                ? new ImageIcon(new ImageIcon(deleteURL).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH))
                : new ImageIcon();

        int colAcoes = modelo.getColumnCount() - 1;
        tableClientes.getColumnModel().getColumn(colAcoes).setPreferredWidth(100);
        tableClientes.getColumnModel().getColumn(colAcoes).setMaxWidth(110);

        // --- RENDERER ---
        tableClientes.getColumnModel().getColumn(colAcoes).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                panel.setOpaque(true);

                JButton btnEditar = criarBotao(editIcon);
                JButton btnExcluir = criarBotao(deleteIcon);

                if (isSelected) {
                    panel.setBackground(table.getSelectionBackground());
                } else {
                    panel.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : Color.WHITE);
                }

                panel.add(btnEditar);
                panel.add(btnExcluir);
                return panel;
            }
        });

        // --- EDITOR (permite clicar) ---
        tableClientes.getColumnModel().getColumn(colAcoes).setCellEditor(new DefaultCellEditor(new JTextField()) {
            private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            private final JButton btnEditar = criarBotao(editIcon);
            private final JButton btnExcluir = criarBotao(deleteIcon);

            {
                btnEditar.addActionListener(evt -> {
    int row = tableClientes.getSelectedRow();

    if (row >= 0) {
        // Seleciona a linha clicada
        tableClientes.setRowSelectionInterval(row, row);

        // Habilita edição manualmente
        tableClientes.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));

        // Coloca o foco na primeira célula editável (coluna 1)
        boolean iniciouEdicao = tableClientes.editCellAt(row, 1);

        if (iniciouEdicao) {
            TableCellEditor editor = tableClientes.getCellEditor();
            if (editor != null) {
                editor.addCellEditorListener(new javax.swing.event.CellEditorListener() {
                    @Override
                    public void editingStopped(javax.swing.event.ChangeEvent e) {
                        salvarAlteracoes(row);
                        // depois de salvar, bloqueia a edição novamente
                        tableClientes.setDefaultEditor(Object.class, null);
                    }

                    @Override
                    public void editingCanceled(javax.swing.event.ChangeEvent e) {
                        tableClientes.setDefaultEditor(Object.class, null);
                    }
                });
            }
        }
    } else {
        JOptionPane.showMessageDialog(null, "Selecione uma linha para editar.");
    }
});


                btnExcluir.addActionListener(e -> {
                    int row = tableClientes.getEditingRow();
                    fireEditingStopped();
                    if (row >= 0) {
                        int resp = JOptionPane.showConfirmDialog(TelaGerenciamentoClientes.this,
                                "Deseja realmente excluir este cliente?",
                                "Confirmação", JOptionPane.YES_NO_OPTION);
                        if (resp == JOptionPane.YES_OPTION) excluirCliente(row);
                    }
                });

                panel.add(btnEditar);
                panel.add(btnExcluir);
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                                                         boolean isSelected, int row, int column) {
                panel.setBackground(table.getSelectionBackground());
                return panel;
            }

            @Override
            public Object getCellEditorValue() {
                return null;
            }
        });

        SwingUtilities.invokeLater(() -> {
            tableClientes.revalidate();
            tableClientes.repaint();
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

    private void editarClientePorLinha(int modelRow) {
        DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();
        String nome = (String) modelo.getValueAt(modelRow, 0);
        String telefone = (String) modelo.getValueAt(modelRow, 1);
        String email = (String) modelo.getValueAt(modelRow, 2);
        String tipo = (String) modelo.getValueAt(modelRow, 3);

        System.out.println("Editar cliente: " + nome + " (" + email + ")");
        // aqui pode abrir a tela de edição, ex: new TelaCadastroCliente(this, cliente).setVisible(true);
    }

    private void carregarClientes() {
    ClienteDAO dao = new ClienteDAO();
    List<Cliente> clientes = dao.listarClientes();

    DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();
    modelo.setRowCount(0);

    for (Cliente c : clientes) {
        // Armazena o objeto Cliente inteiro na linha, mas não mostra o ID
        modelo.addRow(new Object[]{c, c.getTelefone(), c.getEmail(), c.getTipo(), "Ações"});
    }

    // muda o renderizador da primeira coluna (mostrar nome em vez do objeto)
    tableClientes.getColumnModel().getColumn(0).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
        JLabel label = new JLabel(((Cliente) value).getNome());
        label.setOpaque(true);
        if (isSelected) label.setBackground(table.getSelectionBackground());
        return label;
    });
}


    private void excluirCliente(int linha) {
    DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();
    Cliente cliente = (Cliente) modelo.getValueAt(linha, 0); // pega o objeto Cliente direto

    int resp = JOptionPane.showConfirmDialog(
        this,
        "Deseja realmente excluir o cliente \"" + cliente.getNome() + "\"?",
        "Confirmação",
        JOptionPane.YES_NO_OPTION
    );

    if (resp == JOptionPane.YES_OPTION) {
        try {
            new ClienteDAO().excluirCliente(cliente.getId());
            modelo.removeRow(linha);
            JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + e.getMessage());
        }
    }
}


    public void adicionarClienteNaTabela(Cliente c) {
    DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();
    modelo.addRow(new Object[]{c, c.getTelefone(), c.getEmail(), c.getTipo(), "Ações"});
}


private void salvarAlteracoes(int row) {
    try {
        DefaultTableModel modelo = (DefaultTableModel) tableClientes.getModel();
        Cliente cliente = (Cliente) modelo.getValueAt(row, 0); // pega o objeto da primeira coluna

        String telefone = tableClientes.getValueAt(row, 1).toString();
        String email = tableClientes.getValueAt(row, 2).toString();
        String tipo = tableClientes.getValueAt(row, 3).toString();

        cliente.setTelefone(telefone);
        cliente.setEmail(email);
        cliente.setTipo(tipo);

        new ClienteDAO().atualizarCliente(cliente);
        JOptionPane.showMessageDialog(this, "Alterações salvas com sucesso!");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erro ao salvar alterações: " + e.getMessage());
    }
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("GERENCIAMENTO DE CLIENTES");

        tableClientes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tableClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nome", "Telefone", "Email", "Tipo", "Ações"
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVoltar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(847, 847, 847)
                        .addComponent(btnAdicionarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1052, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVoltar)
                    .addComponent(btnAdicionarCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarClienteActionPerformed
        new TelaCadastroCliente(this).setVisible(true);
    }//GEN-LAST:event_btnAdicionarClienteActionPerformed

    private void btnVoltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoltarActionPerformed
        TelaPrincipal telaPrincipal = new TelaPrincipal();
        telaPrincipal.setVisible(true);
        this.dispose(); 
    }//GEN-LAST:event_btnVoltarActionPerformed

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
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TelaGerenciamentoClientes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarCliente;
    private javax.swing.JButton btnVoltar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableClientes;
    // End of variables declaration//GEN-END:variables
}
