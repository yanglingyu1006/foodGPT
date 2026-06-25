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
    }

    @FXML
    private void initialize() {
        if (messageListView != null) {
            messageListView.setItems(messages);
        }
        
        messages.add("AI: 你好！我是你的AI饮食顾问，有什么关于饮食健康的问题可以问我。");
    }

    @FXML
    private void handleSend() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            showAlert("请输入问题");
            return;
        }

        sendRequest(message);
    }
    
    @FXML
    private void handleQuickQuestion1() {
        sendRequest("请给我今日的饮食建议");
    }
    
    @FXML
    private void handleQuickQuestion2() {
        sendRequest("如何搭配营养均衡的饮食？");
    }
    
    @FXML
    private void handleQuickQuestion3() {
        sendRequest("推荐一些健康的食谱");
    }
    
    private void sendRequest(String message) {
        messages.add("用户: " + message);
        if (inputField != null) {
            inputField.clear();
        }
        scrollToBottom();

        if (sendBtn != null) {
            sendBtn.setDisable(true);
        }
        messages.add("AI: 正在思考...");
        scrollToBottom();

        executor.submit(() -> {
            String response = aiAdvisorService.getAdvice(message);
            Platform.runLater(() -> {
                messages.remove(messages.size() - 1);
                messages.add("AI: " + response);
                scrollToBottom();
                if (sendBtn != null) {
                    sendBtn.setDisable(false);
                }
            });
        });
    }

    private void scrollToBottom() {
        if (messageListView != null && !messages.isEmpty()) {
            messageListView.scrollTo(messages.size() - 1);
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void shutdown() {
        executor.shutdown();
    }
}
