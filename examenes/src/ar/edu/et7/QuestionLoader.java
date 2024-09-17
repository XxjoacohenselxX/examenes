package ar.edu.et7;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class QuestionLoader {
    public List<Question> loadQuestions(String filePath) throws Exception {
        List<Question> questions = new ArrayList<>();
        FileReader reader = new FileReader(filePath);
        StringBuilder jsonContent = new StringBuilder();
        int i;
        while ((i = reader.read()) != -1) {
            jsonContent.append((char) i);
        }
        reader.close();
        JSONArray jsonArray = new JSONArray(jsonContent.toString());
        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject jsonObject = jsonArray.getJSONObject(j);
            Question question = new Question();
            question.setTitle(jsonObject.getString("title"));
            question.setCategory(jsonObject.getString("category"));
            question.setStimulus(jsonObject.getString("stimulus"));
            question.setPrompt(jsonObject.getString("prompt"));
            question.setPoints(jsonObject.getInt("points"));

            List<Question.Choice> choices = new ArrayList<>();
            JSONArray choicesArray = jsonObject.getJSONArray("choices");
            for (int k = 0; k < choicesArray.length(); k++) {
                JSONObject choiceObject = choicesArray.getJSONObject(k);
                Question.Choice choice = new Question.Choice();
                choice.setId(choiceObject.getString("id"));
                choice.setContent(choiceObject.getString("content"));
                choices.add(choice);
            }
            question.setChoices(choices);

            JSONArray answersArray = jsonObject.getJSONArray("answers");
            List<List<String>> answers = new ArrayList<>();
            for (int l = 0; l < answersArray.length(); l++) {
                JSONArray answerArray = answersArray.getJSONArray(l);
                List<String> answerList = new ArrayList<>();
                for (int m = 0; m < answerArray.length(); m++) {
                    answerList.add(answerArray.getString(m));
                }
                answers.add(answerList);
            }
            question.setAnswers(answers);

            questions.add(question);
        }
        return questions;
    }
}
