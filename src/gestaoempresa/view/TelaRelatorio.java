package gestaoempresa.view;

import gestaoempresa.dao.RelatorioDAO;
import gestaoempresa.util.JanelaUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.apache.poi.ss.usermodel.Font;



public class TelaRelatorio extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaReceitaDespesa.class.getName());
    public TelaRelatorio() {
        initComponents();
        JanelaUtils.centralizarJanela(this);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
         addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent evt) {
            // Aqui chamamos a classe utilitária que abre a TelaPrincipal
            gestaoempresa.util.JanelaUtils.voltarParaPrincipal(TelaRelatorio.this);
        }
        });
        configurarSpinner();
        agruparRadios();
    }

    private void configurarSpinner() {
        SpinnerDateModel modeloData = new SpinnerDateModel();
        spinnerDataInicio.setModel(modeloData);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerDataInicio, "dd/MM/yyyy");
        spinnerDataInicio.setEditor(editor);
    }

    private void agruparRadios() {
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rdbtnDespesas);
        grupo.add(rdbtnReceitas);
        grupo.add(rdbtnAmbos);
    }
    private void gerarGrafico(Map<String, Double> dados) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Map.Entry<String, Double> entrada : dados.entrySet()) {
            dataset.setValue(entrada.getKey(), entrada.getValue());
        }

        JFreeChart grafico = ChartFactory.createPieChart(
                "Distribuição de Gastos",
                dataset,
                true,
                true,
                false
        );

       
    }
    
    public void gerarExcelRelatorio(List<Map<String, Object>> registros, String caminhoArquivo) {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Relatório");

        // --- Criando estilos ---
        // Estilo do cabeçalho
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        // Estilo para valores numéricos
        CellStyle valorStyle = workbook.createCellStyle();
        valorStyle.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));
        valorStyle.setBorderBottom(BorderStyle.THIN);
        valorStyle.setBorderTop(BorderStyle.THIN);
        valorStyle.setBorderLeft(BorderStyle.THIN);
        valorStyle.setBorderRight(BorderStyle.THIN);

        // Estilo para datas
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // Estilo padrão de células
        CellStyle defaultStyle = workbook.createCellStyle();
        defaultStyle.setBorderBottom(BorderStyle.THIN);
        defaultStyle.setBorderTop(BorderStyle.THIN);
        defaultStyle.setBorderLeft(BorderStyle.THIN);
        defaultStyle.setBorderRight(BorderStyle.THIN);

        // --- Cabeçalho ---
        Row header = sheet.createRow(0);
        String[] colunas = {"Tipo", "Descrição", "Valor", "Data"};
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(headerStyle);
        }

        // --- Conteúdo ---
        int linha = 1;
        for (Map<String, Object> registro : registros) {
            Row row = sheet.createRow(linha++);
            
            // Tipo
            Cell tipoCell = row.createCell(0);
            tipoCell.setCellValue((String) registro.get("tipo"));
            tipoCell.setCellStyle(defaultStyle);

            // Descrição
            Cell descCell = row.createCell(1);
            descCell.setCellValue((String) registro.get("descricao"));
            descCell.setCellStyle(defaultStyle);

            // Valor
            Cell valorCell = row.createCell(2);
            valorCell.setCellValue((Double) registro.get("valor"));
            valorCell.setCellStyle(valorStyle);

            // Data
            Cell dataCell = row.createCell(3);
            Object dataObj = registro.get("data");
            if (dataObj instanceof java.sql.Date sqlDate) {
                dataCell.setCellValue(sqlDate);
            } else if (dataObj instanceof java.util.Date utilDate) {
                dataCell.setCellValue(utilDate);
            } else {
                dataCell.setCellValue(String.valueOf(dataObj));
            }
            dataCell.setCellStyle(dataStyle);
        }

        // Auto-ajustar colunas
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // --- Salvar arquivo ---
        FileOutputStream fileOut = new FileOutputStream(caminhoArquivo);
        workbook.write(fileOut);
        fileOut.close();

        JOptionPane.showMessageDialog(this, "Excel gerado com sucesso!");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Erro ao gerar Excel: " + e.getMessage());
        e.printStackTrace();
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
        spinnerDataInicio = new javax.swing.JSpinner();
        btnGerarRelatorio = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rdbtnDespesas = new javax.swing.JRadioButton();
        rdbtnReceitas = new javax.swing.JRadioButton();
        rdbtnAmbos = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("RELATÓRIOS DE GASTOS");

        spinnerDataInicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnGerarRelatorio.setText("GerarRelatório");
        btnGerarRelatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarRelatorioActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("DATA:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("TIPO:");

        rdbtnDespesas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rdbtnDespesas.setText("Despesas");
        rdbtnDespesas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtnDespesasActionPerformed(evt);
            }
        });

        rdbtnReceitas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rdbtnReceitas.setText("Receitas");

        rdbtnAmbos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rdbtnAmbos.setText("Ambos");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel4.setText("De");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rdbtnDespesas)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdbtnReceitas)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdbtnAmbos))
                            .addComponent(btnGerarRelatorio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(85, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerDataInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerDataInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdbtnReceitas)
                        .addComponent(rdbtnAmbos))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(rdbtnDespesas)))
                .addGap(18, 18, 18)
                .addComponent(btnGerarRelatorio)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGerarRelatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarRelatorioActionPerformed
    
        try {
            // Define o tipo baseado nos radio buttons
            String tipo = null;
            if (rdbtnDespesas.isSelected()) tipo = "Despesa";
            else if (rdbtnReceitas.isSelected()) tipo = "Receita";
            else if (rdbtnAmbos.isSelected()) tipo = "Ambos";
            else {
                JOptionPane.showMessageDialog(this, "Selecione um tipo de relatório (Despesas, Receitas ou Ambos).");
                return;
            }

            //  Pega a data do spinner
            Date dataSelecionada = (Date) spinnerDataInicio.getValue();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String data = sdf.format(dataSelecionada);

            // Chama o DAO
            RelatorioDAO dao = new RelatorioDAO();
            List<Map<String, Object>> registros = dao.obterRegistrosPorTipo(data, tipo); 

            // Gera o Excel
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar Relatório");
            fileChooser.setSelectedFile(new java.io.File("Relatorio.xlsx")); // Nome padrão

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                gerarExcelRelatorio(registros, fileToSave.getAbsolutePath());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnGerarRelatorioActionPerformed

    private void rdbtnDespesasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtnDespesasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbtnDespesasActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new TelaRelatorio().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGerarRelatorio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JRadioButton rdbtnAmbos;
    private javax.swing.JRadioButton rdbtnDespesas;
    private javax.swing.JRadioButton rdbtnReceitas;
    private javax.swing.JSpinner spinnerDataInicio;
    // End of variables declaration//GEN-END:variables
}
