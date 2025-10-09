package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import modelo.TestData;

/**
 * Ventana principal del codificador de Huffman
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private InputPanel inputPanel;
	private ResultPanel resultPanel;
	private MetricsPanel metricsPanel;

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
	}
	
	/**
	 * Método para codificar el mensaje (sin lógica por ahora)
	 */
	private void encodeMessage() {
		String message = inputPanel.getMessage();
		
		// Limpiar resultados anteriores
		resultPanel.clearResults();
		metricsPanel.clearAll();
		
		// TODO: Aquí se llamara la funcion para calcular la de codificación de Huffman
		// Por ahora, usamos datos de prueba simulados
		if (!message.isEmpty()) {
			// Obtener datos de prueba basados en el mensaje
			TestData testData = TestData.createFromMessage(message);
			
			// Mostrar los datos de prueba en la tabla
			for (int i = 0; i < testData.size(); i++) {
				resultPanel.addResult(
					testData.getSymbol(i),
					testData.getFrequency(i),
					testData.getProbability(i),
					testData.getBinaryCode(i)
				);
			}
			
			// Mostrar las métricas y el mensaje codificado
			metricsPanel.setEncodedMessage(testData.getEncodedMessage());
			metricsPanel.setEntropy(testData.getEntropy());
			metricsPanel.setAverageLength(testData.getAverageLength());
			metricsPanel.setEfficiency(testData.getEfficiency());
		}
	}
}