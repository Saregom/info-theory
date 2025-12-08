package controller;

import model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador que gestiona las operaciones de compresión y descompresión
 */
public class CompressionController {
    private LZ78Compressor compressor;
    private LZ78Decompressor decompressor;
    private static final String LZ78_EXTENSION = ".lz78";
    private static final String LZ78_MAGIC_NUMBER = "LZ78";

    public CompressionController() {
        this.compressor = new LZ78Compressor();
        this.decompressor = new LZ78Decompressor();
    }

    /**
     * Carga un archivo de texto para comprimir
     * @param filePath Ruta del archivo
     * @return Contenido del archivo
     * @throws IOException Si hay error al leer
     */
    public String loadTextFile(String filePath) throws IOException {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new IOException("El archivo no existe");
        }
        
        if (!file.canRead()) {
            throw new IOException("El archivo no es legible");
        }
        
        if (file.length() == 0) {
            throw new IOException("El archivo está vacío");
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        String text = content.toString();
        if (text.trim().isEmpty()) {
            throw new IOException("El archivo está vacío o contiene solo espacios en blanco");
        }

        return text;
    }

    /**
     * Carga un archivo binario para comprimir
     * @param filePath Ruta del archivo
     * @return Contenido del archivo como String (bytes convertidos a chars)
     * @throws IOException Si hay error al leer
     */
    public String loadBinaryFile(String filePath) throws IOException {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new IOException("El archivo no existe");
        }
        
        if (!file.canRead()) {
            throw new IOException("El archivo no es legible");
        }
        
        if (file.length() == 0) {
            throw new IOException("El archivo está vacío");
        }

        // Leer todos los bytes del archivo
        byte[] fileBytes;
        try (FileInputStream fis = new FileInputStream(file)) {
            fileBytes = fis.readAllBytes();
        }

        // Convertir bytes a String para el algoritmo LZ78
        StringBuilder sb = new StringBuilder(fileBytes.length);
        for (byte b : fileBytes) {
            // Convertir cada byte a char (0-255)
            sb.append((char) (b & 0xFF));
        }

