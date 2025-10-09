package app;

//import java.util.ArrayList;
//import java.util.List;

//import Controlador.Restaurante;
//import modelo.Cliente;
//import modelo.Comida;
import view.MainWindow;

public class Main {
	public static void main(String[] args) {
		showWindow();
	}

	private static void showWindow() {
		
		MainWindow window = new MainWindow();
		window.setVisible(true);
	}
}
