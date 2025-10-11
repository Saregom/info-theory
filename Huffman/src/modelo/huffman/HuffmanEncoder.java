package modelo.huffman;

import java.util.Map;

/**
 * Clase principal que orquesta el proceso de codificación de Huffman
 */
public class HuffmanEncoder {
	
	/**
	 * Codifica un mensaje usando el algoritmo de Huffman
	 * @param message el mensaje a codificar
	 * @return un objeto HuffmanResult con todos los resultados
	 * @throws IllegalArgumentException si el mensaje está vacío
	 */
	public static HuffmanResult encode(String message) {
		if (message == null || message.isEmpty()) {
			throw new IllegalArgumentException("El mensaje no puede estar vacío");
		}
		
		// Paso 1: Analizar las frecuencias
		Map<Character, Integer> frequencies = FrequencyAnalyzer.analyzeFrequencies(message);
		
		// Paso 2: Calcular las probabilidades
		Map<Character, Double> probabilities = FrequencyAnalyzer.calculateProbabilities(frequencies, message.length());
		
		// Paso 3: Construir el árbol de Huffman
		HuffmanNode root = HuffmanTreeBuilder.buildTree(frequencies);
		
		// Paso 4: Generar los códigos binarios
		Map<Character, String> codes = HuffmanTreeBuilder.generateCodes(root);
		
		// Paso 5: Codificar el mensaje
		String encodedMessage = encodeMessage(message, codes);
		
		// Paso 6: Crear y retornar el resultado
		return new HuffmanResult(frequencies, probabilities, codes, encodedMessage);
	}
	
	/**
	 * Codifica un mensaje reemplazando cada símbolo por su código binario
	 * @param message el mensaje original
	 * @param codes el mapa de códigos
	 * @return el mensaje codificado en binario
	 */
	private static String encodeMessage(String message, Map<Character, String> codes) {
		StringBuilder encoded = new StringBuilder();
		
		for (char c : message.toCharArray()) {
			encoded.append(codes.get(c));
		}
		
		return encoded.toString();
	}
}
