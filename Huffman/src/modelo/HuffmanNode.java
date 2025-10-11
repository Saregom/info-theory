package modelo;

/**
 * Representa un nodo en el árbol de Huffman
 */
public class HuffmanNode implements Comparable<HuffmanNode> {
	
	private char symbol;
	private int frequency;
	private HuffmanNode left;
	private HuffmanNode right;
	private boolean isLeaf;
	
	/**
	 * Constructor para un nodo hoja (contiene un símbolo)
	 * @param symbol el símbolo
	 * @param frequency la frecuencia del símbolo
	 */
	public HuffmanNode(char symbol, int frequency) {
		this.symbol = symbol;
		this.frequency = frequency;
		this.isLeaf = true;
		this.left = null;
		this.right = null;
	}
	
	/**
	 * Constructor para un nodo interno (no contiene símbolo)
	 * @param left el hijo izquierdo
	 * @param right el hijo derecho
	 */
	public HuffmanNode(HuffmanNode left, HuffmanNode right) {
		this.symbol = '\0';
		this.frequency = left.frequency + right.frequency;
		this.left = left;
		this.right = right;
		this.isLeaf = false;
	}
	
	/**
	 * @return el símbolo del nodo
	 */
	public char getSymbol() {
		return symbol;
	}
	
	/**
	 * @return la frecuencia del nodo
	 */
	public int getFrequency() {
		return frequency;
	}
	
	/**
	 * @return el hijo izquierdo
	 */
	public HuffmanNode getLeft() {
		return left;
	}
	
	/**
	 * @return el hijo derecho
	 */
	public HuffmanNode getRight() {
		return right;
	}
	
	/**
	 * @return true si el nodo es una hoja
	 */
	public boolean isLeaf() {
		return isLeaf;
	}
	
	/**
	 * Compara nodos por frecuencia (para la cola de prioridad)
	 */
	@Override
	public int compareTo(HuffmanNode other) {
		return Integer.compare(this.frequency, other.frequency);
	}
}
