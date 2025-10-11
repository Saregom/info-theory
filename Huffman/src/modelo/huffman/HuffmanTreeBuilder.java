package modelo.huffman;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Construye el árbol de Huffman y genera los códigos binarios
 */
public class HuffmanTreeBuilder {
	
	/**
	 * Construye el árbol de Huffman a partir de las frecuencias
	 * @param frequencies mapa de frecuencias de los símbolos
	 * @return la raíz del árbol de Huffman
	 */
	public static HuffmanNode buildTree(Map<Character, Integer> frequencies) {
		// Caso especial: solo un símbolo único
		if (frequencies.size() == 1) {
			Map.Entry<Character, Integer> entry = frequencies.entrySet().iterator().next();
			HuffmanNode leaf = new HuffmanNode(entry.getKey(), entry.getValue());
			// Crear un nodo raíz con un hijo para que haya códigos binarios
			return new HuffmanNode(leaf, null);
		}
		
		// Crear una cola de prioridad con comparador optimizado
		// Prioridad: 1) Menor frecuencia, 2) Nodos hoja antes que internos, 3) Altura del subárbol
		PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>(
			Comparator.comparingInt(HuffmanNode::getFrequency)
				.thenComparing((n1, n2) -> Boolean.compare(n2.isLeaf(), n1.isLeaf()))
				.thenComparingInt(HuffmanTreeBuilder::getHeight)
		);
		
		for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
			priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue()));
		}
		
		// Construir el árbol combinando los dos nodos de menor frecuencia
		while (priorityQueue.size() > 1) {
			HuffmanNode first = priorityQueue.poll();
			HuffmanNode second = priorityQueue.poll();
			
			// Ahora el hijo derecho recibe '0' (código más corto)
			// Colocar el nodo más pesado (mayor frecuencia) a la derecha
			HuffmanNode left, right;
			if (shouldBeRight(first, second)) {
				left = second;
				right = first;
			} else {
				left = first;
				right = second;
			}
			
			HuffmanNode parent = new HuffmanNode(left, right);
			priorityQueue.add(parent);
		}
		
		return priorityQueue.poll();
	}
	
	/**
	 * Determina si un nodo debería ir a la derecha (recibe '0', código más corto)
	 * @param n1 primer nodo
	 * @param n2 segundo nodo
	 * @return true si n1 debe ir a la derecha
	 */
	private static boolean shouldBeRight(HuffmanNode n1, HuffmanNode n2) {
		// Si tienen frecuencias diferentes, el de mayor frecuencia va a la derecha
		if (n1.getFrequency() != n2.getFrequency()) {
			return n1.getFrequency() > n2.getFrequency();
		}
		
		// Si tienen la misma frecuencia, poner el nodo hoja a la derecha
		if (n1.isLeaf() && !n2.isLeaf()) {
			return true;
		}
		if (!n1.isLeaf() && n2.isLeaf()) {
			return false;
		}
		
		// Si ambos son hojas o internos, poner el de menor altura a la derecha
		return getHeight(n1) <= getHeight(n2);
	}
	
	/**
	 * Calcula la altura de un nodo (para optimización del árbol)
	 * @param node el nodo
	 * @return la altura del subárbol
	 */
	private static int getHeight(HuffmanNode node) {
		if (node == null || node.isLeaf()) {
			return 0;
		}
		return 1 + Math.max(getHeight(node.getLeft()), getHeight(node.getRight()));
	}
	
	/**
	 * Genera los códigos binarios para cada símbolo recorriendo el árbol
	 * @param root la raíz del árbol de Huffman
	 * @return un mapa con cada símbolo y su código binario
	 */
	public static Map<Character, String> generateCodes(HuffmanNode root) {
		Map<Character, String> codes = new HashMap<>();
		
		if (root != null) {
			// Caso especial: árbol con un solo símbolo
			if (root.getLeft() != null && root.getRight() == null) {
				codes.put(root.getLeft().getSymbol(), "0");
			} else {
				generateCodesRecursive(root, "", codes);
			}
		}
		
		return codes;
	}
	
	/**
	 * Método recursivo para generar los códigos
	 * @param node el nodo actual
	 * @param code el código acumulado hasta ahora
	 * @param codes el mapa donde se guardan los códigos
	 */
	private static void generateCodesRecursive(HuffmanNode node, String code, Map<Character, String> codes) {
		if (node == null) {
			return;
		}
		
		// Si es una hoja, guardar el código
		if (node.isLeaf()) {
			codes.put(node.getSymbol(), code.isEmpty() ? "0" : code);
			return;
		}
		
		// Recorrer el subárbol izquierdo (agregar '1')
		generateCodesRecursive(node.getLeft(), code + "1", codes);
		
		// Recorrer el subárbol derecho (agregar '0')
		generateCodesRecursive(node.getRight(), code + "0", codes);
	}
}

