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

import java.io.File;
import java.io.IOException;

public class QuestionDisplay extends JFrame {
    private static final long serialVersionUID = 1L;
    private List<List<String>> userAnswers; // Almacena las respuestas seleccionadas del usuario
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private JLabel titleLabel;
    private JTextArea promptArea;
    private JCheckBox[] choiceButtons; // Checkboxes para múltiples respuestas
    private JButton nextButton;
    private Timer timer;
    private JLabel timerLabel;
    private int timeRemaining = 30 * 60; // 30 minutos en segundos
    private int score = 0;
    private JProgressBar progressBar; // Barra de progreso
    private JLabel questionCountLabel; // Etiqueta para contar las preguntas

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
        promptArea = new JTextArea();
        promptArea.setEditable(false);

        // Añadir componentes al contenedor
        add(titleLabel);
        add(promptArea);

// Grupo 7
        // Inicialización de checkboxes
        choiceButtons = new JCheckBox[4];
        for (int i = 0; i < choiceButtons.length; i++) {
            choiceButtons[i] = new JCheckBox();
            add(choiceButtons[i]);
        }

        // Botón para pasar a la siguiente pregunta
        nextButton = new JButton("Siguiente");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
                showQuestion();
            }
        });
        add(nextButton);

        // Inicializar y añadir el temporizador
        timerLabel = new JLabel();
        add(timerLabel);

        // Iniciar el temporizador
        startTimer();
        
// Grupo 1
        // Inicializar la barra de progreso
        progressBar = new JProgressBar(0, questions.size());
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        add(progressBar);

        // Inicializar la etiqueta de conteo de preguntas
        questionCountLabel = new JLabel();
        add(questionCountLabel);

        // Mostrar la primera pregunta
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            titleLabel.setText("Title: " + q.getTitle());
            promptArea.setText(q.getPrompt());

            // Verificar el tamaño de choices y asegurarse de que no haya más opciones que botones
            if (q.getChoices().size() > choiceButtons.length) {
                throw new IllegalStateException("Más opciones que botones de elección disponibles");
            }
            
// Grupo 7 
            // Limpiar y actualizar los checkboxes
            for (int i = 0; i < choiceButtons.length; i++) {
                if (i < q.getChoices().size()) {
                    Question.Choice choice = q.getChoices().get(i);
                    choiceButtons[i].setText(choice.getContent());
                    choiceButtons[i].setActionCommand(choice.getId());
                    choiceButtons[i].setVisible(true);
                } else {
                    choiceButtons[i].setText("");
                    choiceButtons[i].setVisible(false);
                }
            }

            // Deseleccionar todos los checkboxes
            for (JCheckBox button : choiceButtons) {
                button.setSelected(false);
            }

            progressBar.setValue(currentQuestionIndex + 1);
            questionCountLabel.setText("Pregunta " + (currentQuestionIndex + 1) + " de " + questions.size());
            currentQuestionIndex++;
        } else {
            endQuiz();
        }
    }

 // Grupo 5
    private void checkAnswer() {
        if (currentQuestionIndex > 0) {
            Question q = questions.get(currentQuestionIndex - 1);
            List<String> selectedChoiceIds = getSelectedChoiceIds();

            // Almacenar las respuestas del usuario para esta pregunta
            userAnswers.add(selectedChoiceIds);

            // Verificar el puntaje usando la lógica del método calculateScore
            score += calculateScore(q);
        }
    }

    private List<String> getSelectedChoiceIds() {
        List<String> selectedIds = new ArrayList<>();
        for (JCheckBox button : choiceButtons) {
            if (button.isSelected()) {
                selectedIds.add(button.getActionCommand());
            }
        }
        return selectedIds;
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
    
 // Grupo3
    @SuppressWarnings("deprecation")
    private void endQuiz() {
        // Muestra el puntaje al usuario
        JOptionPane.showMessageDialog(this, "Quiz terminado. Puntaje: " + score);

        // Crea el documento PDF
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Quiz terminado. Puntaje: " + score);
            contentStream.newLineAtOffset(0, -20);

            // Agrega cada pregunta y respuesta al PDF
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                contentStream.showText("Pregunta: " + q.getPrompt());
                contentStream.newLineAtOffset(0, -20);

                // Respuestas correctas
                String correctAnswerText = getCorrectAnswerText(q);
                contentStream.setNonStrokingColor(0, 128, 0); // Color verde para respuestas correctas
                contentStream.showText("Respuesta(s) correcta(s): " + correctAnswerText);
                contentStream.newLineAtOffset(0, -20);

                // Respuesta del usuario
                List<String> userAnswerIds = userAnswers.get(i);
                String userAnswerText = getUserAnswerText(q, userAnswerIds);
                contentStream.setNonStrokingColor(255, 0, 0); // Color rojo para respuesta del usuario
                contentStream.showText("Tu respuesta(s): " + userAnswerText);
                contentStream.newLineAtOffset(0, -20);

                // Resetea el color a negro
                contentStream.setNonStrokingColor(0, 0, 0);
            }

            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }

// Grupo 3   
        // Guarda el PDF y cierra el documento
        try {
            document.save("archivo.pdf");
            document.close();

            // Abre el archivo PDF generado
            Desktop.getDesktop().open(new File("archivo.pdf"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Cierra el programa después de finalizar el cuestionario
        System.exit(0);
    }
    

    private String getCorrectAnswerText(Question q) {
        return q.getChoices().stream()
                 .filter(choice -> q.getAnswers().contains(choice.getId()))
                 .map(Question.Choice::getContent)
                 .reduce((a, b) -> a + ", " + b)
                 .orElse("Respuestas no encontradas");
    }

    private String getUserAnswerText(Question q, List<String> userAnswerIds) {
        return q.getChoices().stream()
                 .filter(choice -> userAnswerIds.contains(choice.getId()))
                 .map(Question.Choice::getContent)
                 .reduce((a, b) -> a + ", " + b)
                 .orElse("Respuestas no encontradas");
    }
//Grupo 5
    private int calculateScore(Question question) {
    	int scoreForQuestion = 0;
    	List<String> correctAnswers = question.getAnswers();

    	// Obtener respuestas seleccionadas
    	List<String> selectedAnswers = getSelectedChoiceIds();
    	int selectedCount = selectedAnswers.size();

    	// Penalización por no seleccionar ninguna respuesta o seleccionar todas las opciones
    	//if (selectedCount == 0 || selectedCount > correctAnswers.size()) {
    	//return -10; // Penalización de 10 puntos
    	//}

    	// Verificar si se seleccionaron todas las respuestas correctas
    	boolean hasAllCorrectAnswers = selectedAnswers.containsAll(correctAnswers) && selectedCount == correctAnswers.size();

    	// Sumar puntos solo si hay respuestas correctas seleccionadas
    	if (hasAllCorrectAnswers) {
    		scoreForQuestion += 10; // Sumar 10 puntos por respuestas correctas
    	}

return scoreForQuestion; // Retornar el puntaje total por la pregunta
						}
}
