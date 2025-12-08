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
        result.setCompressedSize(encodedData.size() * 6L);

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
}
