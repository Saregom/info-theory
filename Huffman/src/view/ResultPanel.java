package view;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.Font;

/**
 * Panel para mostrar los resultados de la codificación
 */
public class ResultPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel tableModel;
	
	public ResultPanel() {
		setLayout(null);
		setBounds(10, 140, 684, 290);
		setBorder(new TitledBorder(null, "Códigos de Huffman", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		// Crear el modelo de la tabla
		String[] columnNames = {"Símbolo", "Frecuencia", "Probabilidad", "Código Binario"};
		tableModel = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Hacer la tabla no editable
			}
		};
		
		// Crear la tabla
		table = new JTable(tableModel);
		table.setFont(new Font("Tahoma", Font.PLAIN, 13));
		table.setRowHeight(25);
		table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
		
		// Ajustar el ancho de las columnas
		table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Símbolo
		table.getColumnModel().getColumn(1).setPreferredWidth(100); // Frecuencia
		table.getColumnModel().getColumn(2).setPreferredWidth(120); // Probabilidad
		table.getColumnModel().getColumn(3).setPreferredWidth(300); // Código Binario
		
		// Agregar la tabla a un scroll pane
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 30, 644, 240);
		add(scrollPane);
	}
	
	/**
	 * Agrega una fila a la tabla de resultados
	 * @param symbol el símbolo
	 * @param frequency la frecuencia del símbolo
	 * @param probability la probabilidad del símbolo
	 * @param code el código binario
	 */
	public void addResult(String symbol, int frequency, double probability, String code) {
		String symbolStr = (symbol.equals(" ")) ? "(Espacio)" : symbol;
		Object[] row = {symbolStr, frequency, String.format("%.4f", probability), code};
		tableModel.addRow(row);
	}
	
	/**
	 * Limpia todos los resultados de la tabla
	 */
	public void clearResults() {
		tableModel.setRowCount(0);
	}
	
	/**
	 * Obtiene el modelo de la tabla para manipulación avanzada
	 * @return el modelo de la tabla
	 */
	public DefaultTableModel getTableModel() {
		return tableModel;
	}
}
