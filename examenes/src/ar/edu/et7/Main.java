package ar.edu.et7;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            QuestionLoader loader = new QuestionLoader();
            // Intentar cargar el archivo por defecto
            List<Question> questions = loader.loadQuestions("resources/questions.json");
            SwingUtilities.invokeLater(() -> new QuestionDisplay(questions).setVisible(true));
        } catch (Exception e) {
            e.printStackTrace();
            
            // Mostrar un file chooser si el archivo no se encuentra
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos JSON", "json");
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Selecciona el archivo JSON de preguntas");
            
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    QuestionLoader loader = new QuestionLoader();
                    // Cargar las preguntas desde el archivo seleccionado
                    List<Question> questions = loader.loadQuestions(selectedFile.getAbsolutePath());
                    SwingUtilities.invokeLater(() -> new QuestionDisplay(questions).setVisible(true));
                } catch (Exception ex) {
                    // Manejo de errores al cargar el archivo seleccionado
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al cargar el archivo seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // El usuario cancelo la seleccion del archivo
                JOptionPane.showMessageDialog(null, "No se selecciono ningun archivo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}