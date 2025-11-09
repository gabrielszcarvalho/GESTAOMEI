package gestaoempresa.view;

import gestaoempresa.util.JanelaUtils;

import static com.mysql.cj.conf.PropertyKey.logger;
import gestaoempresa.model.ReceitaDespesa;
import gestaoempresa.dao.ReceitaDespesaDAO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TelaReceitaDespesa extends javax.swing.JFrame {

    private final DefaultTableModel model;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaReceitaDespesa.class.getName());
    private final ReceitaDespesaDAO dao = new ReceitaDespesaDAO();
    
     public TelaReceitaDespesa() {
        initComponents();
        setLocationRelativeTo(null);
        JanelaUtils.centralizarJanela(this);
        JanelaUtils.centralizarPainel(this, jPanel1);
        //deixar a tb de descrição como um input
        tbDescricao.setText("Descrição");
        tbDescricao.setForeground(Color.GRAY);

        tbDescricao.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            // Quando o usuário clicar no campo
            if (tbDescricao.getText().equals("Descrição")) {
                tbDescricao.setText("");
                tbDescricao.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            // Quando o campo perder o foco e estiver vazio
            if (tbDescricao.getText().isEmpty()) {
            tbDescricao.setForeground(Color.GRAY);
            tbDescricao.setText("Descrição");
            }
            }
        });
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
         addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent evt) {
            // Aqui chamamos a classe utilitária que abre a TelaPrincipal
            gestaoempresa.util.JanelaUtils.voltarParaPrincipal(TelaReceitaDespesa.this);
        }
        });
        model = new DefaultTableModel(new String[]{"ID", "Tipo", "Descrição", "Valor", "Data","Totalizador","Ações"}, 0);
        tableRegistros.setModel(model);
        tableRegistros.removeColumn(tableRegistros.getColumnModel().getColumn(0)); //oculta a colune id
        
        
        //alterar os pinners para valores e Data e hora
        spinnerValor.setModel(new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.1));
        spinnerData.setModel(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        spinnerData.setEditor(new JSpinner.DateEditor(spinnerData, "dd/MM/yyyy"));

        cbDespesasReceitas.setModel(new DefaultComboBoxModel<>(new String[]{"Receita", "Despesa"}));

        carregarTabela();

        // Botão de adicionar
        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.addActionListener(e -> adicionarRegistro());
        JPanel panel = new JPanel();
        panel.add(btnAdicionar);
        getContentPane().add(panel);
        pack(); // garante que todos os componentes estão dimensionados
        setLocationRelativeTo(null); // centraliza a tela
    }
    


    private void adicionarRegistro() {
        ReceitaDespesa rd = new ReceitaDespesa();
        rd.setTipo(cbDespesasReceitas.getSelectedItem().toString());
        rd.setDescricao(tbDescricao.getText());
        rd.setValor((double) spinnerValor.getValue());
        rd.setData((Date) spinnerData.getValue());

        dao.inserir(rd);
        carregarTabela();
        limparCampos();
    }

    // Atualiza os painéis de Receita, Despesa e Saldo
    private void atualizarTotais() {
    double totalReceitas = 0;
    double totalDespesas = 0;

    for (int i = 0; i < model.getRowCount(); i++) {
        String tipo = model.getValueAt(i, 1).toString();
        double valor = Double.parseDouble(model.getValueAt(i, 3).toString());



        if (tipo.equalsIgnoreCase("Receita")) {
            totalReceitas += valor;
        } else if (tipo.equalsIgnoreCase("Despesa")) {
            totalDespesas += valor;
        }
    }

    double saldoFinal = totalReceitas - totalDespesas;
    lblTotalReceitas.setText("Receitas: R$ " + String.format("%.2f", totalReceitas));
    lblTotalDespesas.setText("Despesas: R$ " + String.format("%.2f", totalDespesas));
    lblSaldo.setText("Saldo: R$ " + String.format("%.2f", saldoFinal));
}


    // Limpa campos após adicionar
    private void limparCampos() {
    cbDespesasReceitas.setSelectedIndex(0);
    tbDescricao.setText("Descrição");
    tbDescricao.setForeground(Color.GRAY);
    spinnerValor.setValue(0.0);
    spinnerData.setValue(new Date());
    }

    
    
    private void preencherCamposParaEdicao() {
    int selectedRow = tableRegistros.getSelectedRow();
    if (selectedRow >= 0) {
        cbDespesasReceitas.setSelectedItem(model.getValueAt(selectedRow, 1).toString());
        tbDescricao.setText(model.getValueAt(selectedRow, 2).toString());
        spinnerValor.setValue((double) model.getValueAt(selectedRow, 3));

        try {
            Date data = new SimpleDateFormat("dd/MM/yyyy")
                    .parse(model.getValueAt(selectedRow, 4).toString());
            spinnerData.setValue(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

private void atualizarRegistro() {
    int selectedRow = tableRegistros.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Selecione um registro para editar.");
        return;
    }

    int id = (int) model.getValueAt(selectedRow, 0);

    ReceitaDespesa rd = new ReceitaDespesa();
    rd.setId(id);
    rd.setTipo(cbDespesasReceitas.getSelectedItem().toString());
    rd.setDescricao(tbDescricao.getText());
    rd.setValor((double) spinnerValor.getValue());
    rd.setData((Date) spinnerData.getValue());

    dao.atualizar(rd);
    carregarTabela();
    limparCampos();
}

    private void excluirRegistro() {
    int selectedRow = tableRegistros.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Selecione um registro para excluir.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir este registro?",
            "Confirmação", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        int id = (int) model.getValueAt(selectedRow, 0);
        dao.excluir(id);
        carregarTabela();
    }
}

    
    
    
    private void configurarTabelaAcoes() {
    DefaultTableModel modelo = (DefaultTableModel) tableRegistros.getModel();
    tableRegistros.setRowHeight(38);
    tableRegistros.setDefaultEditor(Object.class, null); // bloqueia edição inicial

    // Ícone de exclusão
    java.net.URL deleteURL = getClass().getResource("/icons/delete.png");
    final Icon deleteIcon = (deleteURL != null)
            ? new ImageIcon(new ImageIcon(deleteURL).getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH))
            : new ImageIcon();

    int colAcoes = tableRegistros.getColumnCount() - 1;
    tableRegistros.getColumnModel().getColumn(colAcoes).setPreferredWidth(100);
    tableRegistros.getColumnModel().getColumn(colAcoes).setMaxWidth(110);

    // Renderer para botão de excluir
    tableRegistros.getColumnModel().getColumn(colAcoes).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(true);

        JButton btnExcluir = criarBotao(deleteIcon);
        btnExcluir.addActionListener(e -> {
            int modelRow = table.convertRowIndexToModel(row);
            excluirRegistro(modelRow);
        });

        panel.add(btnExcluir);
        panel.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
        if (isSelected) panel.setBackground(table.getSelectionBackground());

        return panel;
    });

    // Editor para botão
    tableRegistros.getColumnModel().getColumn(colAcoes).setCellEditor(new DefaultCellEditor(new JTextField()) {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private final JButton btnExcluir = criarBotao(deleteIcon);

        {
            btnExcluir.addActionListener(e -> {
                int row = tableRegistros.convertRowIndexToModel(tableRegistros.getEditingRow());
                fireEditingStopped();
                if (row >= 0) excluirRegistro(row);
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

private void excluirRegistro(int linha) {
    DefaultTableModel modelo = (DefaultTableModel) tableRegistros.getModel();
    int id = Integer.parseInt(modelo.getValueAt(linha, 0).toString());
    String descricao = (String) modelo.getValueAt(linha, 2);

    int resp = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir o registro \"" + descricao + "\"?",
            "Confirmação",
            JOptionPane.YES_NO_OPTION
    );

    if (resp == JOptionPane.YES_OPTION) {
        try {
            ReceitaDespesaDAO dao = new ReceitaDespesaDAO();
            dao.excluir(id);
            modelo.removeRow(linha);
            JOptionPane.showMessageDialog(this, "Registro excluído com sucesso!");
            atualizarTotais();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao excluir registro: " + e.getMessage());
        }
    }
}

private void carregarTabela() {
    DefaultTableModel modelo = (DefaultTableModel) tableRegistros.getModel();
    modelo.setRowCount(0);

    ReceitaDespesaDAO dao = new ReceitaDespesaDAO();
    var lista = dao.listarTodos();

    for (var item : lista) {
        modelo.addRow(new Object[]{
                item.getId(),
                item.getTipo(),
                item.getDescricao(),
                item.getValor(),
                item.getData(),
                "Ações"
        });
    }

    configurarTabelaAcoes(); // chama o novo método
    atualizarTotais();
}



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblTotalReceitas = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblTotalDespesas = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lblSaldo = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbDespesasReceitas = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableRegistros = new javax.swing.JTable();
        spinnerValor = new javax.swing.JSpinner();
        tbDescricao = new javax.swing.JTextField();
        spinnerData = new javax.swing.JSpinner();
        btnAdicionar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblTotalReceitas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTotalReceitas.setText("0,00");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblTotalReceitas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(lblTotalReceitas)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblTotalDespesas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTotalDespesas.setText("0,00");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblTotalDespesas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblTotalDespesas)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblSaldo.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblSaldo.setText("0,00");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblSaldo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(lblSaldo)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setText("REGISTROS");
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        cbDespesasReceitas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tableRegistros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Tipo", "Descrição", "Data", "Valor", "Totalizador", "Ações"
            }
        ));
        jScrollPane1.setViewportView(tableRegistros);

        spinnerValor.setBorder(null);

        tbDescricao.setText("Descrição");

        btnAdicionar.setText("Adicionar");
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 820, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnAdicionar)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(spinnerValor, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinnerData, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cbDespesasReceitas, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbDespesasReceitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinnerValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinnerData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdicionar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        adicionarRegistro();
    }//GEN-LAST:event_btnAdicionarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new TelaReceitaDespesa().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JComboBox<String> cbDespesasReceitas;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSaldo;
    private javax.swing.JLabel lblTotalDespesas;
    private javax.swing.JLabel lblTotalReceitas;
    private javax.swing.JSpinner spinnerData;
    private javax.swing.JSpinner spinnerValor;
    private javax.swing.JTable tableRegistros;
    private javax.swing.JTextField tbDescricao;
    // End of variables declaration//GEN-END:variables
}
