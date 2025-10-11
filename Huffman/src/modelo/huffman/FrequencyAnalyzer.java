package modelo.huffman;

import java.util.HashMap;
import java.util.Map;

/**
 * Analiza las frecuencias de los símbolos en un mensaje
 */
public class FrequencyAnalyzer {
	
	/**
	 * Calcula la frecuencia de cada símbolo en el mensaje
	 * @param message el mensaje a analizar
	 * @return un mapa con cada símbolo y su frecuencia
	 */
	public static Map<Character, Integer> analyzeFrequencies(String message) {
		Map<Character, Integer> frequencies = new HashMap<>();
		
		// Contar la frecuencia de cada carácter
		for (char c : message.toCharArray()) {
			frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
		}
		
		return frequencies;
	}
	
	/**
	 * Calcula la probabilidad de cada símbolo
	 * @param frequencies mapa de frecuencias
	 * @param totalSymbols número total de símbolos
	 * @return mapa con las probabilidades
	 */
	public static Map<Character, Double> calculateProbabilities(Map<Character, Integer> frequencies, int totalSymbols) {
		Map<Character, Double> probabilities = new HashMap<>();
		
		for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
			double probability = (double) entry.getValue() / totalSymbols;
			probabilities.put(entry.getKey(), probability);
		}
		
		return probabilities;
	}
}
