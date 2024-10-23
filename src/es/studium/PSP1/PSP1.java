package es.studium.PSP1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.border.LineBorder;

public class PSP1 extends JFrame {

	private JTextField txtExtension;
	private JTextArea txtResults;
	private JButton btnBuscar;
	private ArrayList<String> archivosres;
	private String extension;

	public PSP1() {

		setTitle("PSP - Practica Tema 1");

		setSize(600, 400);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLayout(new BorderLayout());

		JPanel panelBottom = new JPanel();
		panelBottom.setLayout(new FlowLayout());
		panelBottom.setBackground(Color.LIGHT_GRAY);

		JLabel lblExtension = new JLabel("Escribe una extensión:");
		txtExtension = new JTextField(10);
		btnBuscar = new JButton("Buscar");

		txtExtension.setBorder(new LineBorder(Color.DARK_GRAY, 2, true));
		txtExtension.setBackground(Color.WHITE);
		txtExtension.setForeground(Color.BLACK);

		panelBottom.add(lblExtension);
		panelBottom.add(txtExtension);
		panelBottom.add(btnBuscar);

		txtResults = new JTextArea();
		txtResults.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(txtResults);

		add(scrollPane, BorderLayout.CENTER);
		add(panelBottom, BorderLayout.SOUTH);

		archivosres = new ArrayList<>();
		btnBuscar.addActionListener(e -> iniciarBusqueda());
		txtResults.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int caretPosition = txtResults.getCaretPosition();
					String selectedFile = obtenerselec(caretPosition);
					if (selectedFile.endsWith(".exe")) {
						ejecutarexe(selectedFile);
					}
				}
			}
		});
	}

	private void iniciarBusqueda() {

		txtResults.setText("");
		archivosres.clear();

		extension = txtExtension.getText().trim();

		if (extension.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Introduce una extensión válida");
			return;
		}

		if (!extension.startsWith(".")) {
			extension = "." + extension;
		}

		File[] unidades = File.listRoots();

		SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
			@Override
			protected Void doInBackground() throws Exception {
				for (File unidad : unidades) {
					buscardir(unidad);
				}
				return null;
			}

			@Override
			protected void process(java.util.List<String> chunks) {
				for (String archivo : chunks) {
					txtResults.append(archivo + "\n");
				}
			}

			@Override
			protected void done() {
				Busquedaterminada();
			}

			private void buscardir(File directorio) {
				File[] archivos = directorio.listFiles();
				if (archivos != null) {
					for (File archivo : archivos) {
						if (archivo.isDirectory()) {
							buscardir(archivo);
						} else if (archivo.getName().endsWith(extension)) {
							archivosres.add(archivo.getAbsolutePath());
							publish(archivo.getAbsolutePath());
						}
					}
				}
			}
		};

		worker.execute();
	}

	private void Busquedaterminada() {

		JDialog dialog = new JDialog(this, "Búsqueda", true);
		dialog.setLayout(new BorderLayout());
		dialog.setSize(300, 150);

		JLabel label = new JLabel("Búsqueda completada!", SwingConstants.CENTER);
		JButton btnCerrar = new JButton("Cerrar");

		btnCerrar.addActionListener(e -> dialog.dispose());

		dialog.add(label, BorderLayout.CENTER);
		dialog.add(btnCerrar, BorderLayout.SOUTH);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private String obtenerselec(int caretPosition) {

		try {
			int start = caretPosition;
			while (start > 0 && txtResults.getText(start - 1, 1).charAt(0) != '\n') {
				start--;
			}

			int end = caretPosition;
			while (end < txtResults.getText().length() && txtResults.getText(end, 1).charAt(0) != '\n') {
				end++;
			}

			return txtResults.getText(start, end - start).trim();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	private void ejecutarexe(String rutaExe) {

		try {
			Desktop.getDesktop().open(new File(rutaExe));
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "No se pudo ejecutar el archivo .exe: " + rutaExe);
		}
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {
			PSP1 frame = new PSP1();
			frame.setVisible(true);
		});
	}
}
