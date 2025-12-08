package model;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa el diccionario utilizado en el algoritmo LZ78
 */
public class Dictionary {
    private Map<String, Integer> encodingDict;
    private Map<Integer, String> decodingDict;
    private int nextIndex;
    
    public Dictionary() {
        encodingDict = new HashMap<>();
        decodingDict = new HashMap<>();
        nextIndex = 1; // El índice 0 se reserva para cadena vacía
    }
    
    /**
     * Agrega una nueva entrada al diccionario
     */
    public int addEntry(String phrase) {
        if (!encodingDict.containsKey(phrase)) {
            encodingDict.put(phrase, nextIndex);
            decodingDict.put(nextIndex, phrase);
            nextIndex++;
            return nextIndex - 1;
        }
        return encodingDict.get(phrase);
    }
    
    /**
     * Busca una frase en el diccionario de codificación
     */
    public Integer getIndex(String phrase) {
        return encodingDict.get(phrase);
    }
    
    /**
     * Busca una frase por su índice en el diccionario de decodificación
     */
    public String getPhrase(int index) {
        return decodingDict.get(index);
    }
    
    /**
     * Verifica si una frase existe en el diccionario
     */
    public boolean contains(String phrase) {
        return encodingDict.containsKey(phrase);
    }
    
    /**
     * Obtiene el tamaño actual del diccionario
     */
    public int size() {
        return encodingDict.size();
    }
    
    /**
     * Limpia el diccionario
     */
    public void clear() {
        encodingDict.clear();
        decodingDict.clear();
        nextIndex = 1;
    }
    
    /**
     * Obtiene una representación en texto del diccionario para visualización
     */
    public String getDictionaryAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Índice\t| Frase\n");
        sb.append("--------------------------------------\n");
        
        List<Integer> indices = new ArrayList<>(decodingDict.keySet());
        indices.sort(Integer::compareTo);
        
        for (Integer index : indices) {
            String phrase = decodingDict.get(index);
            String displayPhrase = phrase.replace("\n", "\\n")
                                         .replace("\r", "\\r")
                                         .replace("\t", "\\t");
            sb.append(String.format("%d\t| %s\n", index, displayPhrase));
        }
        
        sb.append("--------------------------------------\n");
        sb.append("Total de entradas: " + size() + "\n");
        
        return sb.toString();
    }
    
    /**
     * Obtiene el mapa de codificación (para exportar)
     */
    public Map<String, Integer> getEncodingDict() {
        return new HashMap<>(encodingDict);
    }
    
    /**
     * Obtiene el mapa de decodificación (para exportar)
     */
    public Map<Integer, String> getDecodingDict() {
        return new HashMap<>(decodingDict);
    }
}