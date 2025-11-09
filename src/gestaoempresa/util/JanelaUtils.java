package gestaoempresa.util;

import gestaoempresa.view.TelaPrincipal;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JanelaUtils {

    // Método genérico para voltar à TelaPrincipal
    public static void voltarParaPrincipal(JFrame janelaAtual) {
        new TelaPrincipal().setVisible(true); // abre a tela principal
        janelaAtual.dispose(); // fecha a janela atual
    }
    public static void centralizarPainel(JFrame frame, JPanel painel) {
        int x = (frame.getWidth() - painel.getWidth()) / 2;
        int y = (frame.getHeight() - painel.getHeight()) / 2;
        painel.setLocation(x, y);
    }
    public static void centralizarJanela(JFrame frame) {
        frame.setLocationRelativeTo(null);
    }
}
