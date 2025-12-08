package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa el algoritmo de compresión LZ78
 */
public class LZ78Compressor {
    
    /**
     * Clase interna que representa un par codificado (índice, carácter)
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
            return "(" + index + ", '" + character + "')";
        }
    }
    
    /**
     * Comprime un archivo usando el algoritmo LZ78
     */
    public CompressionResult compress(File inputFile) {
        CompressionResult result = new CompressionResult();
        
        try {
            // Validar archivo
            if (!inputFile.exists() || !inputFile.canRead()) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo no existe o no se puede leer");
                return result;
            }
            
            // Leer el contenido del archivo
            String text = readFile(inputFile);
            result.setOriginalText(text);
            result.setOriginalSize(inputFile.length());
            
            // Comprimir el texto
            Dictionary dictionary = new Dictionary();
            List<EncodedPair> encoded = compressText(text, dictionary);
            
            // Convertir a bytes
            byte[] compressedData = encodeToBytes(encoded);
            result.setCompressedData(compressedData);
            result.setCompressedSize(compressedData.length);
            result.setDictionary(dictionary);
            
            result.setSuccess(true);
            
        } catch (IOException e) {
            result.setSuccess(false);
            result.setErrorMessage("Error al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Error durante la compresión: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Lee el contenido completo de un archivo
     */
    private String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    /**
     * Comprime el texto usando el algoritmo LZ78
     */
    private List<EncodedPair> compressText(String text, Dictionary dictionary) {
        List<EncodedPair> encoded = new ArrayList<>();
        String current = "";
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String temp = current + c;
            
            if (dictionary.contains(temp)) {
                // Si la cadena existe en el diccionario, continuamos
                current = temp;
            } else {
                // Si no existe, emitimos el par (índice, carácter)
                int index = 0;
                if (!current.isEmpty()) {
                    index = dictionary.getIndex(current);
                }
                
                encoded.add(new EncodedPair(index, c));
                
                // Agregamos la nueva cadena al diccionario
                dictionary.addEntry(temp);
                
                // Reiniciamos la cadena actual
                current = "";
            }
        }
        
        // Si queda algo en current al final, lo emitimos
        if (!current.isEmpty()) {
            int index = dictionary.getIndex(current);
            encoded.add(new EncodedPair(index, '\0')); // Usamos null character como marcador
        }
        
        return encoded;
    }
    
    /**
     * Codifica la lista de pares a bytes usando codificación variable
     * para optimizar el tamaño
     */
    private byte[] encodeToBytes(List<EncodedPair> encoded) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // Escribir el número de pares (necesario para decodificar)
        writeVariableInt(dos, encoded.size());
        
        // Escribir cada par con codificación optimizada
        for (EncodedPair pair : encoded) {
            // Usar codificación variable para índices pequeños
            writeVariableInt(dos, pair.getIndex());
            // Los caracteres siguen siendo 2 bytes (UTF-16)
            dos.writeChar(pair.getCharacter());
        }
        
        dos.flush();
        return baos.toByteArray();
    }
    
    /**
     * Escribe un entero usando codificación de longitud variable
     * Números pequeños usan menos bytes
     */
    private void writeVariableInt(DataOutputStream dos, int value) throws IOException {
        // Si el valor es menor a 128, usar 1 byte
        if (value < 128) {
            dos.writeByte(value);
        } 
        // Si es menor a 16384, usar 2 bytes
        else if (value < 16384) {
            dos.writeByte((value >> 8) | 0x80);  // Primer byte con bit alto en 1
            dos.writeByte(value & 0xFF);
        }
        // Si es mayor, usar 4 bytes completos con marcador
        else {
            dos.writeByte(0xFF);  // Marcador especial
            dos.writeInt(value);
        }
    }
    
    /**
     * Guarda los datos comprimidos en un archivo
     */
    public void saveCompressedFile(File outputFile, CompressionResult result) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(result.getCompressedData());
        }
    }
}
