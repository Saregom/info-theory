package view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;

/**
 * Panel para mostrar el mensaje codificado y las métricas de la codificación
 */
public class MetricsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextArea textAreaEncodedMessage;
	private JLabel lblEntropyValue;
	private JLabel lblAverageLengthValue;
	private JLabel lblEfficiencyValue;
	
	public MetricsPanel() {
		setLayout(null);
		setBounds(10, 440, 684, 200);
		setBorder(new TitledBorder(null, "Mensaje Codificado y Métricas", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		// Etiqueta para mensaje codificado
		JLabel lblEncoded = new JLabel("Mensaje Codificado:");
		lblEncoded.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblEncoded.setBounds(20, 25, 150, 20);
		add(lblEncoded);
		
		// Área de texto para el mensaje codificado
		textAreaEncodedMessage = new JTextArea();
		textAreaEncodedMessage.setFont(new Font("Courier New", Font.PLAIN, 12));
		textAreaEncodedMessage.setLineWrap(true);
		textAreaEncodedMessage.setWrapStyleWord(false);
		textAreaEncodedMessage.setEditable(false);
		textAreaEncodedMessage.setBackground(new Color(245, 245, 245));
		
		JScrollPane scrollPane = new JScrollPane(textAreaEncodedMessage);
		scrollPane.setBounds(20, 50, 644, 60);
		add(scrollPane);
		
		// Panel de métricas
		JPanel metricsSubPanel = new JPanel();
		metricsSubPanel.setLayout(null);
		metricsSubPanel.setBounds(20, 120, 644, 70);
		metricsSubPanel.setBorder(new TitledBorder(null, "Métricas de Codificación", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(metricsSubPanel);
		
		// Entropía H(S)
		JLabel lblEntropy = new JLabel("Entropía H(S):");
		lblEntropy.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblEntropy.setBounds(20, 25, 100, 20);
		metricsSubPanel.add(lblEntropy);
		
		lblEntropyValue = new JLabel("0.0000 bits");
		lblEntropyValue.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblEntropyValue.setBounds(120, 25, 100, 20);
		metricsSubPanel.add(lblEntropyValue);
		
		// Largo medio L
		JLabel lblAverageLength = new JLabel("Largo Medio (L):");
		lblAverageLength.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAverageLength.setBounds(240, 25, 120, 20);
		metricsSubPanel.add(lblAverageLength);
		
		lblAverageLengthValue = new JLabel("0.0000 bits");
		lblAverageLengthValue.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblAverageLengthValue.setBounds(360, 25, 100, 20);
		metricsSubPanel.add(lblAverageLengthValue);
		
		// Eficiencia η
		JLabel lblEfficiency = new JLabel("Eficiencia (η):");
		lblEfficiency.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblEfficiency.setBounds(480, 25, 100, 20);
		metricsSubPanel.add(lblEfficiency);
		
		lblEfficiencyValue = new JLabel("0.00%");
		lblEfficiencyValue.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblEfficiencyValue.setBounds(580, 25, 60, 20);
		metricsSubPanel.add(lblEfficiencyValue);
	}
	
	/**
	 * Establece el mensaje codificado
	 * @param encodedMessage el mensaje en binario
	 */
	public void setEncodedMessage(String encodedMessage) {
		textAreaEncodedMessage.setText(encodedMessage);
	}
	
	/**
	 * Establece el valor de la entropía
	 * @param entropy valor de la entropía
	 */
	public void setEntropy(double entropy) {
		lblEntropyValue.setText(String.format("%.4f bits", entropy));
	}
	
	/**
	 * Establece el valor del largo medio
	 * @param averageLength valor del largo medio
	 */
	public void setAverageLength(double averageLength) {
		lblAverageLengthValue.setText(String.format("%.4f bits", averageLength));
	}
	
	/**
	 * Establece el valor de la eficiencia
	 * @param efficiency valor de la eficiencia en porcentaje
	 */
	public void setEfficiency(double efficiency) {
		lblEfficiencyValue.setText(String.format("%.2f%%", efficiency));
	}
	
	/**
	 * Limpia todos los valores
	 */
	public void clearAll() {
		textAreaEncodedMessage.setText("");
		lblEntropyValue.setText("0.0000 bits");
		lblAverageLengthValue.setText("0.0000 bits");
		lblEfficiencyValue.setText("0.00%");
	}
}


