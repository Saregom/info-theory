package view;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Panel para visualizar el diccionario generado
 */
public class DictionaryViewer extends JPanel {
    private JTextArea dictionaryArea;
    private JScrollPane scrollPane;
    private JLabel statusLabel;

    public DictionaryViewer() {
        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        dictionaryArea = new JTextArea();
        dictionaryArea.setEditable(false);
        dictionaryArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        dictionaryArea.setMargin(new Insets(10, 10, 10, 10));
        
        scrollPane = new JScrollPane(dictionaryArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        statusLabel = new JLabel("Diccionario vacío");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel titleLabel = new JLabel("Diccionario LZ78");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Muestra el diccionario en el área de texto
     */
    public void displayDictionary(Map<Integer, String> dictionary) {
        if (dictionary == null || dictionary.isEmpty()) {
            dictionaryArea.setText("El diccionario está vacío.");
            statusLabel.setText("Entradas: 0");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    DICCIONARIO LZ78                        ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append("║  Índice  │  Secuencia                                      ║\n");
        sb.append("╠══════════╪═════════════════════════════════════════════════╣\n");

        dictionary.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String sequence = escapeString(entry.getValue());
                sb.append(String.format("║  %-7d │ %-47s ║\n", 
                    entry.getKey(), 
                    truncate(sequence, 47)));
            });

        sb.append("╚══════════╧═════════════════════════════════════════════════╝\n");

        dictionaryArea.setText(sb.toString());
        dictionaryArea.setCaretPosition(0);
        statusLabel.setText("Entradas: " + dictionary.size());
    }

    /**
     * Limpia el diccionario
     */
    public void clear() {
        dictionaryArea.setText("");
        statusLabel.setText("Diccionario vacío");
    }

    /**
     * Escapa caracteres especiales
     */
    private String escapeString(String str) {
        return str.replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t")
                  .replace("\"", "\\\"");
    }

    /**
     * Trunca un string si es muy largo
     */
    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
