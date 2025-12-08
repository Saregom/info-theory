package view;

import controller.CompressionController;
import model.CompressionResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Ventana principal de la aplicación de compresión LZ78
 */
public class MainWindow extends JFrame {
    private CompressionController controller;
    private JTabbedPane tabbedPane;
    
    // Panel de Compresión
    private JPanel compressionPanel;
    private JTextArea inputTextArea;
    private JTextArea compressedOutputArea;
    private JTextArea statsArea;
    private DictionaryViewer compressionDictionaryViewer;
    private JButton loadFileButton;
    private JButton compressButton;
    private JButton saveCompressedButton;
    private JButton saveDictionaryButton;
    private JButton clearCompressionButton;
    private JLabel compressionStatusLabel;
    
    // Panel de Descompresión
    private JPanel decompressionPanel;
    private JTextArea decompressedOutputArea;
    private JTextArea decompressionStatsArea;
    private DictionaryViewer decompressionDictionaryViewer;
    private JButton loadCompressedButton;
    private JButton decompressButton;
    private JButton saveDecompressedButton;
    private JButton clearDecompressionButton;
    private JLabel decompressionStatusLabel;
    
    // Datos de compresión
    private CompressionResult lastCompressionResult;
    private CompressionResult lastDecompressionResult;
    private String lastCompressedFileName;
    private String lastOriginalFileName;
    private String lastOriginalFilePath; // Path completo del archivo a comprimir

    public MainWindow() {
        controller = new CompressionController();
        initComponents();
        layoutComponents();
        setupListeners();
        
        setTitle("Compresor LZ78 - Teoría de la Información");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Inicializar panel de compresión
        initCompressionPanel();
        
        // Inicializar panel de descompresión
        initDecompressionPanel();
    }

    private void initCompressionPanel() {
        compressionPanel = new JPanel(new BorderLayout(10, 10));
        compressionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Área de entrada
        inputTextArea = new JTextArea(8, 40);
        inputTextArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        
        // Área de salida comprimida
        compressedOutputArea = new JTextArea(8, 40);
        compressedOutputArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        compressedOutputArea.setEditable(false);
        compressedOutputArea.setLineWrap(true);
        
        // Área de estadísticas
        statsArea = new JTextArea(8, 40);
        statsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        statsArea.setEditable(false);
        
        // Visor de diccionario
        compressionDictionaryViewer = new DictionaryViewer();
        
        // Botones
        loadFileButton = new JButton("Cargar Archivo");
        compressButton = new JButton("Comprimir");
        saveCompressedButton = new JButton("Guardar Comprimido");
        saveDictionaryButton = new JButton("Guardar Diccionario");
        clearCompressionButton = new JButton("Limpiar");
        
        saveCompressedButton.setEnabled(false);
        saveDictionaryButton.setEnabled(false);
        
        // Estado
        compressionStatusLabel = new JLabel("Listo para comprimir");
        compressionStatusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
    }

    private void initDecompressionPanel() {
        decompressionPanel = new JPanel(new BorderLayout(10, 10));
        decompressionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Área de salida descomprimida
        decompressedOutputArea = new JTextArea(12, 40);
        decompressedOutputArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        decompressedOutputArea.setEditable(false);
        decompressedOutputArea.setLineWrap(true);
        decompressedOutputArea.setWrapStyleWord(true);
        
        // Área de estadísticas
        decompressionStatsArea = new JTextArea(8, 40);
        decompressionStatsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        decompressionStatsArea.setEditable(false);
        
        // Visor de diccionario
        decompressionDictionaryViewer = new DictionaryViewer();
        
        // Botones
        loadCompressedButton = new JButton("Cargar Archivo Comprimido");
        decompressButton = new JButton("Descomprimir");
        saveDecompressedButton = new JButton("Guardar Descomprimido");
        clearDecompressionButton = new JButton("Limpiar");
        
        decompressButton.setEnabled(false);
        saveDecompressedButton.setEnabled(false);
        
        // Estado
        decompressionStatusLabel = new JLabel("Listo para descomprimir");
        decompressionStatusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
    }

