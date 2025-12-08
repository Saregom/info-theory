package controller;

import model.*;
import view.MainWindow;
import view.DictionaryViewer;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Controlador principal que maneja la lógica de la aplicación
 */
public class CompressionController {
    private MainWindow view;
    private LZ78Compressor compressor;
    private LZ78Decompressor decompressor;
    private CompressionResult currentResult;
    private File currentFile;
    
    public CompressionController(MainWindow view) {
        this.view = view;
        this.compressor = new LZ78Compressor();
        this.decompressor = new LZ78Decompressor();
    }
    
    /**
     * Carga un archivo de texto para comprimir
     */
    public void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de texto");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de texto (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            
            try {
                // Leer el archivo y mostrarlo
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.FileReader(currentFile));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                
                view.setTextArea(content.toString());
                view.setFileInfo("Archivo: " + currentFile.getName() + 
                                " (Tamaño: " + currentFile.length() + " bytes)");
                view.setStatus("Archivo cargado correctamente");
                view.enableCompress(true);
                view.enableViewDictionary(false);
                view.enableSaveCompressed(false);
                view.enableSaveDecompressed(false);
                
            } catch (Exception e) {
                view.showError("Error al leer el archivo: " + e.getMessage());
                view.setStatus("Error al cargar archivo");
            }
        }
    }
    
    /**
     * Comprime el archivo cargado
     */
    public void compress() {
        if (currentFile == null) {
            view.showError("Primero debe cargar un archivo");
            return;
        }
        
        view.setStatus("Comprimiendo archivo...");
        
        // Ejecutar compresión en un hilo separado para no bloquear la interfaz
        SwingWorker<CompressionResult, Void> worker = new SwingWorker<CompressionResult, Void>() {
            @Override
            protected CompressionResult doInBackground() {
                return compressor.compress(currentFile);
            }
            
            @Override
            protected void done() {
                try {
                    currentResult = get();
                    
                    if (currentResult.isSuccess()) {
                        view.setStatus("Compresión completada");
                        view.enableSaveCompressed(true);
                        view.enableViewDictionary(true);
                        view.showStatistics(currentResult.getStatistics());
                    } else {
                        view.showError(currentResult.getErrorMessage());
                        view.setStatus("Error en la compresión");
                    }
                } catch (Exception e) {
                    view.showError("Error durante la compresión: " + e.getMessage());
                    view.setStatus("Error");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Guarda el archivo comprimido
     */
    public void saveCompressed() {
        if (currentResult == null || !currentResult.isSuccess()) {
            view.showError("No hay datos comprimidos para guardar");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo comprimido");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos LZ78 (*.lz78)", "lz78");
        fileChooser.setFileFilter(filter);
        
        // Sugerir nombre de archivo
        if (currentFile != null) {
            String suggestedName = currentFile.getName().replaceFirst("[.][^.]+$", "") + ".lz78";
            fileChooser.setSelectedFile(new File(suggestedName));
        }
        
        int result = fileChooser.showSaveDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            
            // Asegurar extensión .lz78
            if (!outputFile.getName().toLowerCase().endsWith(".lz78")) {
                outputFile = new File(outputFile.getAbsolutePath() + ".lz78");
            }
            
            try {
                compressor.saveCompressedFile(outputFile, currentResult);
                view.showSuccess("Archivo comprimido guardado exitosamente en:\n" + 
                                outputFile.getAbsolutePath());
                view.setStatus("Archivo guardado");
            } catch (Exception e) {
                view.showError("Error al guardar el archivo: " + e.getMessage());
                view.setStatus("Error al guardar");
            }
        }
    }
    
    /**
     * Carga y descomprime un archivo .lz78
     */
    public void loadAndDecompress() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo comprimido");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos LZ78 (*.lz78)", "lz78");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File compressedFile = fileChooser.getSelectedFile();
            
            view.setStatus("Descomprimiendo archivo...");
            
            // Ejecutar descompresión en un hilo separado
            SwingWorker<CompressionResult, Void> worker = new SwingWorker<CompressionResult, Void>() {
                @Override
                protected CompressionResult doInBackground() {
                    return decompressor.decompress(compressedFile);
                }
                
                @Override
                protected void done() {
                    try {
                        currentResult = get();
                        
                        if (currentResult.isSuccess()) {
                            view.setTextArea(currentResult.getOriginalText());
                            view.setFileInfo("Archivo descomprimido: " + compressedFile.getName());
                            view.setStatus("Descompresión completada");
                            view.enableSaveDecompressed(true);
                            view.enableViewDictionary(true);
                            view.showStatistics(currentResult.getStatistics());
                        } else {
                            view.showError(currentResult.getErrorMessage());
                            view.setStatus("Error en la descompresión");
                        }
                    } catch (Exception e) {
                        view.showError("Error durante la descompresión: " + e.getMessage());
                        view.setStatus("Error");
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    /**
     * Guarda el archivo descomprimido
     */
    public void saveDecompressed() {
        if (currentResult == null || currentResult.getOriginalText() == null) {
            view.showError("No hay texto descomprimido para guardar");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo descomprimido");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de texto (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(new File("descomprimido.txt"));
        
        int result = fileChooser.showSaveDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            
            // Asegurar extensión .txt
            if (!outputFile.getName().toLowerCase().endsWith(".txt")) {
                outputFile = new File(outputFile.getAbsolutePath() + ".txt");
            }
            
            try {
                decompressor.saveDecompressedFile(outputFile, currentResult.getOriginalText());
                view.showSuccess("Archivo descomprimido guardado exitosamente en:\n" + 
                                outputFile.getAbsolutePath());
                view.setStatus("Archivo guardado");
            } catch (Exception e) {
                view.showError("Error al guardar el archivo: " + e.getMessage());
                view.setStatus("Error al guardar");
            }
        }
    }
    
    /**
     * Muestra el diccionario en una ventana separada
     */
    public void showDictionary() {
        if (currentResult == null || currentResult.getDictionary() == null) {
            view.showError("No hay diccionario disponible");
            return;
        }
        
        DictionaryViewer viewer = new DictionaryViewer(view, currentResult.getDictionary());
        viewer.setVisible(true);
    }
    
    /**
     * Limpia el estado actual
     */
    public void clear() {
        currentResult = null;
        currentFile = null;
    }
}