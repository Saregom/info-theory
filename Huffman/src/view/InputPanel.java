package view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.border.TitledBorder;

/**
 * Panel para capturar el mensaje de entrada
 */
public class InputPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextField textFieldMessage;
	private JButton btnEncode;
	private JButton btnSave;
	
	public InputPanel() {
		setLayout(null);
		setBounds(10, 10, 684, 120);
		setBorder(new TitledBorder(null, "Entrada de Datos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JLabel lblMessage = new JLabel("Mensaje:");
		lblMessage.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblMessage.setBounds(20, 30, 80, 25);
		add(lblMessage);
		
		textFieldMessage = new JTextField();
		textFieldMessage.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldMessage.setBounds(110, 30, 550, 25);
		add(textFieldMessage);
		textFieldMessage.setColumns(10);
		
		btnEncode = new JButton("Codificar");
		btnEncode.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnEncode.setBackground(new Color(100, 149, 237));
		btnEncode.setForeground(Color.WHITE);
		btnEncode.setBounds(190, 70, 140, 35);
		add(btnEncode);
		
		btnSave = new JButton("Guardar Archivo");
		btnSave.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnSave.setBackground(new Color(76, 175, 80));
		btnSave.setForeground(Color.WHITE);
		btnSave.setBounds(350, 70, 160, 35);
		add(btnSave);
		
		// Permitir presionar Enter en el campo de texto para codificar
		textFieldMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnEncode.doClick();
				}
			}
		});
	}
	
	/**
	 * Obtiene el texto ingresado
	 * @return el mensaje de entrada
	 */
	public String getMessage() {
		return textFieldMessage.getText();
	}
	
	/**
	 * Limpia el campo de texto
	 */
	public void clearMessage() {
		textFieldMessage.setText("");
	}
	
	/**
	 * Obtiene el botón de codificación para agregar listeners
	 * @return el botón de codificación
	 */
	public JButton getEncodeButton() {
		return btnEncode;
	}
	
	/**
	 * Obtiene el botón de guardar para agregar listeners
	 * @return el botón de guardar
	 */
	public JButton getSaveButton() {
		return btnSave;
	}
}
