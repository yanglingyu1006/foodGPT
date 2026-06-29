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

/**
 * AI健康顾问控制器
 * 
 * 功能模块：
 * 1. 对话界面 - 显示用户和AI的消息列表
 * 2. 发送问题 - 向AI发送饮食健康问题
 * 3. 快捷问题 - 一键发送预设的常见问题
 * 4. 新建对话 - 清空消息列表，重新开始对话
 * 5. 异步处理 - 使用线程池异步调用AI接口，避免UI阻塞
 * 
 * @author FoodGPT
 */
public class AiAdvisorController {

    // ==================== FXML 组件注入 ====================
    
    @FXML
    private ListView<String> messageListView;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendBtn;
    @FXML
    private Button newChatBtn;

    private AiAdvisorService aiAdvisorService;
    private ObservableList<String> messages = FXCollections.observableArrayList();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /** 注入AI顾问服务 */
    public void setService(AiAdvisorService aiAdvisorService) {
        this.aiAdvisorService = aiAdvisorService;
    }

    /** FXML 初始化：设置消息列表，显示欢迎语 */
    @FXML
    private void initialize() {
        if (messageListView != null) {
            messageListView.setItems(messages);
        }
        
        messages.add("AI: 你好！我是你的AI饮食顾问，有什么关于饮食健康的问题可以问我。");
    }

    /** 新建对话：清空消息列表，显示欢迎语 */
    @FXML
    private void handleNewChat() {
        messages.clear();
        messages.add("AI: 你好！我是你的AI饮食顾问，有什么关于饮食健康的问题可以问我。");
        if (inputField != null) {
            inputField.clear();
        }
    }

    /** 发送问题：获取输入框内容，调用AI接口 */
    @FXML
    private void handleSend() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            showAlert("请输入问题");
            return;
        }

        sendRequest(message);
    }
    
    /** 快捷问题1：今日饮食建议 */
    @FXML
    private void handleQuickQuestion1() {
        sendRequest("请给我今日的饮食建议");
    }
    
    /** 快捷问题2：营养搭配建议 */
    @FXML
    private void handleQuickQuestion2() {
        sendRequest("如何搭配营养均衡的饮食？");
    }
    
    /** 快捷问题3：健康食谱推荐 */
    @FXML
    private void handleQuickQuestion3() {
        sendRequest("推荐一些健康的食谱");
    }
    
    /** 发送请求到AI：异步调用，显示"正在思考..."，收到响应后更新消息列表 */
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

    /** 滚动到消息列表底部 */
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

    /** 关闭线程池 */
    public void shutdown() {
        executor.shutdown();
    }
}
