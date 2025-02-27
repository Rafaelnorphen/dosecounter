import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class HMJICON {
    private static final String DEFAULT_ICON_PATH = "D:\\HMJ\\frascos\\frasco\\src\\main\\java\\irssl.jpeg";

    public static void applyCustomIcon() {
        SwingUtilities.invokeLater(() -> {
            Frame[] frames = Frame.getFrames();
            for (Frame frame : frames) {
                if (frame instanceof JFrame && frame.isVisible()) {
                    setFrameIcon((JFrame) frame, DEFAULT_ICON_PATH);
                    // Adiciona um listener para aplicar o ícone caso o frame seja reaberto
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                            setFrameIcon((JFrame) frame, DEFAULT_ICON_PATH);
                        }
                    });
                }
            }
        });
    }

    private static void setFrameIcon(JFrame frame, String iconPath) {
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            frame.setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Erro ao carregar o ícone: " + e.getMessage());
            // Tenta carregar o ícone como um recurso se falhar como arquivo
            try {
                Image icon = Toolkit.getDefaultToolkit().getImage(HMJICON.class.getResource(iconPath));
                frame.setIconImage(icon);
            } catch (Exception ex) {
                System.err.println("Erro ao carregar o ícone como recurso: " + ex.getMessage());
            }
        }
    }

    // Método opcional para definir um ícone personalizado em tempo de execução
    public static void setCustomIcon(String iconPath) {
        SwingUtilities.invokeLater(() -> {
            Frame[] frames = Frame.getFrames();
            for (Frame frame : frames) {
                if (frame instanceof JFrame && frame.isVisible()) {
                    setFrameIcon((JFrame) frame, iconPath);
                }
            }
        });
    }
}