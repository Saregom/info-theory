package model;

import java.util.*;

/**
 * Representa el diccionario usado en el algoritmo LZ78
 */
public class Dictionary {
    private Map<String, Integer> dictionary;
    private Map<Integer, String> reverseDictionary;
    private int nextIndex;

    public Dictionary() {
        this.dictionary = new HashMap<>();
        this.reverseDictionary = new HashMap<>();
        this.nextIndex = 1;
    }

    /**
     * Agrega una entrada al diccionario
     */
    public int add(String sequence) {
        if (!dictionary.containsKey(sequence)) {
            dictionary.put(sequence, nextIndex);
            reverseDictionary.put(nextIndex, sequence);
            nextIndex++;
        }
        return dictionary.get(sequence);
    }

    /**
     * Obtiene el índice de una secuencia
     */
    public Integer getIndex(String sequence) {
        return dictionary.get(sequence);
    }

    /**
     * Obtiene la secuencia dado un índice
     */
    public String getSequence(int index) {
        return reverseDictionary.get(index);
    }

    /**
     * Verifica si una secuencia existe en el diccionario
     */
    public boolean contains(String sequence) {
        return dictionary.containsKey(sequence);
    }

    /**
     * Retorna el tamaño del diccionario
     */
    public int size() {
        return dictionary.size();
    }

    /**
     * Limpia el diccionario
     */
    public void clear() {
        dictionary.clear();
        reverseDictionary.clear();
        nextIndex = 1;
    }

    /**
     * Obtiene una copia del diccionario para visualización
     */
    public Map<Integer, String> getReverseDictionary() {
        return new HashMap<>(reverseDictionary);
    }

    /**
     * Retorna una representación ordenada del diccionario
     */
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DICCIONARIO LZ78\n");
        sb.append("================\n\n");
        
        List<Integer> indices = new ArrayList<>(reverseDictionary.keySet());
        Collections.sort(indices);
        
        for (int index : indices) {
            String sequence = reverseDictionary.get(index);
            sb.append(String.format("%-5d -> %s\n", index, escapeString(sequence)));
        }
        
        return sb.toString();
    }

    /**
     * Escapa caracteres especiales para visualización
     */
    private String escapeString(String str) {
        return str.replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    @Override
    public String toString() {
        return "Dictionary{size=" + size() + "}";
    }
}
