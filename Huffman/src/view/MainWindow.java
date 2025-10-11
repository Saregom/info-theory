package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import modelo.FileExporter;
import modelo.HuffmanEncoder;
import modelo.HuffmanResult;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Ventana principal del codificador de Huffman
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private InputPanel inputPanel;
	private ResultPanel resultPanel;
	private MetricsPanel metricsPanel;
	private HuffmanResult currentResult; // Guardar el resultado actual para exportar
	private String currentMessage; // Guardar el mensaje actual

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		
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
		
		inputPanel.getExportButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportResults();
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
		inputPanel.disableExport();
		
		// Validar que el mensaje no esté vacío
		if (message.isEmpty()) {
			return;
		}
		
		try {
			// Codificar el mensaje usando el algoritmo de Huffman
			HuffmanResult result = HuffmanEncoder.encode(message);
			
			// Mostrar los resultados en la tabla
			for (int i = 0; i < result.getSymbolCount(); i++) {
				resultPanel.addResult(
					String.valueOf(result.getSymbol(i)),
					result.getFrequency(i),
					result.getProbability(i),
					result.getCode(i)
				);
			}
			
			// Mostrar las métricas y el mensaje codificado
			metricsPanel.setEncodedMessage(result.getEncodedMessage());
			metricsPanel.setEntropy(result.getEntropy());
			metricsPanel.setAverageLength(result.getAverageLength());
			metricsPanel.setEfficiency(result.getEfficiency());
			inputPanel.enableExport();
			
			// Guardar el resultado y mensaje actual para exportar
			currentResult = result;
			currentMessage = message;
			
		} catch (Exception ex) {
			// Manejar errores
			System.err.println("Error al codificar el mensaje: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * Exporta los resultados a un archivo
	 */
	private void exportResults() {
		if (currentResult == null) {
			JOptionPane.showMessageDialog(this, 
				"No hay resultados para exportar.", 
				"Error", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Crear el diálogo de guardar archivo
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Guardar resultados de Huffman");
		
		// Establecer filtro de archivos
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt");
		fileChooser.setFileFilter(filter);
		
		// Sugerir un nombre de archivo
		fileChooser.setSelectedFile(new File(FileExporter.getSuggestedFileName()));
		
		// Mostrar el diálogo
		int userSelection = fileChooser.showSaveDialog(this);
		
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			
			// Asegurarse de que el archivo tenga la extensión .txt
			if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
				fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
			}
			
			try {
				// Exportar los resultados
				FileExporter.exportToFile(currentResult, fileToSave.getAbsolutePath(), currentMessage);
				
				// Mostrar mensaje de éxito
				JOptionPane.showMessageDialog(this, 
					"Resultados exportados exitosamente a:\n" + fileToSave.getAbsolutePath(), 
					"Éxito", 
					JOptionPane.INFORMATION_MESSAGE);
				
			} catch (Exception ex) {
				// Mostrar mensaje de error
				JOptionPane.showMessageDialog(this, 
					"Error al exportar el archivo:\n" + ex.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
}