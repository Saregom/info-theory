package model;

import java.util.List;

/**
 * Almacena los resultados de la compresión/descompresión
 */
public class CompressionResult {
    private List<EncodedPair> encodedData;
    private Dictionary dictionary;
    private long originalSize;
    private long compressedSize;
    private String originalText;
    private String decompressedText;

    /**
     * Representa un par (índice, carácter) en la codificación LZ78
     */
    public static class EncodedPair {
        private int index;
        private char character;

        public EncodedPair(int index, char character) {
            this.index = index;
            this.character = character;
        }

        public int getIndex() {
            return index;
        }

        public char getCharacter() {
            return character;
        }

        @Override
        public String toString() {
            return "(" + index + "," + character + ")";
        }
    }

    public CompressionResult() {
        this.encodedData = null;
        this.dictionary = null;
        this.originalSize = 0;
        this.compressedSize = 0;
    }

    // Getters y Setters
    public List<EncodedPair> getEncodedData() {
        return encodedData;
    }

    public void setEncodedData(List<EncodedPair> encodedData) {
        this.encodedData = encodedData;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public long getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(long originalSize) {
        this.originalSize = originalSize;
    }

    public long getCompressedSize() {
        return compressedSize;
    }

    public void setCompressedSize(long compressedSize) {
        this.compressedSize = compressedSize;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getDecompressedText() {
        return decompressedText;
    }

    public void setDecompressedText(String decompressedText) {
        this.decompressedText = decompressedText;
    }

    /**
     * Calcula el porcentaje de compresión
     */
    public double getCompressionPercentage() {
        if (originalSize == 0) {
            return 0.0;
        }
        return ((double) (originalSize - compressedSize) / originalSize) * 100.0;
    }

    /**
     * Calcula la tasa de compresión
     */
    public double getCompressionRatio() {
        if (compressedSize == 0) {
            return 0.0;
        }
        return (double) originalSize / compressedSize;
    }

    /**
     * Retorna las estadísticas como String formateado
     */
    public String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("ESTADÍSTICAS DE COMPRESIÓN\n");
        sb.append("==========================\n\n");
        sb.append(String.format("Tamaño original:      %,d bytes\n", originalSize));
        sb.append(String.format("Tamaño comprimido:    %,d bytes\n", compressedSize));
        sb.append(String.format("Porcentaje compresión: %.2f%%\n", getCompressionPercentage()));
        sb.append(String.format("Ratio de compresión:  %.2f:1\n", getCompressionRatio()));
        sb.append(String.format("Entradas diccionario: %d\n", dictionary != null ? dictionary.size() : 0));
        sb.append(String.format("Pares codificados:    %d\n", encodedData != null ? encodedData.size() : 0));
        
        return sb.toString();
    }

    /**
     * Retorna los datos codificados como String
     */
    public String getEncodedDataString() {
        if (encodedData == null || encodedData.isEmpty()) {
            return "Sin datos codificados";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("DATOS CODIFICADOS\n");
        sb.append("=================\n\n");
        
        int count = 0;
        for (EncodedPair pair : encodedData) {
            sb.append(pair.toString());
            count++;
            if (count % 10 == 0) {
                sb.append("\n");
            } else {
                sb.append(" ");
            }
        }
        
        return sb.toString();
    }
}
