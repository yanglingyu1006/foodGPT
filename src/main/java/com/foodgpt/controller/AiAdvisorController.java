package com.foodgpt.controller;

import com.foodgpt.service.AiAdvisorService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiAdvisorController {

    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendBtn;

    private AiAdvisorService aiAdvisorService;
    private ObservableList<String> messages = FXCollections.observableArrayList();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void setService(AiAdvisorService aiAdvisorService) {
        this.aiAdvisorService = aiAdvisorService;
        messageListView.setItems(messages);
    }

    @FXML
    private void handleSend() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;

        messages.add("用户: " + message);
        inputField.clear();
        scrollToBottom();

        sendBtn.setDisable(true);
        messages.add("AI: 正在思考...");
        scrollToBottom();

        executor.submit(() -> {
            String response = aiAdvisorService.getAdvice(message);
            Platform.runLater(() -> {
                messages.remove(messages.size() - 1);
                messages.add("AI: " + response);
                scrollToBottom();
                sendBtn.setDisable(false);
            });
        });
    }

    private void scrollToBottom() {
        if (!messages.isEmpty()) {
            messageListView.scrollTo(messages.size() - 1);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
