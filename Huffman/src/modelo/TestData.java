package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase con datos de prueba para simular resultados de codificación Huffman
 */
public class TestData {
	
	private List<String> symbols;
	private List<Integer> frequencies;
	private List<Double> probabilities;
	private List<String> binaryCodes;
	
	// Métricas de la codificación
	private String encodedMessage;
	private double entropy;
	private double averageLength;
	private double efficiency;
	
	/**
	 * Constructor que inicializa los datos de prueba
	 */
	public TestData() {
		symbols = new ArrayList<>();
		frequencies = new ArrayList<>();
		probabilities = new ArrayList<>();
		binaryCodes = new ArrayList<>();
		
		// Valores por defecto de métricas
		encodedMessage = "";
		entropy = 0.0;
		averageLength = 0.0;
		efficiency = 0.0;
	}
	
	/**
	 * Crea datos de prueba para un mensaje específico (simulado)
	 * @param message el mensaje para el cual simular datos
	 * @return una instancia de TestData con datos simulados
	 */
	public static TestData createFromMessage(String message) {
		TestData data = new TestData();
		data.clearData();
		
		// Ejemplo: "HOLA MUNDO"
		if (message.toUpperCase().contains("HOLA MUNDO")) {
			data.addSymbol("O", 2, 0.2000, "00");
			data.addSymbol("L", 1, 0.1000, "010");
			data.addSymbol("H", 1, 0.1000, "011");
			data.addSymbol("A", 1, 0.1000, "100");
			data.addSymbol(" ", 1, 0.1000, "101");
			data.addSymbol("M", 1, 0.1000, "110");
			data.addSymbol("U", 1, 0.1000, "1110");
			data.addSymbol("N", 1, 0.1000, "1111");
			data.addSymbol("D", 1, 0.1000, "1112");
			
			// Métricas simuladas para "HOLA MUNDO"
			data.encodedMessage = "011 00 010 100 101 110 1110 1111 1112 00";
			data.entropy = 3.1699;
			data.averageLength = 3.5000;
			data.efficiency = 90.57;
		}
		// Por defecto, usar el ejemplo de "ABRACADABRA"
		else if (!message.isEmpty()) {
			// data.initializeDefaultData();
            data.addSymbol("A", 5, 0.4545, "0");
            data.addSymbol("B", 2, 0.1818, "111");
            data.addSymbol("R", 2, 0.1818, "110");
            data.addSymbol("C", 1, 0.0909, "1011");
            data.addSymbol("D", 1, 0.0909, "1010");
            
            // Métricas simuladas para "ABRACADABRA"
            data.encodedMessage = "0 111 110 0 1011 0 1010 0 111 110 0";
            data.entropy = 1.8845;
            data.averageLength = 2.0909;
            data.efficiency = 90.13;
		}
		
		return data;
	}
	
	/**
	 * Agrega un símbolo con sus datos
	 */
	private void addSymbol(String symbol, int frequency, double probability, String code) {
		symbols.add(symbol);
		frequencies.add(frequency);
		probabilities.add(probability);
		binaryCodes.add(code);
	}
	
	/**
	 * Limpia todos los datos
	 */
	private void clearData() {
		symbols.clear();
		frequencies.clear();
		probabilities.clear();
		binaryCodes.clear();
	}
	
	// Getters
	
	public List<String> getSymbols() {
		return symbols;
	}
	
	public List<Integer> getFrequencies() {
		return frequencies;
	}
	
	public List<Double> getProbabilities() {
		return probabilities;
	}
	
	public List<String> getBinaryCodes() {
		return binaryCodes;
	}
	
	/**
	 * Obtiene el número de símbolos
	 * @return cantidad de símbolos
	 */
	public int size() {
		return symbols.size();
	}
	
	/**
	 * Obtiene un símbolo por índice
	 */
	public String getSymbol(int index) {
		return symbols.get(index);
	}
	
	/**
	 * Obtiene una frecuencia por índice
	 */
	public int getFrequency(int index) {
		return frequencies.get(index);
	}
	
	/**
	 * Obtiene una probabilidad por índice
	 */
	public double getProbability(int index) {
		return probabilities.get(index);
	}
	
	/**
	 * Obtiene un código binario por índice
	 */
	public String getBinaryCode(int index) {
		return binaryCodes.get(index);
	}
	
	/**
	 * Obtiene el mensaje codificado
	 */
	public String getEncodedMessage() {
		return encodedMessage;
	}
	
	/**
	 * Obtiene la entropía
	 */
	public double getEntropy() {
		return entropy;
	}
	
	/**
	 * Obtiene el largo medio
	 */
	public double getAverageLength() {
		return averageLength;
	}
	
	/**
	 * Obtiene la eficiencia
	 */
	public double getEfficiency() {
		return efficiency;
	}
}
