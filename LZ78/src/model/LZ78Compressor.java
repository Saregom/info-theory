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
            // Validar archivo existe y es legible
            if (!inputFile.exists()) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo no existe");
                return result;
            }
            
            if (!inputFile.canRead()) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo no se puede leer. Verifique los permisos.");
                return result;
            }
            
            // Validar que el archivo no esté vacío
            if (inputFile.length() == 0) {
                result.setSuccess(false);
                result.setErrorMessage("El archivo está vacío. No hay nada que comprimir.");
                return result;
            }
            
            // Leer el archivo como bytes (funciona para texto y binarios)
            byte[] fileBytes = readFileAsBytes(inputFile);
            
            // Guardar la extensión del archivo original
            String fileName = inputFile.getName();
            String extension = "";
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0 && lastDot < fileName.length() - 1) {
                extension = fileName.substring(lastDot + 1);
            }
            result.setOriginalFileExtension(extension);
            
            // Convertir bytes a String para el algoritmo LZ78
            // Usamos ISO-8859-1 que mapea 1:1 con bytes (0-255)
            String text = new String(fileBytes, "ISO-8859-1");
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
     * Lee el archivo completo como bytes (funciona para texto y binarios)
     */
    private byte[] readFileAsBytes(File file) throws IOException {
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(data);
            if (bytesRead != data.length) {
                throw new IOException("No se pudo leer el archivo completo");
            }
        }
        return data;
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
            // Escribir el carácter como byte (ISO-8859-1) para preservar binarios
            dos.writeByte((byte) pair.getCharacter());
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
     * Guarda los datos comprimidos en un archivo incluyendo el diccionario
     * Formato del archivo:
     * [HEADER: "LZ78" + versión]
     * [Extensión del archivo original (longitud + string)]
     * [Tamaño del diccionario]
     * [Diccionario serializado]
     * [Datos comprimidos (pares índice-carácter)]
     */
    public void saveCompressedFile(File outputFile, CompressionResult result) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputFile)))) {
            
            // Escribir header con identificador y versión
            dos.writeBytes("LZ78");
            dos.writeByte(1); // Versión del formato
            
            // Escribir la extensión del archivo original
            String extension = result.getOriginalFileExtension();
            if (extension == null) extension = "";
            dos.writeByte(extension.length());
            if (extension.length() > 0) {
                dos.writeBytes(extension);
            }
            
            // Serializar el diccionario
            Dictionary dict = result.getDictionary();
            byte[] dictData = serializeDictionary(dict);
            
            // Escribir tamaño del diccionario
            dos.writeInt(dictData.length);
            
            // Escribir el diccionario
            dos.write(dictData);
            
            // Escribir los datos comprimidos
            dos.write(result.getCompressedData());
            
            dos.flush();
        }
    }
    
    /**
     * Serializa el diccionario a bytes
     */
    private byte[] serializeDictionary(Dictionary dictionary) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // Obtener el mapa de decodificación
        var decodingDict = dictionary.getDecodingDict();
        
        // Escribir número de entradas
        writeVariableInt(dos, decodingDict.size());
        
        // Escribir cada entrada (índice -> frase)
        for (var entry : decodingDict.entrySet()) {
            int index = entry.getKey();
            String phrase = entry.getValue();
            
            // Escribir índice
            writeVariableInt(dos, index);
            
            // Escribir longitud de la frase
            writeVariableInt(dos, phrase.length());
            
            // Escribir la frase byte por byte (ISO-8859-1 para preservar binarios)
            for (int j = 0; j < phrase.length(); j++) {
                // Convertir char a byte usando ISO-8859-1 (mapeo 1:1)
                dos.writeByte((byte) phrase.charAt(j));
            }
        }
        
        dos.flush();
        return baos.toByteArray();
    }
}
