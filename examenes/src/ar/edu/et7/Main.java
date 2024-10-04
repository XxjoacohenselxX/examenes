package ar.edu.et7;

import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.JFileChooser;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            // Crear un JFileChooser para seleccionar el archivo JSON
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                // Obtener el archivo seleccionado
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();

                // Cargar las preguntas desde el archivo seleccionado
                QuestionLoader loader = new QuestionLoader();
                List<Question> questions = loader.loadQuestions(filePath);
                
                SwingUtilities.invokeLater(() -> new QuestionDisplay(questions).setVisible(true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
