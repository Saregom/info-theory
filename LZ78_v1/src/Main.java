import view.MainWindow;
import javax.swing.*;

/**
 * Clase principal de la aplicación
 * Compresor LZ78 - Teoría de la Información
 * Universidad Distrital Francisco José de Caldas
 */
public class Main {
    public static void main(String[] args) {
        // Configurar el Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel: " + e.getMessage());
        }
        
        // Crear y mostrar la ventana principal en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}