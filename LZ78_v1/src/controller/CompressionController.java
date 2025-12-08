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
    private File currentCompressedFile; // Para guardar referencia al archivo .lz78
    
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
        fileChooser.setDialogTitle("Seleccionar archivo para comprimir");
        
        // Filtro para todos los archivos con extensiones comunes
        FileNameExtensionFilter textFilter = new FileNameExtensionFilter(
                "Archivos de texto (*.txt)", "txt");
        FileNameExtensionFilter wordFilter = new FileNameExtensionFilter(
                "Documentos Word (*.doc, *.docx)", "doc", "docx");
        FileNameExtensionFilter excelFilter = new FileNameExtensionFilter(
                "Hojas de cálculo Excel (*.xls, *.xlsx)", "xls", "xlsx");
        FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter(
                "Documentos PDF (*.pdf)", "pdf");
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                "Imágenes (*.jpg, *.png, *.gif, *.bmp)", "jpg", "jpeg", "png", "gif", "bmp");
        
        fileChooser.addChoosableFileFilter(textFilter);
        fileChooser.addChoosableFileFilter(wordFilter);
        fileChooser.addChoosableFileFilter(excelFilter);
        fileChooser.addChoosableFileFilter(pdfFilter);
        fileChooser.addChoosableFileFilter(imageFilter);
        
        // Habilitar el filtro "All Files" predeterminado de JFileChooser
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
        
        int result = fileChooser.showOpenDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            
            try {
                String fileName = currentFile.getName();
                String extension = getFileExtension(fileName).toLowerCase();
                
                // Detectar si es archivo de texto o binario
                boolean isTextFile = extension.equals("txt") || extension.equals("log") || 
                                    extension.equals("csv") || extension.equals("xml") ||
                                    extension.equals("json") || extension.equals("html");
                
                if (isTextFile) {
                    // Leer como texto y mostrar
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.FileReader(currentFile));
                    StringBuilder content = new StringBuilder();
                    String line;
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null && lineCount < 500) {
                        content.append(line).append("\n");
                        lineCount++;
                    }
                    if (lineCount >= 500) {
                        content.append("\n[... archivo demasiado grande, mostrando primeras 500 líneas ...]");
                    }
                    reader.close();
                    view.setTextArea(content.toString());
                } else {
                    // Archivo binario - mostrar información
                    String fileInfo = "ARCHIVO BINARIO: " + fileName + "\n\n";
                    fileInfo += "Tipo: " + getFileTypeDescription(extension) + "\n";
                    fileInfo += "Tamaño: " + formatBytes(currentFile.length()) + "\n\n";
                    fileInfo += "Este es un archivo binario y no puede mostrarse como texto.\n";
                    fileInfo += "Presione 'Comprimir' para comprimirlo con LZ78.";
                    view.setTextArea(fileInfo);
                }
                
                view.setFileInfo("Archivo: " + fileName + 
                                " (Tamaño: " + formatBytes(currentFile.length()) + ")");
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
        
        FileNameExtensionFilter lz78Filter = new FileNameExtensionFilter(
                "Archivos LZ78 (*.lz78)", "lz78");
        fileChooser.addChoosableFileFilter(lz78Filter);
        
        // Habilitar el filtro "All Files" y usarlo por defecto
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(lz78Filter); // Usar .lz78 por defecto para descomprimir
        
        int result = fileChooser.showOpenDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File compressedFile = fileChooser.getSelectedFile();
            currentCompressedFile = compressedFile; // Guardar referencia
            
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
                            // Detectar si el contenido es texto o binario
                            String originalText = currentResult.getOriginalText();
                            boolean isPrintable = isPrintableText(originalText);
                            
                            // Obtener información del archivo original
                            String originalExt = currentResult.getOriginalFileExtension();
                            String fileType = getFileTypeDescription(originalExt != null ? originalExt : "");
                            
                            if (isPrintable) {
                                // Mostrar diccionario + contenido para archivos de texto
                                String fullContent = buildDecompressedContent(currentResult);
                                view.setTextArea(fullContent);
                            } else {
                                // Para archivos binarios, solo mostrar información
                                String info = "ARCHIVO BINARIO DESCOMPRIMIDO\n\n";
                                info += "Archivo comprimido: " + compressedFile.getName() + "\n";
                                info += "Tipo de archivo: " + fileType + "\n";
                                info += "Extensión original: ." + (originalExt != null ? originalExt : "desconocida") + "\n";
                                info += "Tamaño descomprimido: " + formatBytes(currentResult.getOriginalSize()) + "\n\n";
                                info += "Este es un archivo binario y no puede mostrarse como texto.\n";
                                info += "Presione 'Guardar Descomprimido' para restaurar el archivo original.\n\n";
                                info += "Diccionario:\n";
                                info += currentResult.getDictionary().getDictionaryAsString();
                                view.setTextArea(info);
                            }
                            
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
        
        // Obtener la extensión original del archivo (guardada en el .lz78)
        String originalExtension = currentResult.getOriginalFileExtension();
        if (originalExtension == null || originalExtension.isEmpty()) {
            originalExtension = "txt"; // Por defecto si no hay extensión guardada
        }
        
        // Generar nombre base del archivo
        String baseName = "descomprimido";
        if (currentCompressedFile != null) {
            baseName = currentCompressedFile.getName().replaceFirst("[.][^.]+$", "");
        }
        
        // Agregar filtro específico según la extensión detectada
        String filterDescription = getFileTypeDescription(originalExtension);
        FileNameExtensionFilter specificFilter = new FileNameExtensionFilter(
                filterDescription + " (*." + originalExtension + ")", originalExtension);
        fileChooser.addChoosableFileFilter(specificFilter);
        
        // Habilitar el filtro "All Files" predeterminado
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
        
        // Generar nombre por defecto con la extensión original detectada
        String defaultName = baseName + "_descomprimido." + originalExtension;
        fileChooser.setSelectedFile(new File(defaultName));
        
        int result = fileChooser.showSaveDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            
            try {
                // Detectar si el archivo es texto o binario según la extensión
                String originalText = currentResult.getOriginalText();
                
                // Extensiones que son definitivamente texto
                boolean isTextByExtension = originalExtension.equals("txt") || 
                                           originalExtension.equals("log") ||
                                           originalExtension.equals("csv") ||
                                           originalExtension.equals("xml") ||
                                           originalExtension.equals("json") ||
                                           originalExtension.equals("html") ||
                                           originalExtension.equals("htm");
                
                // Para archivos de texto: guardar con diccionario
                // Para archivos binarios: guardar SOLO los bytes originales
                if (isTextByExtension) {
                    // Archivos de texto: incluir diccionario
                    String fullContent = buildDecompressedContent(currentResult);
                    decompressor.saveDecompressedTextFile(outputFile, fullContent);
                    
                    view.showSuccess("Archivo de texto guardado con diccionario incluido:\n" + 
                                    outputFile.getAbsolutePath());
                } else {
                    // Archivos binarios: SOLO bytes originales (sin diccionario)
                    decompressor.saveDecompressedFile(outputFile, originalText);
                    
                    view.showSuccess("Archivo binario restaurado exitosamente:\n" + 
                                    outputFile.getAbsolutePath() + "\n\n" +
                                    "El archivo original ha sido completamente restaurado.");
                }
                
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
     * Construye el contenido completo mostrando diccionario y texto original
     */
    private String buildDecompressedContent(CompressionResult result) {
        StringBuilder content = new StringBuilder();
        
        // Agregar el diccionario
        content.append("========================================\n");
        content.append("       DICCIONARIO GENERADO (LZ78)      \n");
        content.append("========================================\n\n");
        content.append(result.getDictionary().getDictionaryAsString());
        content.append("\n\n");
        
        // Agregar separador
        content.append("========================================\n");
        content.append("         CONTENIDO ORIGINAL             \n");
        content.append("========================================\n\n");
        
        // Agregar el texto original
        content.append(result.getOriginalText());
        
        return content.toString();
    }
    
    /**
     * Obtiene la extensión de un archivo
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1);
        }
        return "";
    }
    
    /**
     * Obtiene una descripción del tipo de archivo según su extensión
     */
    private String getFileTypeDescription(String extension) {
        switch (extension.toLowerCase()) {
            case "txt": return "Archivo de texto plano";
            case "doc":
            case "docx": return "Documento de Microsoft Word";
            case "xls":
            case "xlsx": return "Hoja de cálculo de Microsoft Excel";
            case "ppt":
            case "pptx": return "Presentación de Microsoft PowerPoint";
            case "pdf": return "Documento PDF";
            case "jpg":
            case "jpeg": return "Imagen JPEG";
            case "png": return "Imagen PNG";
            case "gif": return "Imagen GIF";
            case "bmp": return "Imagen BMP";
            case "zip": return "Archivo comprimido ZIP";
            case "rar": return "Archivo comprimido RAR";
            case "mp3": return "Archivo de audio MP3";
            case "mp4": return "Archivo de video MP4";
            case "avi": return "Archivo de video AVI";
            case "exe": return "Archivo ejecutable";
            case "dll": return "Biblioteca de enlace dinámico";
            default: return "Archivo binario (" + extension.toUpperCase() + ")";
        }
    }
    
    /**
     * Formatea bytes a una representación legible
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " bytes";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Detecta si un texto es imprimible (texto plano) o binario
     */
    private boolean isPrintableText(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        
        int printableCount = 0;
        int totalCount = Math.min(text.length(), 1000); // Analizar primeros 1000 caracteres
        
        for (int i = 0; i < totalCount; i++) {
            char c = text.charAt(i);
            // Considerar imprimible: caracteres ASCII imprimibles, tabs, newlines
            if ((c >= 32 && c < 127) || c == '\n' || c == '\r' || c == '\t') {
                printableCount++;
            }
        }
        
        // Si más del 85% son caracteres imprimibles, es texto
        return (printableCount * 100.0 / totalCount) > 85;
    }
    
    /**
     * Limpia el estado actual
     */
    public void clear() {
        currentResult = null;
        currentFile = null;
        currentCompressedFile = null;
    }
}