package modelo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase para exportar los resultados de la codificación de Huffman a un archivo
 */
public class FileExporter {
	
	/**
	 * Exporta el diccionario y el mensaje codificado a un archivo
	 * @param result el resultado de la codificación de Huffman
	 * @param filePath la ruta del archivo donde guardar
	 * @param originalMessage el mensaje original
	 * @throws IOException si hay un error al escribir el archivo
	 */
	public static void exportToFile(HuffmanResult result, String filePath, String originalMessage) throws IOException {
		File file = new File(filePath);
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			// Escribir encabezado
			writer.write("========================================");
			writer.newLine();
			writer.write("   CODIFICACIÓN DE HUFFMAN - RESULTADOS");
			writer.newLine();
			writer.write("========================================");
			writer.newLine();
			writer.newLine();
			
			// Escribir mensaje original
			writer.write("MENSAJE ORIGINAL:");
			writer.newLine();
			writer.write(originalMessage);
			writer.newLine();
			writer.newLine();
			
			// Escribir diccionario de códigos
			writer.write("DICCIONARIO DE CÓDIGOS HUFFMAN:");
			writer.newLine();
			writer.write("----------------------------------------");
			writer.newLine();
			writer.write(String.format("%-10s %-12s %-15s %s", "Símbolo", "Frecuencia", "Probabilidad", "Código"));
			writer.newLine();
			writer.write("----------------------------------------");
			writer.newLine();
			
			for (int i = 0; i < result.getSymbolCount(); i++) {
				char symbol = result.getSymbol(i);
				String symbolStr = symbol == ' ' ? "espacio" : String.valueOf(symbol);
				
				writer.write(String.format("%-10s %-12d %-15.4f %s", 
					symbolStr,
					result.getFrequency(i),
					result.getProbability(i),
					result.getCode(i)
				));
				writer.newLine();
			}
			
			writer.newLine();
			
			// Escribir mensaje codificado
			writer.write("MENSAJE CODIFICADO:");
			writer.newLine();
			writer.write("----------------------------------------");
			writer.newLine();
			writer.write(result.getEncodedMessage());
			writer.newLine();
			writer.newLine();
			
			// Escribir métricas
			writer.write("MÉTRICAS DE CODIFICACIÓN:");
			writer.newLine();
			writer.write("----------------------------------------");
			writer.newLine();
			writer.write(String.format("Entropía H(S):         %.4f bits", result.getEntropy()));
			writer.newLine();
			writer.write(String.format("Largo Medio (L):       %.4f bits", result.getAverageLength()));
			writer.newLine();
			writer.write(String.format("Eficiencia (η):        %.2f%%", result.getEfficiency()));
			writer.newLine();
			writer.newLine();
			
			// Escribir estadísticas adicionales
			writer.write("ESTADÍSTICAS:");
			writer.newLine();
			writer.write("----------------------------------------");
			writer.newLine();
			writer.write(String.format("Longitud original:     %d caracteres", originalMessage.length()));
			writer.newLine();
			writer.write(String.format("Longitud codificada:   %d bits", result.getEncodedMessage().length()));
			writer.newLine();
			writer.write(String.format("Bits originales:       %d bits (ASCII 8 bits/char)", originalMessage.length() * 8));
			writer.newLine();
			writer.write(String.format("Compresión:            %.2f%%", 
				(1.0 - (double) result.getEncodedMessage().length() / (originalMessage.length() * 8)) * 100));
			writer.newLine();
			writer.newLine();
			
			writer.write("========================================");
			writer.newLine();
		}
	}
	
	/**
	 * Obtiene la extensión recomendada para el archivo
	 * @return la extensión del archivo
	 */
	public static String getFileExtension() {
		return ".txt";
	}
	
	/**
	 * Genera un nombre de archivo sugerido basado en la fecha/hora actual
	 * @return nombre de archivo sugerido
	 */
	public static String getSuggestedFileName() {
		return "huffman_" + System.currentTimeMillis() + getFileExtension();
	}
}
