package modelo.huffman;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Encapsula los resultados de la codificación de Huffman
 */
public class HuffmanResult {
	
	private List<Character> symbols;
	private List<Integer> frequencies;
	private List<Double> probabilities;
	private List<String> codes;
	private String encodedMessage;
	private double entropy;
	private double averageLength;
	private double efficiency;
	
	/**
	 * Clase auxiliar para ordenar los símbolos
	 */
	private static class SymbolData {
		char symbol;
		int frequency;
		double probability;
		String code;
		
		SymbolData(char symbol, int frequency, double probability, String code) {
			this.symbol = symbol;
			this.frequency = frequency;
			this.probability = probability;
			this.code = code;
		}
	}
	
	/**
	 * Constructor
	 * @param frequencies mapa de frecuencias
	 * @param probabilities mapa de probabilidades
	 * @param codes mapa de códigos
	 * @param encodedMessage mensaje codificado
	 */
	public HuffmanResult(Map<Character, Integer> frequencies, Map<Character, Double> probabilities, 
	                     Map<Character, String> codes, String encodedMessage) {
		this.symbols = new ArrayList<>();
		this.frequencies = new ArrayList<>();
		this.probabilities = new ArrayList<>();
		this.codes = new ArrayList<>();
		
		// Crear lista de datos para ordenar
		List<SymbolData> symbolDataList = new ArrayList<>();
		for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
			char symbol = entry.getKey();
			symbolDataList.add(new SymbolData(
				symbol,
				entry.getValue(),
				probabilities.get(symbol),
				codes.get(symbol)
			));
		}
		
		// Ordenar por frecuencia de mayor a menor
		symbolDataList.sort(Comparator.comparingInt((SymbolData sd) -> sd.frequency).reversed());
		
		// Convertir a listas separadas ya ordenadas
		for (SymbolData data : symbolDataList) {
			this.symbols.add(data.symbol);
			this.frequencies.add(data.frequency);
			this.probabilities.add(data.probability);
			this.codes.add(data.code);
		}
		
		this.encodedMessage = encodedMessage;
		
		// Calcular métricas
		calculateMetrics();
	}
	
	/**
	 * Calcula la entropía, largo medio y eficiencia
	 */
	private void calculateMetrics() {
		// Calcular entropía: H(S) = -Σ(p(i) * log2(p(i)))
		entropy = 0.0;
		for (double p : probabilities) {
			if (p > 0) {
				entropy -= p * (Math.log(p) / Math.log(2));
			}
		}
		
		// Calcular largo medio: L = Σ(p(i) * l(i))
		averageLength = 0.0;
		for (int i = 0; i < probabilities.size(); i++) {
			averageLength += probabilities.get(i) * codes.get(i).length();
		}
		
		// Calcular eficiencia: η = H(S) / L * 100%
		if (averageLength > 0) {
			efficiency = (entropy / averageLength) * 100;
		} else {
			efficiency = 0.0;
		}
	}
	
	/**
	 * @return número de símbolos diferentes
	 */
	public int getSymbolCount() {
		return symbols.size();
	}
	
	/**
	 * @param index índice del símbolo
	 * @return el símbolo en la posición indicada
	 */
	public char getSymbol(int index) {
		return symbols.get(index);
	}
	
	/**
	 * @param index índice del símbolo
	 * @return la frecuencia del símbolo
	 */
	public int getFrequency(int index) {
		return frequencies.get(index);
	}
	
	/**
	 * @param index índice del símbolo
	 * @return la probabilidad del símbolo
	 */
	public double getProbability(int index) {
		return probabilities.get(index);
	}
	
	/**
	 * @param index índice del símbolo
	 * @return el código binario del símbolo
	 */
	public String getCode(int index) {
		return codes.get(index);
	}
	
	/**
	 * @return el mensaje codificado
	 */
	public String getEncodedMessage() {
		return encodedMessage;
	}
	
	/**
	 * @return la entropía en bits
	 */
	public double getEntropy() {
		return entropy;
	}
	
	/**
	 * @return el largo medio en bits
	 */
	public double getAverageLength() {
		return averageLength;
	}
	
	/**
	 * @return la eficiencia en porcentaje
	 */
	public double getEfficiency() {
		return efficiency;
	}
}
