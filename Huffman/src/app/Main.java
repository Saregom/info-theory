package app;

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
