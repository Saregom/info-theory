package model;

/**
 * Clase que encapsula los resultados de la compresión/descompresión
 */
public class CompressionResult {
    private boolean success;
    private String errorMessage;
    
    // Datos originales
    private String originalText;
    private long originalSize;
    
    // Datos comprimidos
    private byte[] compressedData;
    private long compressedSize;
    
    // Diccionario generado
    private Dictionary dictionary;
    
    // Extensión del archivo original
    private String originalFileExtension;
    
    public CompressionResult() {
        this.success = true;
        this.errorMessage = "";
    }
    
    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getOriginalText() {
        return originalText;
    }
    
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }
    
    public long getOriginalSize() {
        return originalSize;
    }
    
    public void setOriginalSize(long originalSize) {
        this.originalSize = originalSize;
    }
    
    public byte[] getCompressedData() {
        return compressedData;
    }
    
    public void setCompressedData(byte[] compressedData) {
        this.compressedData = compressedData;
    }
    
    public long getCompressedSize() {
        return compressedSize;
    }
    
    public void setCompressedSize(long compressedSize) {
        this.compressedSize = compressedSize;
    }
    
    public Dictionary getDictionary() {
        return dictionary;
    }
    
    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }
    
    public String getOriginalFileExtension() {
        return originalFileExtension;
    }
    
    public void setOriginalFileExtension(String originalFileExtension) {
        this.originalFileExtension = originalFileExtension;
    }
    
    /**
     * Calcula la tasa de compresión
     */
    public double getCompressionRatio() {
        if (originalSize == 0) return 0.0;
        return (double) compressedSize / originalSize;
    }
    
    /**
     * Calcula el porcentaje de espacio ahorrado
     */
    public double getSpaceSavings() {
        if (originalSize == 0) return 0.0;
        return (1.0 - getCompressionRatio()) * 100.0;
    }
    
    /**
     * Genera un reporte de estadísticas
     */
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== ESTADÍSTICAS DE COMPRESIÓN ==========\n\n");
        
        sb.append("Tamaño Original:    ").append(formatBytes(originalSize)).append("\n");
        sb.append("Tamaño Comprimido:  ").append(formatBytes(compressedSize)).append("\n");
        sb.append("Tamaño Diccionario: ").append(dictionary != null ? dictionary.size() : 0).append(" entradas\n\n");
        
        sb.append("Ratio de Compresión: ").append(String.format("%.2f%%", getCompressionRatio() * 100)).append("\n");
        sb.append("Espacio Ahorrado:    ").append(String.format("%.2f%%", getSpaceSavings())).append("\n");
        sb.append("Reducción de Tamaño: ").append(formatBytes(originalSize - compressedSize)).append("\n");
        
        sb.append("\n===============================================");
        
        return sb.toString();
    }
    
    /**
     * Formatea bytes a una representación legible
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " bytes";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
}
