package model;

import java.util.List;

/**
 * Implementa el algoritmo de descompresión LZ78
 */
public class LZ78Decompressor {
    
    /**
     * Descomprime datos codificados con LZ78
     * @param encodedData Lista de pares codificados
     * @return Resultado con el texto descomprimido
     * @throws IllegalArgumentException si los datos están vacíos o son inválidos
     */
    public CompressionResult decompress(List<CompressionResult.EncodedPair> encodedData) {
        if (encodedData == null || encodedData.isEmpty()) {
            throw new IllegalArgumentException("Los datos codificados no pueden estar vacíos");
        }

        CompressionResult result = new CompressionResult();
        Dictionary dictionary = new Dictionary();
        StringBuilder decompressedText = new StringBuilder();

        for (CompressionResult.EncodedPair pair : encodedData) {
            int index = pair.getIndex();
            char character = pair.getCharacter();

            String sequence;
            if (index == 0) {
                // Si el índice es 0, la secuencia es solo el carácter
                sequence = String.valueOf(character);
            } else {
                // Obtener la secuencia del diccionario y agregar el carácter
                String prefix = dictionary.getSequence(index);
                if (prefix == null) {
                    throw new IllegalArgumentException("Datos corruptos: índice " + index + " no encontrado");
                }
                sequence = prefix + character;
            }

            // Agregar al texto descomprimido
            decompressedText.append(sequence);

            // Agregar al diccionario
            dictionary.add(sequence);
        }

        result.setDecompressedText(decompressedText.toString());
        result.setDictionary(dictionary);
        result.setEncodedData(encodedData);
        result.setOriginalSize(decompressedText.length());
        
        // Calcular el tamaño comprimido usando el mismo método que el compresor
        long compressedSize = calculateCompressedSize(encodedData, dictionary.size());
        result.setCompressedSize(compressedSize);

        return result;
    }

    /**
     * Reconstruye el diccionario a partir de datos codificados
     * @param encodedData Lista de pares codificados
     * @return Diccionario reconstruido
     */
    public Dictionary rebuildDictionary(List<CompressionResult.EncodedPair> encodedData) {
        Dictionary dictionary = new Dictionary();

        for (CompressionResult.EncodedPair pair : encodedData) {
            int index = pair.getIndex();
            char character = pair.getCharacter();

            String sequence;
            if (index == 0) {
                sequence = String.valueOf(character);
            } else {
                String prefix = dictionary.getSequence(index);
                if (prefix == null) {
                    throw new IllegalArgumentException("Datos corruptos: índice " + index + " no encontrado");
                }
                sequence = prefix + character;
            }

            dictionary.add(sequence);
        }

        return dictionary;
    }

    /**
     * Calcula el tamaño real del archivo comprimido en bytes
     * usando codificación de longitud variable basada en el tamaño del diccionario
     * @param encodedData Lista de pares codificados
     * @param dictionarySize Tamaño del diccionario
     * @return Tamaño estimado en bytes
     */
    private long calculateCompressedSize(List<CompressionResult.EncodedPair> encodedData, int dictionarySize) {
        if (encodedData.isEmpty()) {
            return 0;
        }

        // Calcular bits necesarios para el índice más grande
        // log2(dictionarySize) redondeado hacia arriba
        int maxIndex = dictionarySize;
        int bitsPerIndex = (int) Math.ceil(Math.log(maxIndex + 1) / Math.log(2));
        
        // Cada carácter usa 8 bits (1 byte)
        int bitsPerChar = 8;
        
        // Total de bits para todos los pares
        long totalBits = (long) encodedData.size() * (bitsPerIndex + bitsPerChar);
        
        // Convertir a bytes (redondear hacia arriba)
        long totalBytes = (totalBits + 7) / 8;
        
        return totalBytes;
    }
}
