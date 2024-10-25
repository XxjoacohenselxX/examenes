package ar.edu.et7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class QuestionDisplay extends JFrame {
    private static final long serialVersionUID = 1L;
    private List<String> userAnswers; // Almacena las respuestas del usuario
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private JLabel titleLabel;
    private JLabel stimulusLabel;
    private JTextArea promptArea;
    private JRadioButton[] choiceButtons; // Opciones de respuesta
    private JButton nextButton;
    private Timer timer;
    private JLabel timerLabel;
    private int timeRemaining = 30 * 60; // 30 minutos en segundos
    private int score = 0;

    public QuestionDisplay(List<Question> questions) {
        this.questions = questions;
        Collections.shuffle(this.questions); // Mezcla las preguntas

        userAnswers = new ArrayList<>();

        // Configuración de la ventana
        setTitle("Multiple Choice Quiz");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        // Inicialización de componentes
        titleLabel = new JLabel();
        stimulusLabel = new JLabel();
        promptArea = new JTextArea();
        promptArea.setEditable(false);

        // Añadir componentes al contenedor
        add(titleLabel);
        add(stimulusLabel);
        add(promptArea);

        // Inicialización de botones de opción
        choiceButtons = new JRadioButton[4];
        ButtonGroup buttonGroup = new ButtonGroup();
        for (int i = 0; i < choiceButtons.length; i++) {
            choiceButtons[i] = new JRadioButton();
            buttonGroup.add(choiceButtons[i]);
            add(choiceButtons[i]);
        }

        // Botón para pasar a la siguiente pregunta
        nextButton = new JButton("Siguiente");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
                showNextQuestion();
            }
        });
        add(nextButton);

        // Inicializar y añadir el temporizador
        timerLabel = new JLabel();
        add(timerLabel);

        // Iniciar el temporizador
        startTimer();

        // Mostrar la primera pregunta
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            titleLabel.setText("Title: " + q.getTitle());
            stimulusLabel.setText("Stimulus: " + q.getStimulus());
            promptArea.setText(q.getPrompt());

            // Limpiar y actualizar los botones de opción
            for (int i = 0; i < choiceButtons.length; i++) {
                if (i < q.getChoices().size()) {
                    Question.Choice choice = q.getChoices().get(i);
                    choiceButtons[i].setText(choice.getContent());
                    choiceButtons[i].setActionCommand(choice.getId());
                } else {
                    choiceButtons[i].setText(""); // Limpiar botones no utilizados
                }
            }

            // Deseleccionar todos los botones
            for (JRadioButton button : choiceButtons) {
                button.setSelected(false);
            }

            currentQuestionIndex++;
        } else {
            endQuiz();
        }
    }

    private void checkAnswer() {
        if (currentQuestionIndex > 0) {
            Question q = questions.get(currentQuestionIndex - 1);
            String selectedChoiceId = getSelectedChoiceId();

            // Almacenar la respuesta del usuario
            userAnswers.add(selectedChoiceId);

            if (selectedChoiceId != null && 
                q.getAnswers().stream().anyMatch(answer -> answer.contains(selectedChoiceId))) {
                score += q.getPoints(); // Aumentar puntaje por respuesta correcta
            }
        }
    }

    private String getSelectedChoiceId() {
        for (JRadioButton button : choiceButtons) {
            if (button.isSelected()) {
                return button.getActionCommand();
            }
        }
        return null;
    }

    private void showNextQuestion() {
        showQuestion();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeRemaining--;
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                timerLabel.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));
                if (timeRemaining <= 0) {
                    timer.cancel();
                    endQuiz();
                }
            }
        }, 0, 1000);
    }

    @SuppressWarnings("deprecation")
    private void endQuiz() {
        // Mostrar el puntaje
        JOptionPane.showMessageDialog(this, "Quiz terminado. Puntaje: " + score);
        
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page); // Agregar la página al documento

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(100, 700); // Establecer la posición del texto
            contentStream.showText("Quiz terminado. Puntaje: " + score); // Escribir el puntaje
            contentStream.newLineAtOffset(0, -20); // Bajar la posición

            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                contentStream.showText("Pregunta: " + q.getPrompt());
                contentStream.newLineAtOffset(0, -20);
                
                // Mostrar respuesta correcta
                String correctAnswerText = getCorrectAnswerText(q);
                contentStream.setNonStrokingColor(0, 128, 0); // Verde
                contentStream.showText("Respuesta correcta: " + correctAnswerText);
                contentStream.newLineAtOffset(0, -20);
                
                // Mostrar respuesta del usuario
                String userAnswer = userAnswers.get(i);
                String userAnswerText = getUserAnswerText(q, userAnswer);
                contentStream.setNonStrokingColor(255, 0, 0); // Rojo
                contentStream.showText("Tu respuesta: " + userAnswer + " (" + userAnswerText + ")");
                contentStream.newLineAtOffset(0, -20);

                // Resetear color a negro para el siguiente texto
                contentStream.setNonStrokingColor(0, 0, 0); // Negro
            }

            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Guardar el documento como archivo.pdf
        try {
            document.save("archivo.pdf");
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCorrectAnswerText(Question q) {
        // Obtener el texto de la respuesta correcta
        return q.getChoices().stream()
                 .filter(choice -> q.getAnswers().get(0).contains(choice.getId())) // Asumiendo que la primera respuesta es la correcta
                 .map(choice -> choice.getContent())
                 .findFirst()
                 .orElse("Respuesta no encontrada");
    }

    private String getUserAnswerText(Question q, String userAnswerId) {
        // Obtener el texto de la respuesta del usuario
        return q.getChoices().stream()
                 .filter(choice -> choice.getId().equals(userAnswerId))
                 .map(choice -> choice.getContent())
                 .findFirst()
                 .orElse("Respuesta no encontrada");
    }
}
