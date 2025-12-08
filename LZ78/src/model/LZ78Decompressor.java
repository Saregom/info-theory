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
            // Validar archivo existe
            if (!compressedFile.exists()) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo no existe");
                return result;
            }
            
            // Validar que se puede leer
            if (!compressedFile.canRead()) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo no se puede leer. Verifique los permisos.");
                return result;
            }
            
            // Validar que no esté vacío
            if (compressedFile.length() == 0) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo está vacío");
                return result;
            }
            
            // Validar tamaño mínimo (header + datos)
            if (compressedFile.length() < 10) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo es demasiado pequeño. Posiblemente esté corrupto.");
                return result;
            }
            
            result.setCompressedSize(compressedFile.length());
            
            // Leer y parsear el archivo completo
            try (DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(compressedFile)))) {
                
                // Leer y validar header
                byte[] header = new byte[4];
                dis.readFully(header);
                String headerStr = new String(header);
                
                if (!headerStr.equals("LZ78")) {
                    result.setSuccess(false);
                    result.setErrorMessage("Formato de archivo inválido. No es un archivo LZ78.");
                    return result;
                }
                
                // Leer versión
                int version = dis.readUnsignedByte();
                if (version != 1) {
                    result.setSuccess(false);
                    result.setErrorMessage("Versión de archivo no soportada: " + version);
                    return result;
                }
                
                // Leer la extensión del archivo original
                int extensionLength = dis.readUnsignedByte();
                String originalExtension = "";
                if (extensionLength > 0) {
                    byte[] extBytes = new byte[extensionLength];
                    dis.readFully(extBytes);
                    originalExtension = new String(extBytes);
                }
                result.setOriginalFileExtension(originalExtension);
                
                // Leer tamaño del diccionario
                int dictSize = dis.readInt();
                
                // Leer y deserializar el diccionario
                byte[] dictData = new byte[dictSize];
                dis.readFully(dictData);
                Dictionary dictionary = deserializeDictionary(dictData);
                
                // Leer el resto como datos comprimidos
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                byte[] compressedData = baos.toByteArray();
                
                result.setCompressedData(compressedData);
                
                // Decodificar pares
                List<LZ78Compressor.EncodedPair> encoded = decodeFromBytes(compressedData);
                
                // Descomprimir usando el diccionario leído
                String decompressedText = decompressText(encoded, dictionary);
                
                result.setOriginalText(decompressedText);
                // Calcular tamaño usando ISO-8859-1 para mantener bytes 1:1
                result.setOriginalSize(decompressedText.getBytes("ISO-8859-1").length);
                result.setDictionary(dictionary);
                result.setSuccess(true);
            }
            
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
     * Deserializa el diccionario desde bytes
     */
    private Dictionary deserializeDictionary(byte[] data) throws IOException {
        Dictionary dictionary = new Dictionary();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        
        // Leer número de entradas
        int size = readVariableInt(dis);
        
        // Leer cada entrada
        for (int i = 0; i < size; i++) {
            // Leer índice (lo leemos del archivo pero el diccionario genera sus propios índices)
            readVariableInt(dis); // índice del archivo
            
            // Leer longitud de la frase
            int phraseLength = readVariableInt(dis);
            
            // Leer la frase byte por byte (ISO-8859-1)
            StringBuilder phrase = new StringBuilder();
            for (int j = 0; j < phraseLength; j++) {
                // Leer byte y convertir a char (0-255)
                phrase.append((char) dis.readUnsignedByte());
            }
            
            // Agregar al diccionario
            dictionary.addEntry(phrase.toString());
        }
        
        return dictionary;
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
                // Leer carácter como byte (0-255) para preservar binarios
                char character = (char) dis.readUnsignedByte();
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
     * Guarda el archivo descomprimido en formato binario puro
     * IMPORTANTE: Para archivos binarios (imágenes, Word, PDF, etc.)
     * Solo guarda los bytes originales sin agregar nada más
     * Usa ISO-8859-1 para mantener la correspondencia 1:1 con bytes
     */
    public void saveDecompressedFile(File outputFile, String text) throws IOException {
        // Convertir el string a bytes usando ISO-8859-1 para preservar datos binarios
        byte[] bytes = text.getBytes("ISO-8859-1");
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(bytes);
            fos.flush(); // Asegurar que todos los datos se escriban
        }
    }
    
    /**
     * Guarda archivo de texto con contenido legible
     * IMPORTANTE: Para archivos de texto (.txt, .log, etc.)
     * Puede incluir el diccionario y otro contenido adicional
     */
    public void saveDecompressedTextFile(File outputFile, String text) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"))) {
            writer.write(text);
        }
    }
}