        return sb.toString();
    }

    /**
     * Comprime un texto
     * @param text Texto a comprimir
     * @return Resultado de la compresión
     * @throws IllegalArgumentException Si el texto es inválido
     */
    public CompressionResult compressText(String text) {
        if (!compressor.validateText(text)) {
            throw new IllegalArgumentException("El texto es inválido o está vacío");
        }
        return compressor.compress(text);
    }

    /**
     * Guarda el archivo comprimido en formato .lz78
     * @param result Resultado de la compresión
     * @param outputPath Ruta donde guardar
     * @param originalExtension Extensión original del archivo (ej: ".txt", ".docx")
     * @throws IOException Si hay error al escribir
     */
    public void saveCompressedFile(CompressionResult result, String outputPath, String originalExtension) throws IOException {
        if (!outputPath.toLowerCase().endsWith(LZ78_EXTENSION)) {
            outputPath += LZ78_EXTENSION;
        }

        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputPath)))) {
            
            // Escribir número mágico
            dos.writeUTF(LZ78_MAGIC_NUMBER);
            
            // Escribir extensión original (nueva)
            dos.writeUTF(originalExtension != null ? originalExtension : "");
            
            // Escribir tamaño original
            dos.writeLong(result.getOriginalSize());
            
            // Escribir número de pares
            List<CompressionResult.EncodedPair> encodedData = result.getEncodedData();
            dos.writeInt(encodedData.size());
            
            // Escribir cada par
            for (CompressionResult.EncodedPair pair : encodedData) {
                dos.writeInt(pair.getIndex());
                dos.writeChar(pair.getCharacter());
            }
            
            dos.flush();
        }
    }

    /**
     * Carga un archivo comprimido .lz78 con extensión original
     * @param filePath Ruta del archivo
     * @return Array con [0]=extensión original, [1]=datos codificados
     * @throws IOException Si hay error al leer o el formato es incorrecto
     */
    public Object[] loadCompressedFileWithExtension(String filePath) throws IOException {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new IOException("El archivo no existe");
        }
        
        if (!file.canRead()) {
            throw new IOException("El archivo no es legible");
        }
        
        if (!filePath.toLowerCase().endsWith(LZ78_EXTENSION)) {
            throw new IOException("Formato de archivo incorrecto. Se esperaba extensión .lz78");
        }

        List<CompressionResult.EncodedPair> encodedData = new ArrayList<>();
        String originalExtension = "";

        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            
            // Leer y verificar número mágico
            String magicNumber = dis.readUTF();
            if (!LZ78_MAGIC_NUMBER.equals(magicNumber)) {
                throw new IOException("Archivo incompatible. No es un archivo LZ78 válido");
            }
            
            // Leer extensión original
            originalExtension = dis.readUTF();
            
            // Leer tamaño original (solo para información)
            dis.readLong();
            
            // Leer número de pares
            int pairCount = dis.readInt();
            
            if (pairCount <= 0) {
                throw new IOException("Archivo corrupto: número de pares inválido");
            }
            
            // Leer cada par
            for (int i = 0; i < pairCount; i++) {
                int index = dis.readInt();
                char character = dis.readChar();
                encodedData.add(new CompressionResult.EncodedPair(index, character));
            }
        } catch (EOFException e) {
            throw new IOException("Archivo corrupto: fin de archivo inesperado");
        }

        return new Object[]{originalExtension, encodedData};
    }

    /**
     * Carga un archivo comprimido .lz78 (versión legacy)
     * @param filePath Ruta del archivo
     * @return Datos codificados
     * @throws IOException Si hay error al leer o el formato es incorrecto
     */
    @SuppressWarnings("unchecked")
    public List<CompressionResult.EncodedPair> loadCompressedFile(String filePath) throws IOException {
        Object[] result = loadCompressedFileWithExtension(filePath);
        return (List<CompressionResult.EncodedPair>) result[1];
    }

    /**
     * Descomprime datos codificados
     * @param encodedData Datos a descomprimir
     * @return Resultado de la descompresión
     */
    public CompressionResult decompressData(List<CompressionResult.EncodedPair> encodedData) {
        return decompressor.decompress(encodedData);
    }

    /**
     * Guarda el texto descomprimido en un archivo
     * @param text Texto a guardar
     * @param outputPath Ruta donde guardar
     * @throws IOException Si hay error al escribir
     */
    public void saveDecompressedFile(String text, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8))) {
            writer.write(text);
        }
    }

    /**
     * Guarda el archivo binario descomprimido
     * @param text String con los bytes del archivo (chars 0-255)
     * @param outputPath Ruta donde guardar
     * @throws IOException Si hay error al escribir
     */
    public void saveBinaryFile(String text, String outputPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            byte[] bytes = new byte[text.length()];
            for (int i = 0; i < text.length(); i++) {
                bytes[i] = (byte) text.charAt(i);
            }
            fos.write(bytes);
        }
    }

    /**
     * Guarda el diccionario y datos codificados en un archivo de texto
     * @param result Resultado de la compresión
     * @param outputPath Ruta donde guardar
     * @throws IOException Si hay error al escribir
     */
    public void saveDictionaryAndEncoded(CompressionResult result, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8))) {
            
            writer.write("╔════════════════════════════════════════════════════════════╗\n");
            writer.write("║           ARCHIVO DE COMPRESIÓN LZ78                      ║\n");
            writer.write("╚════════════════════════════════════════════════════════════╝\n\n");
            
            // Escribir estadísticas
            writer.write(result.getStatistics());
            writer.write("\n\n");
            
            // Escribir diccionario
            writer.write(result.getDictionary().toFormattedString());
            writer.write("\n\n");
            
            // Escribir datos codificados
            writer.write(result.getEncodedDataString());
            writer.write("\n");
        }
    }

    /**
     * Valida la extensión del archivo
     * @param filePath Ruta del archivo
     * @return true si tiene extensión .lz78
     */
    public boolean isValidLZ78File(String filePath) {
        return filePath != null && filePath.toLowerCase().endsWith(LZ78_EXTENSION);
    }

    /**
     * Obtiene el compresor
     */
    public LZ78Compressor getCompressor() {
        return compressor;
    }

    /**
     * Obtiene el descompresor
     */
    public LZ78Decompressor getDecompressor() {
        return decompressor;
    }
}
