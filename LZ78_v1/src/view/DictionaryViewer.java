package view;

import model.Dictionary;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana para visualizar el diccionario generado por LZ78
 */
public class DictionaryViewer extends JDialog {
    
    public DictionaryViewer(JFrame parent, Dictionary dictionary) {
        super(parent, "Diccionario LZ78", true);
        initComponents(dictionary);
    }
    
    private void initComponents(Dictionary dictionary) {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con título
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        JLabel titleLabel = new JLabel("Diccionario Generado por el Algoritmo LZ78");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // Área de texto con el diccionario
        JTextArea textArea = new JTextArea();
        textArea.setText(dictionary.getDictionaryAsString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botón de cerrar
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        JButton btnClose = new JButton("Cerrar");
        btnClose.addActionListener(e -> dispose());
        bottomPanel.add(btnClose);
        
        JButton btnExport = new JButton("Exportar");
        btnExport.setToolTipText("Copiar al portapapeles");
        btnExport.addActionListener(e -> {
            textArea.selectAll();
            textArea.copy();
            textArea.setSelectionEnd(0);
            JOptionPane.showMessageDialog(this, 
                "Diccionario copiado al portapapeles", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        bottomPanel.add(btnExport);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
}