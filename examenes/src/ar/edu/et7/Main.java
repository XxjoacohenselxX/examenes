package ar.edu.et7;

import java.util.List;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            QuestionLoader loader = new QuestionLoader();
            List<Question> questions = loader.loadQuestions("questions.json");
            SwingUtilities.invokeLater(() -> new QuestionDisplay(questions).setVisible(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