    private void layoutComponents() {
        // Layout del panel de compresión
        layoutCompressionPanel();
        
        // Layout del panel de descompresión
        layoutDecompressionPanel();
        
        // Agregar pestañas
        tabbedPane.addTab("Compresión", compressionPanel);
        tabbedPane.addTab("Descompresión", decompressionPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void layoutCompressionPanel() {
        // Panel superior: entrada y controles
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new TitledBorder("Texto de Entrada"));
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(loadFileButton);
        buttonPanel.add(compressButton);
        buttonPanel.add(saveCompressedButton);
        buttonPanel.add(saveDictionaryButton);
        buttonPanel.add(clearCompressionButton);
        
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Panel central: dividido entre salida y estadísticas
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("Datos Codificados"));
        outputPanel.add(new JScrollPane(compressedOutputArea), BorderLayout.CENTER);
        
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(new TitledBorder("Estadísticas"));
        statsPanel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        
        centerSplit.setLeftComponent(outputPanel);
        centerSplit.setRightComponent(statsPanel);
        centerSplit.setDividerLocation(400);
        
        // Panel inferior: diccionario
        JPanel dictPanel = new JPanel(new BorderLayout());
        dictPanel.setBorder(new TitledBorder("Diccionario Generado"));
        dictPanel.add(compressionDictionaryViewer, BorderLayout.CENTER);
        
        // Split principal
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setTopComponent(topPanel);
        
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        bottomSplit.setTopComponent(centerSplit);
        bottomSplit.setBottomComponent(dictPanel);
        bottomSplit.setDividerLocation(200);
        
        mainSplit.setBottomComponent(bottomSplit);
        mainSplit.setDividerLocation(200);
        
        compressionPanel.add(mainSplit, BorderLayout.CENTER);
        compressionPanel.add(compressionStatusLabel, BorderLayout.SOUTH);
    }

    private void layoutDecompressionPanel() {
        // Panel superior: controles
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(loadCompressedButton);
        topPanel.add(decompressButton);
        topPanel.add(saveDecompressedButton);
        topPanel.add(clearDecompressionButton);
        
        // Panel central: texto descomprimido y estadísticas
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("Texto Descomprimido"));
        outputPanel.add(new JScrollPane(decompressedOutputArea), BorderLayout.CENTER);
        
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(new TitledBorder("Estadísticas"));
        statsPanel.add(new JScrollPane(decompressionStatsArea), BorderLayout.CENTER);
        
        centerSplit.setLeftComponent(outputPanel);
        centerSplit.setRightComponent(statsPanel);
        centerSplit.setDividerLocation(500);
        
        // Panel inferior: diccionario
        JPanel dictPanel = new JPanel(new BorderLayout());
        dictPanel.setBorder(new TitledBorder("Diccionario Reconstruido"));
        dictPanel.add(decompressionDictionaryViewer, BorderLayout.CENTER);
        
        // Split principal
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setTopComponent(centerSplit);
        mainSplit.setBottomComponent(dictPanel);
        mainSplit.setDividerLocation(300);
        
        decompressionPanel.add(topPanel, BorderLayout.NORTH);
        decompressionPanel.add(mainSplit, BorderLayout.CENTER);
        decompressionPanel.add(decompressionStatusLabel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        // Listeners de compresión
        loadFileButton.addActionListener(e -> loadFile());
        compressButton.addActionListener(e -> compress());
        saveCompressedButton.addActionListener(e -> saveCompressed());
        saveDictionaryButton.addActionListener(e -> saveDictionary());
        clearCompressionButton.addActionListener(e -> clearCompression());
        
        // Listeners de descompresión
        loadCompressedButton.addActionListener(e -> loadCompressedFile());
        decompressButton.addActionListener(e -> decompress());
        saveDecompressedButton.addActionListener(e -> saveDecompressed());
        clearDecompressionButton.addActionListener(e -> clearDecompression());
    }

