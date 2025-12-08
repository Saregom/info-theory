package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa el algoritmo de descompresión LZ78
 */
public class LZ78Decompressor {
    
    /**
     * Descomprime un archivo .lz78
     */
    public CompressionResult decompress(File compressedFile) {
        CompressionResult result = new CompressionResult();
        
        try {
            // Validar archivo
            if (!compressedFile.exists() || !compressedFile.canRead()) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo no existe o no se puede leer");
                return result;
            }
            
            if (compressedFile.length() == 0) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo está vacío");
                return result;
            }
            
            // Leer datos comprimidos
            byte[] compressedData = readCompressedFile(compressedFile);
            result.setCompressedData(compressedData);
            result.setCompressedSize(compressedFile.length());
            
            // Decodificar pares
            List<LZ78Compressor.EncodedPair> encoded = decodeFromBytes(compressedData);
            
            // Descomprimir
            Dictionary dictionary = new Dictionary();
            String decompressedText = decompressText(encoded, dictionary);
            
            result.setOriginalText(decompressedText);
            result.setOriginalSize(decompressedText.getBytes("UTF-8").length);
            result.setDictionary(dictionary);
            
        } catch (IOException e) {
            result.setSuccess(false);
            result.setErrorMessage("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Error durante la descompresión: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Lee el archivo comprimido completo
     */
    private byte[] readCompressedFile(File file) throws IOException {
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }
        return data;
    }
    
    /**
     * Decodifica los bytes a lista de pares usando codificación variable
     */
    private List<LZ78Compressor.EncodedPair> decodeFromBytes(byte[] data) throws IOException {
        List<LZ78Compressor.EncodedPair> encoded = new ArrayList<>();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        
        try {
            // Leer número de pares con codificación variable
            int count = readVariableInt(dis);
            
            // Leer cada par
            for (int i = 0; i < count; i++) {
                int index = readVariableInt(dis);
                char character = dis.readChar();
                encoded.add(new LZ78Compressor.EncodedPair(index, character));
            }
        } catch (EOFException e) {
            throw new IOException("Archivo comprimido corrupto o formato incorrecto");
        }
        
        return encoded;
    }
    
    /**
     * Lee un entero con codificación de longitud variable
     */
    private int readVariableInt(DataInputStream dis) throws IOException {
        int firstByte = dis.readUnsignedByte();
        
        // Si es menor a 128, es un valor de 1 byte
        if (firstByte < 128) {
            return firstByte;
        }
        // Si es 0xFF, es un entero completo de 4 bytes
        else if (firstByte == 0xFF) {
            return dis.readInt();
        }
        // Si no, es un valor de 2 bytes
        else {
            int secondByte = dis.readUnsignedByte();
            return ((firstByte & 0x7F) << 8) | secondByte;
        }
    }
    
    /**
     * Descomprime el texto usando LZ78
     */
    private String decompressText(List<LZ78Compressor.EncodedPair> encoded, Dictionary dictionary) {
        StringBuilder decompressed = new StringBuilder();
        
        for (LZ78Compressor.EncodedPair pair : encoded) {
            String phrase = "";
            
            // Si el índice es 0, es una cadena nueva
            if (pair.getIndex() == 0) {
                phrase = String.valueOf(pair.getCharacter());
            } else {
                // Obtener la frase del diccionario y agregar el nuevo carácter
                String dictPhrase = dictionary.getPhrase(pair.getIndex());
                if (dictPhrase != null) {
                    phrase = dictPhrase + pair.getCharacter();
                } else {
                    phrase = String.valueOf(pair.getCharacter());
                }
            }
            
            // Agregar al diccionario si no es el último carácter nulo
            if (pair.getCharacter() != '\0') {
                dictionary.addEntry(phrase);
                decompressed.append(phrase);
            } else {
                // Si es carácter nulo, solo agregar lo que está en el diccionario
                if (pair.getIndex() != 0) {
                    String dictPhrase = dictionary.getPhrase(pair.getIndex());
                    if (dictPhrase != null) {
                        decompressed.append(dictPhrase);
                    }
                }
            }
        }
        
        return decompressed.toString();
    }
    
    /**
     * Guarda el texto descomprimido en un archivo
     */
    public void saveDecompressedFile(File outputFile, String text) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"))) {
            writer.write(text);
        }
    }
}