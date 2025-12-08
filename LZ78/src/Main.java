import view.MainWindow;
import javax.swing.*;

/**
 * Clase principal de la aplicación de compresión LZ78
 */
public class Main {
    
    public static void main(String[] args) {
        // Configurar el Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, usar el Look and Feel por defecto
            System.err.println("No se pudo establecer el Look and Feel del sistema");
        }
        
        // Iniciar la aplicación en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