    // ==================== MÉTODOS DE COMPRESIÓN ====================
    
    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        
        // Agregar filtros para diferentes tipos de archivos
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
            "Todos los archivos soportados", 
            "txt", "docx", "doc", "xlsx", "xls", "pdf", "jpg", "jpeg", "png", "gif", "zip", "rar"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Archivos de Texto", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Documentos Word", "docx", "doc"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Hojas de cálculo Excel", "xlsx", "xls"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "gif"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Archivos comprimidos", "zip", "rar"));
        fileChooser.setAcceptAllFileFilterUsed(true);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String fileName = file.getName().toLowerCase();
                String content;
                
                // Determinar si es archivo de texto o binario
                if (fileName.endsWith(".txt")) {
                    content = controller.loadTextFile(file.getAbsolutePath());
                    inputTextArea.setText(content);
                } else {
                    // Archivo binario
                    content = controller.loadBinaryFile(file.getAbsolutePath());
                    inputTextArea.setText("[Archivo binario cargado: " + file.getName() + "]\n" +
                        "Tamaño: " + file.length() + " bytes\n" +
                        "Tipo: " + getFileExtension(fileName));
                }
                
                // Guardar el nombre y path del archivo para usarlo al comprimir
                lastOriginalFileName = file.getName();
                lastOriginalFilePath = file.getAbsolutePath();
                
                compressionStatusLabel.setText("Archivo cargado: " + file.getName() + " (" + file.length() + " bytes)");
                compressionStatusLabel.setForeground(Color.BLUE);
            } catch (Exception ex) {
                showError("Error al cargar archivo", ex.getMessage());
                compressionStatusLabel.setText("Error: " + ex.getMessage());
                compressionStatusLabel.setForeground(Color.RED);
            }
        }
    }

    private void compress() {
        if (lastOriginalFilePath == null || lastOriginalFileName == null) {
            showError("Sin archivo", "Por favor, cargue un archivo primero.");
            return;
        }

        try {
            // Comprimir
            compressionStatusLabel.setText("Comprimiendo...");
            compressionStatusLabel.setForeground(Color.BLUE);
            
            // Cargar el archivo usando el path almacenado
            String content;
            String fileName = lastOriginalFileName.toLowerCase();
            
            if (fileName.endsWith(".txt")) {
                content = controller.loadTextFile(lastOriginalFilePath);
            } else {
                content = controller.loadBinaryFile(lastOriginalFilePath);
            }
            
            lastCompressionResult = controller.compressText(content);
            
            // Mostrar datos codificados
            compressedOutputArea.setText(lastCompressionResult.getEncodedDataString());
            
            // Mostrar estadísticas
            statsArea.setText(lastCompressionResult.getStatistics());
            
            // Mostrar diccionario
            compressionDictionaryViewer.displayDictionary(
                lastCompressionResult.getDictionary().getReverseDictionary());
            
            // Habilitar botones de guardado
            saveCompressedButton.setEnabled(true);
            saveDictionaryButton.setEnabled(true);
            
            compressionStatusLabel.setText("Compresión exitosa - " + 
                String.format("%.2f%% de compresión", lastCompressionResult.getCompressionPercentage()));
            compressionStatusLabel.setForeground(new Color(0, 128, 0));
            
        } catch (Exception ex) {
            showError("Error al comprimir", ex.getMessage());
            compressionStatusLabel.setText("Error: " + ex.getMessage());
            compressionStatusLabel.setForeground(Color.RED);
        }
    }

    private void saveCompressed() {
        if (lastCompressionResult == null) {
            showError("Sin datos", "No hay datos comprimidos para guardar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos LZ78", "lz78"));
        
        // Generar nombre de archivo basado en el archivo original
        String defaultFileName = "comprimido.lz78";
        if (lastOriginalFileName != null && !lastOriginalFileName.isEmpty()) {
            // Remover cualquier extensión del archivo original
            String baseName = lastOriginalFileName;
            int lastDot = baseName.lastIndexOf('.');
            if (lastDot > 0) {
                baseName = baseName.substring(0, lastDot);
            }
            defaultFileName = baseName + "_comprimido.lz78";
        }
        
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // Obtener la extensión original del archivo
                String originalExtension = getFileExtension(lastOriginalFileName);
                controller.saveCompressedFile(lastCompressionResult, file.getAbsolutePath(), originalExtension);
                showInfo("Guardado exitoso", "El archivo comprimido se guardó correctamente.");
                compressionStatusLabel.setText("Archivo guardado: " + file.getName());
            } catch (Exception ex) {
                showError("Error al guardar", ex.getMessage());
            }
        }
    }

    private void saveDictionary() {
        if (lastCompressionResult == null) {
            showError("Sin datos", "No hay diccionario para guardar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto", "txt"));
        fileChooser.setSelectedFile(new File("diccionario_compresion.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                controller.saveDictionaryAndEncoded(lastCompressionResult, file.getAbsolutePath());
                showInfo("Guardado exitoso", "El diccionario y datos codificados se guardaron correctamente.");
            } catch (Exception ex) {
                showError("Error al guardar", ex.getMessage());
            }
        }
    }

    // ==================== MÉTODOS DE DESCOMPRESIÓN ====================
    
    private void loadCompressedFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos LZ78", "lz78"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Object[] result = controller.loadCompressedFileWithExtension(file.getAbsolutePath());
                String originalExtension = (String) result[0];
                @SuppressWarnings("unchecked")
                List<CompressionResult.EncodedPair> encodedData = 
                    (List<CompressionResult.EncodedPair>) result[1];
                
                // Guardar el nombre del archivo para usarlo al descomprimir
                lastCompressedFileName = file.getName();
                lastOriginalFileName = originalExtension; // Guardar extensión para descomprimir
                
                // Crear resultado temporal para mostrar información
                CompressionResult tempResult = new CompressionResult();
                tempResult.setEncodedData(encodedData);
                
                lastDecompressionResult = tempResult;
                
                String fileType = !originalExtension.isEmpty() ? originalExtension : "desconocido";
                decompressionStatsArea.setText("Archivo cargado correctamente.\n\n" +
                    "Tipo de archivo original: " + fileType + "\n" +
                    "Pares codificados: " + encodedData.size() + "\n" +
                    "Tamaño estimado: " + (encodedData.size() * 6) + " bytes\n\n" +
                    "Presione 'Descomprimir' para continuar.");
                
                decompressButton.setEnabled(true);
                decompressionStatusLabel.setText("Archivo cargado: " + file.getName());
                decompressionStatusLabel.setForeground(Color.BLUE);
                
            } catch (Exception ex) {
                showError("Error al cargar archivo", ex.getMessage());
                decompressionStatusLabel.setText("Error: " + ex.getMessage());
                decompressionStatusLabel.setForeground(Color.RED);
                decompressButton.setEnabled(false);
            }
        }
    }

    private void decompress() {
        if (lastDecompressionResult == null || lastDecompressionResult.getEncodedData() == null) {
            showError("Sin datos", "No hay datos comprimidos cargados.");
            return;
        }

        try {
            decompressionStatusLabel.setText("Descomprimiendo...");
            decompressionStatusLabel.setForeground(Color.BLUE);
            
            // Descomprimir
            lastDecompressionResult = controller.decompressData(
                lastDecompressionResult.getEncodedData());
            
            // Mostrar texto descomprimido
            decompressedOutputArea.setText(lastDecompressionResult.getDecompressedText());
            
            // Mostrar estadísticas
            decompressionStatsArea.setText(lastDecompressionResult.getStatistics());
            
            // Mostrar diccionario
            decompressionDictionaryViewer.displayDictionary(
                lastDecompressionResult.getDictionary().getReverseDictionary());
            
            // Habilitar botón de guardado
            saveDecompressedButton.setEnabled(true);
            
            decompressionStatusLabel.setText("Descompresión exitosa");
            decompressionStatusLabel.setForeground(new Color(0, 128, 0));
            
        } catch (Exception ex) {
            showError("Error al descomprimir", ex.getMessage());
            decompressionStatusLabel.setText("Error: " + ex.getMessage());
            decompressionStatusLabel.setForeground(Color.RED);
        }
    }

    private void saveDecompressed() {
        if (lastDecompressionResult == null || 
            lastDecompressionResult.getDecompressedText() == null) {
            showError("Sin datos", "No hay texto descomprimido para guardar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        
        // Determinar la extensión original
        String originalExt = lastOriginalFileName != null && !lastOriginalFileName.isEmpty() 
            ? lastOriginalFileName : ".txt";
        if (!originalExt.startsWith(".")) {
            originalExt = "." + originalExt;
        }
        
        // Configurar filtro según el tipo de archivo
        if (originalExt.equals(".txt")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto", "txt"));
        } else if (originalExt.equals(".docx") || originalExt.equals(".doc")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Documentos Word", "docx", "doc"));
        } else if (originalExt.equals(".xlsx") || originalExt.equals(".xls")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Hojas Excel", "xlsx", "xls"));
        } else if (originalExt.equals(".pdf")) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));
        } else {
            fileChooser.setAcceptAllFileFilterUsed(true);
        }
        
        // Generar nombre de archivo basado en el archivo .lz78 original
        String defaultFileName = "descomprimido" + originalExt;
        if (lastCompressedFileName != null && !lastCompressedFileName.isEmpty()) {
            String baseName = lastCompressedFileName;
            // Remover la extensión .lz78
            if (baseName.toLowerCase().endsWith(".lz78")) {
                baseName = baseName.substring(0, baseName.length() - 5);
            }
            // Remover el sufijo _comprimido si existe
            if (baseName.toLowerCase().endsWith("_comprimido")) {
                baseName = baseName.substring(0, baseName.length() - 11);
            }
            defaultFileName = baseName + "_descomprimido" + originalExt;
        }
        
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // Guardar como binario o texto según la extensión
                if (originalExt.equals(".txt")) {
                    controller.saveDecompressedFile(
                        lastDecompressionResult.getDecompressedText(), 
                        file.getAbsolutePath());
                } else {
                    controller.saveBinaryFile(
                        lastDecompressionResult.getDecompressedText(), 
                        file.getAbsolutePath());
                }
                showInfo("Guardado exitoso", "El archivo descomprimido se guardó correctamente.");
                decompressionStatusLabel.setText("Archivo guardado: " + file.getName());
            } catch (Exception ex) {
                showError("Error al guardar", ex.getMessage());
            }
        }
    }

    // ==================== MÉTODOS DE LIMPIEZA ====================
    
    private void clearCompression() {
        inputTextArea.setText("");
        compressedOutputArea.setText("");
        statsArea.setText("");
        compressionDictionaryViewer.clear();
        lastCompressionResult = null;
        lastOriginalFileName = null;
        lastOriginalFilePath = null;
        saveCompressedButton.setEnabled(false);
        saveDictionaryButton.setEnabled(false);
        compressionStatusLabel.setText("Listo para comprimir");
        compressionStatusLabel.setForeground(Color.BLACK);
    }
    
    private void clearDecompression() {
        decompressedOutputArea.setText("");
        decompressionStatsArea.setText("");
        decompressionDictionaryViewer.clear();
        lastDecompressionResult = null;
        lastCompressedFileName = null;
        decompressButton.setEnabled(false);
        saveDecompressedButton.setEnabled(false);
        decompressionStatusLabel.setText("Listo para descomprimir");
        decompressionStatusLabel.setForeground(Color.BLACK);
    }
    
    // ==================== MÉTODOS DE UTILIDAD ====================
    
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Obtiene la extensión de un archivo
     * @param fileName Nombre del archivo
     * @return Extensión con punto (ej: ".txt")
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot);
        }
        return "";
    }
}
