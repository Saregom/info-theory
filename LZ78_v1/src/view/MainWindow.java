package view;

import controller.CompressionController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana principal de la aplicación de compresión LZ78
 */
public class MainWindow extends JFrame {
    private CompressionController controller;
    
    // Componentes de la interfaz
    private JTextArea textArea;
    private JButton btnLoadFile;
    private JButton btnCompress;
    private JButton btnDecompress;
    private JButton btnSaveCompressed;
    private JButton btnSaveDecompressed;
    private JButton btnViewDictionary;
    private JButton btnClear;
    private JLabel lblStatus;
    private JLabel lblFileInfo;
    
    public MainWindow() {
        controller = new CompressionController(this);
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Compresor LZ78 - Teoría de la Información");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con botones
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Panel central con área de texto
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior con información
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 5, 5));
        
        btnLoadFile = new JButton("Cargar Archivo");
        btnLoadFile.setToolTipText("Cargar un archivo de texto para comprimir");
        btnLoadFile.addActionListener(e -> controller.loadFile());
        
        btnCompress = new JButton("Comprimir");
        btnCompress.setToolTipText("Comprimir el archivo cargado");
        btnCompress.addActionListener(e -> controller.compress());
        btnCompress.setEnabled(false);
        
        btnSaveCompressed = new JButton("Guardar Comprimido");
        btnSaveCompressed.setToolTipText("Guardar el archivo comprimido");
        btnSaveCompressed.addActionListener(e -> controller.saveCompressed());
        btnSaveCompressed.setEnabled(false);
        
        btnDecompress = new JButton("Cargar y Descomprimir");
        btnDecompress.setToolTipText("Cargar un archivo .lz78 y descomprimirlo");
        btnDecompress.addActionListener(e -> controller.loadAndDecompress());
        
        btnSaveDecompressed = new JButton("Guardar Descomprimido");
        btnSaveDecompressed.setToolTipText("Guardar el texto descomprimido");
        btnSaveDecompressed.addActionListener(e -> controller.saveDecompressed());
        btnSaveDecompressed.setEnabled(false);
        
        btnViewDictionary = new JButton("Ver Diccionario");
        btnViewDictionary.setToolTipText("Mostrar el diccionario generado");
        btnViewDictionary.addActionListener(e -> controller.showDictionary());
        btnViewDictionary.setEnabled(false);
        
        btnClear = new JButton("Limpiar");
        btnClear.setToolTipText("Limpiar el área de texto");
        btnClear.addActionListener(e -> clearAll());
        
        JButton btnExit = new JButton("Salir");
        btnExit.addActionListener(e -> System.exit(0));
        
        JButton btnHelp = new JButton("Ayuda");
        btnHelp.addActionListener(e -> showHelp());
        
        panel.add(btnLoadFile);
        panel.add(btnCompress);
        panel.add(btnSaveCompressed);
        panel.add(btnDecompress);
        panel.add(btnSaveDecompressed);
        panel.add(btnViewDictionary);
        panel.add(btnClear);
        panel.add(btnHelp);
        panel.add(btnExit);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel label = new JLabel("Contenido del Archivo:");
        panel.add(label, BorderLayout.NORTH);
        
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        lblFileInfo = new JLabel("Ningún archivo cargado");
        lblFileInfo.setFont(new Font("Arial", Font.BOLD, 11));
        panel.add(lblFileInfo, BorderLayout.NORTH);
        
        lblStatus = new JLabel("Listo");
        lblStatus.setBorder(BorderFactory.createEtchedBorder());
        panel.add(lblStatus, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void clearAll() {
        textArea.setText("");
        lblFileInfo.setText("Ningún archivo cargado");
        lblStatus.setText("Listo");
        btnCompress.setEnabled(false);
        btnSaveCompressed.setEnabled(false);
        btnSaveDecompressed.setEnabled(false);
        btnViewDictionary.setEnabled(false);
        controller.clear();
    }
    
    private void showHelp() {
        String help = "COMPRESOR LZ78 - AYUDA\n\n" +
                "1. Comprimir un archivo:\n" +
                "   - Click en 'Cargar Archivo'\n" +
                "   - Click en 'Comprimir'\n" +
                "   - Click en 'Guardar Comprimido'\n\n" +
                "2. Descomprimir un archivo:\n" +
                "   - Click en 'Cargar y Descomprimir'\n" +
                "   - Seleccionar archivo .lz78\n" +
                "   - Click en 'Guardar Descomprimido'\n\n" +
                "3. Ver Diccionario:\n" +
                "   - Después de comprimir o descomprimir\n" +
                "   - Click en 'Ver Diccionario'\n\n" +
                "Universidad Distrital Francisco José de Caldas\n" +
                "Teoría de la Información 2025-III";
        
        JOptionPane.showMessageDialog(this, help, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Métodos públicos para el controlador
    public void setTextArea(String text) {
        textArea.setText(text);
    }
    
    public String getTextArea() {
        return textArea.getText();
    }
    
    public void setStatus(String status) {
        lblStatus.setText(status);
    }
    
    public void setFileInfo(String info) {
        lblFileInfo.setText(info);
    }
    
    public void enableCompress(boolean enable) {
        btnCompress.setEnabled(enable);
    }
    
    public void enableSaveCompressed(boolean enable) {
        btnSaveCompressed.setEnabled(enable);
    }
    
    public void enableSaveDecompressed(boolean enable) {
        btnSaveDecompressed.setEnabled(enable);
    }
    
    public void enableViewDictionary(boolean enable) {
        btnViewDictionary.setEnabled(enable);
    }
    
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showStatistics(String stats) {
        JTextArea textArea = new JTextArea(stats);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        JOptionPane.showMessageDialog(this, scrollPane, "Estadísticas de Compresión", 
                                      JOptionPane.INFORMATION_MESSAGE);
    }
}