package modelo;

import java.util.*;

public class CodificadorHuffman {
    private Map<Character, String> diccionario;
    private Map<Character, Integer> frecuencias;
    private String mensajeOriginal;
    private String mensajeCodificado;
    
    public CodificadorHuffman() {
        this.diccionario = new HashMap<>();
        this.frecuencias = new HashMap<>();
    }
    
    public void codificarMensaje(String mensaje) {
        // Limpiar estructuras anteriores
        this.diccionario.clear();
        this.frecuencias.clear();
        
        this.mensajeOriginal = mensaje;
        calcularFrecuencias(mensaje);
        
        // Verificar que hay caracteres para codificar
        if (frecuencias.isEmpty()) {
            throw new IllegalArgumentException("El mensaje no contiene caracteres válidos");
        }
        
        NodoHuffman raiz = construirArbol();
        generarCodigos(raiz, "");
        this.mensajeCodificado = codificarTexto(mensaje);
    }
    
    private void calcularFrecuencias(String mensaje) {
        frecuencias.clear();
        for (char c : mensaje.toCharArray()) {
            frecuencias.put(c, frecuencias.getOrDefault(c, 0) + 1);
        }
    }
    
    private NodoHuffman construirArbol() {
        PriorityQueue<NodoHuffman> cola = new PriorityQueue<>();
        
        // Crear nodos hoja para cada caracter
        for (Map.Entry<Character, Integer> entry : frecuencias.entrySet()) {
            cola.offer(new NodoHuffman(entry.getKey(), entry.getValue()));
        }
        
        // Construir el árbol
        while (cola.size() > 1) {
            NodoHuffman izquierdo = cola.poll();
            NodoHuffman derecho = cola.poll();
            
            NodoHuffman padre = new NodoHuffman(
                izquierdo.frecuencia + derecho.frecuencia,
                izquierdo, derecho
            );
            
            cola.offer(padre);
        }
        
        return cola.poll();
    }
    
    private void generarCodigos(NodoHuffman nodo, String codigo) {
        if (nodo == null) return;
        
        if (nodo.esHoja()) {
            diccionario.put(nodo.caracter, codigo.isEmpty() ? "0" : codigo);
        } else {
            generarCodigos(nodo.izquierdo, codigo + "0");
            generarCodigos(nodo.derecho, codigo + "1");
        }
    }
    
    private String codificarTexto(String texto) {
        StringBuilder codificado = new StringBuilder();
        for (char c : texto.toCharArray()) {
            String codigo = diccionario.get(c);
            if (codigo == null) {
                throw new IllegalStateException("No se encontró código para el carácter: '" + c + "'");
            }
            codificado.append(codigo);
        }
        return codificado.toString();
    }
    
    // Getters
    public Map<Character, String> getDiccionario() {
        return Collections.unmodifiableMap(diccionario);
    }
    
    public Map<Character, Integer> getFrecuencias() {
        return Collections.unmodifiableMap(frecuencias);
    }
    
    public String getMensajeCodificado() {
        return mensajeCodificado;
    }
    
    public String getMensajeOriginal() {
        return mensajeOriginal;
    }
    
    // Cálculos estadísticos
    public double calcularEntropia() {
        int longitudTotal = mensajeOriginal.length();
        if (longitudTotal == 0) return 0.0;
        
        double entropia = 0.0;
        
        for (int frecuencia : frecuencias.values()) {
            double probabilidad = (double) frecuencia / longitudTotal;
            if (probabilidad > 0) {
                entropia += probabilidad * (Math.log(probabilidad) / Math.log(2));
            }
        }
        
        return -entropia;
    }
    
    public double calcularLargoMedio() {
        int longitudTotal = mensajeOriginal.length();
        if (longitudTotal == 0) return 0.0;
        
        double largoMedio = 0.0;
        
        for (Map.Entry<Character, Integer> entry : frecuencias.entrySet()) {
            double probabilidad = (double) entry.getValue() / longitudTotal;
            String codigo = diccionario.get(entry.getKey());
            if (codigo != null) {
                largoMedio += probabilidad * codigo.length();
            }
        }
        
        return largoMedio;
    }
    
    public double calcularEficiencia() {
        double entropia = calcularEntropia();
        double largoMedio = calcularLargoMedio();
        if (largoMedio == 0) return 0.0;
        return (entropia / largoMedio) * 100;
    }
}
