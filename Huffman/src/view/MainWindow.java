package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import modelo.CodificadorHuffman;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Ventana principal del codificador de Huffman
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private InputPanel inputPanel;
	private ResultPanel resultPanel;
	private MetricsPanel metricsPanel;
	private CodificadorHuffman codificador;

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		
		codificador = new CodificadorHuffman();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 700);
		setLocationRelativeTo(null);
		setTitle("Codificador de Huffman");
		setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Crear e inicializar los paneles modulares
		inputPanel = new InputPanel();
		contentPane.add(inputPanel);
		
		resultPanel = new ResultPanel();
		contentPane.add(resultPanel);
		
		metricsPanel = new MetricsPanel();
		contentPane.add(metricsPanel);
		
		// Configurar el listener del botón de codificación
		setupListeners();
	}
	
	/**
	 * Configura los listeners de los componentes
	 */
	private void setupListeners() {
		inputPanel.getEncodeButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				encodeMessage();
			}
		});
		
		inputPanel.getSaveButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				guardarArchivo();
			}
		});
	}
	
	/**
	 * Método para codificar el mensaje usando Huffman
	 */
	private void encodeMessage() {
		String message = inputPanel.getMessage();
		
		// Limpiar resultados anteriores
		resultPanel.clearResults();
		metricsPanel.clearAll();
		
		// Validar que el mensaje no esté vacío
		if (message.isEmpty()) {
			JOptionPane.showMessageDialog(this, 
				"Por favor, ingrese un mensaje para codificar", 
				"Mensaje Vacío", 
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		try {
			// Codificar el mensaje usando Huffman
			codificador.codificarMensaje(message);
			
			// Obtener los resultados
			Map<Character, String> diccionario = codificador.getDiccionario();
			Map<Character, Integer> frecuencias = codificador.getFrecuencias();
			int longitudTotal = message.length();
			
			// Crear una lista de caracteres ordenada por frecuencia (descendente)
			List<Character> caracteresOrdenados = new ArrayList<>(frecuencias.keySet());
			caracteresOrdenados.sort(new Comparator<Character>() {
				@Override
				public int compare(Character c1, Character c2) {
					// Ordenar de mayor a menor frecuencia
					return frecuencias.get(c2).compareTo(frecuencias.get(c1));
				}
			});
			
			// Llenar la tabla con los resultados ordenados
			for (Character caracter : caracteresOrdenados) {
				String codigo = diccionario.get(caracter);
				int frecuencia = frecuencias.get(caracter);
				double probabilidad = (double) frecuencia / longitudTotal;
				
				// Formatear el símbolo para visualización
				String simbolo = (caracter == ' ') ? "[ESPACIO]" : 
							   (caracter == '\n') ? "[SALTO]" : 
							   (caracter == '\t') ? "[TAB]" :
							   String.valueOf(caracter);
				
				resultPanel.addResult(simbolo, probabilidad, codigo);
			}
			
			// Mostrar el mensaje codificado y las métricas
			metricsPanel.setEncodedMessage(codificador.getMensajeCodificado());
			metricsPanel.setEntropy(codificador.calcularEntropia());
			metricsPanel.setAverageLength(codificador.calcularLargoMedio());
			metricsPanel.setEfficiency(codificador.calcularEficiencia());
			
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, 
				"Error al codificar el mensaje: " + ex.getMessage(), 
				"Error", 
				JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	/**
	 * Guarda los resultados de la codificación en un archivo
	 */
	private void guardarArchivo() {
		if (codificador.getMensajeCodificado() == null || codificador.getMensajeCodificado().isEmpty()) {
			JOptionPane.showMessageDialog(this, 
				"No hay mensaje codificado para guardar. Por favor, codifique un mensaje primero.", 
				"Sin Datos", 
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Guardar archivo Huffman");
		
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				String filePath = fileChooser.getSelectedFile().getAbsolutePath();
				if (!filePath.toLowerCase().endsWith(".txt")) {
					filePath += ".txt";
				}
				guardarDatosHuffman(filePath);
				JOptionPane.showMessageDialog(this, 
					"Archivo guardado exitosamente en:\n" + filePath,
					"Guardado Exitoso",
					JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, 
					"Error al guardar el archivo: " + ex.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Guarda los datos de la codificación Huffman en un archivo de texto
	 */
	private void guardarDatosHuffman(String filePath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			// Guardar diccionario
			writer.write("=== DICCIONARIO HUFFMAN ===\n");
			Map<Character, String> diccionario = codificador.getDiccionario();
			Map<Character, Integer> frecuencias = codificador.getFrecuencias();
			int longitudTotal = codificador.getMensajeOriginal().length();
			
			// Crear una lista de caracteres ordenada por frecuencia (descendente)
			List<Character> caracteresOrdenados = new ArrayList<>(frecuencias.keySet());
			caracteresOrdenados.sort(new Comparator<Character>() {
				@Override
				public int compare(Character c1, Character c2) {
					// Ordenar de mayor a menor frecuencia
					return frecuencias.get(c2).compareTo(frecuencias.get(c1));
				}
			});
			
			// Guardar los símbolos ordenados
			for (Character caracter : caracteresOrdenados) {
				String simbolo = (caracter == ' ') ? "[ESPACIO]" : 
							   (caracter == '\n') ? "[SALTO]" : 
							   (caracter == '\t') ? "[TAB]" :
							   String.valueOf(caracter);
				
				double probabilidad = (double) frecuencias.get(caracter) / longitudTotal;
				String codigo = diccionario.get(caracter);
				
				writer.write(String.format("Símbolo: %s | Probabilidad: %.4f | Código: %s\n", 
					simbolo, probabilidad, codigo));
			}
			
			// Guardar estadísticas
			writer.write("\n=== ESTADÍSTICAS ===\n");
			writer.write(String.format("Entropía H(S): %.4f bits\n", codificador.calcularEntropia()));
			writer.write(String.format("Largo medio (L): %.4f bits\n", codificador.calcularLargoMedio()));
			writer.write(String.format("Eficiencia (η): %.2f%%\n", codificador.calcularEficiencia()));
			
			// Guardar mensaje codificado
			writer.write("\n=== MENSAJE CODIFICADO ===\n");
			writer.write(codificador.getMensajeCodificado());
			writer.write("\n");
			
			// Guardar mensaje original
			writer.write("\n=== MENSAJE ORIGINAL ===\n");
			writer.write(codificador.getMensajeOriginal());
		}
	}
}