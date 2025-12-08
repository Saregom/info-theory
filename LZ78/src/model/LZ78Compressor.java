package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementa el algoritmo de compresión LZ78
 */
public class LZ78Compressor {
    
    /**
     * Comprime un texto usando el algoritmo LZ78
     * @param text Texto a comprimir
     * @return Resultado de la compresión
     * @throws IllegalArgumentException si el texto está vacío
     */
    public CompressionResult compress(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }

        CompressionResult result = new CompressionResult();
        Dictionary dictionary = new Dictionary();
        List<CompressionResult.EncodedPair> encodedData = new ArrayList<>();

        String current = "";
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String next = current + c;

            if (dictionary.contains(next)) {
                current = next;
            } else {
                // Obtener el índice del prefijo (0 si no existe)
                int index = current.isEmpty() ? 0 : dictionary.getIndex(current);
                
                // Agregar par (índice, carácter)
                encodedData.add(new CompressionResult.EncodedPair(index, c));
                
                // Agregar la nueva secuencia al diccionario
                dictionary.add(next);
                
                // Reiniciar la secuencia actual
                current = "";
            }
        }

        // Si queda algo en current al final
        if (!current.isEmpty()) {
            // Obtener el índice del último prefijo
            String prefix = current.substring(0, current.length() - 1);
            int index = prefix.isEmpty() ? 0 : dictionary.getIndex(prefix);
            char lastChar = current.charAt(current.length() - 1);
            
            encodedData.add(new CompressionResult.EncodedPair(index, lastChar));
        }

        // Calcular tamaños
        result.setOriginalText(text);
        result.setEncodedData(encodedData);
        result.setDictionary(dictionary);
        result.setOriginalSize(text.length());
        
        // Calcular tamaño comprimido de forma más precisa
        // Usar codificación de tamaño variable basada en el tamaño del diccionario
        long compressedSize = 0;
        int maxIndex = dictionary.size();
        
        // Calcular bits necesarios para representar los índices
        int bitsForIndex = maxIndex > 0 ? (int) Math.ceil(Math.log(maxIndex + 1) / Math.log(2)) : 1;
        if (bitsForIndex < 1) bitsForIndex = 1;
        
        // Cada par: índice (bits variables) + carácter (8 bits)
        int bitsPerPair = bitsForIndex + 8;
        long totalBits = (long) encodedData.size() * bitsPerPair;
        
        // Convertir a bytes (redondear hacia arriba)
        compressedSize = (totalBits + 7) / 8;
        
        // Añadir overhead del formato (número mágico, tamaños, etc.)
        compressedSize += 50; // Overhead aproximado del encabezado
        
        result.setCompressedSize(compressedSize);

        return result;
    }

    /**
     * Valida que un archivo/texto sea procesable
     * @param text Texto a validar
     * @return true si es válido
     */
    public boolean validateText(String text) {
        return text != null && !text.isEmpty() && text.trim().length() > 0;
    }
}
