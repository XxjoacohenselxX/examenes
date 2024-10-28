package ar.edu.et7;

import java.util.List;

public class Question {
    private String title;       // Título de la pregunta
    private String category;    // Categoría de la pregunta
    private String stimulus;    // Estímulo de la pregunta
    private String prompt;      // Texto de la pregunta
    private List<Choice> choices; // Opciones de respuesta
    private int points;         // Puntos que vale la pregunta
    private List<String> answers; // Respuestas correctas

    // Getters y Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStimulus() {
        return stimulus;
    }

    public void setStimulus(String stimulus) {
        this.stimulus = stimulus;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<String> getAnswers() {
        return answers; // Cambiado a List<String>
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers; // Cambiado a List<String>
    }

    // Clase interna Choice
    public static class Choice {
        private String id;       // ID de la opción
        private String content;  // Contenido de la opción

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        // Getters y Setters para Choice...
    }
}
