package com.foodgpt.controller;

import com.foodgpt.entity.WeightRecord;
import com.foodgpt.service.WeightTrackService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeightTrackController {

    @FXML
    private LineChart<String, Number> weightChart;
    @FXML
    private Spinner<Double> weightInput;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button addBtn;
    @FXML
    private ListView<WeightRecord> recordListView;

    private WeightTrackService weightTrackService;
    private MainLayoutController mainLayoutController;

    public void setService(WeightTrackService weightTrackService) {
        this.weightTrackService = weightTrackService;
    }

    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    @FXML
    private void initialize() {
        weightInput.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(30, 200, 55, 0.1));
        datePicker.setValue(LocalDate.now());
        loadData();
    }

    private void loadData() {
        if (weightTrackService != null) {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            List<WeightRecord> records = weightTrackService.getWeightRecords(startDate, endDate);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("体重变化");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            for (WeightRecord record : records) {
                series.getData().add(new XYChart.Data<>(record.getRecordDate().format(formatter), record.getWeight()));
            }

            if (weightChart != null) {
                weightChart.getData().clear();
                weightChart.getData().add(series);
            }

            List<WeightRecord> recent = weightTrackService.getRecentRecords(10);
            ObservableList<WeightRecord> items = FXCollections.observableArrayList(recent);
            if (recordListView != null) {
                recordListView.setItems(items);
                recordListView.setCellFactory(param -> new ListCell<WeightRecord>() {
                    @Override
                    protected void updateItem(WeightRecord item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("%s - %.1f kg", item.getRecordDate(), item.getWeight()));
                        }
                    }
                });
            }
        }
    }

    @FXML
    private void handleAdd() {
        WeightRecord record = new WeightRecord();
        record.setUserId(1);
        record.setWeight(weightInput.getValue());
        record.setRecordDate(datePicker.getValue());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        weightTrackService.saveWeightRecord(record);
        loadData();
        System.out.println("[WeightTrack] 体重记录已添加，刷新首页体重趋势");
        if (mainLayoutController != null) {
            mainLayoutController.refreshDashboard();
        }
        showAlert("记录成功");
    }

    @FXML
    private void handleDelete() {
        WeightRecord selected = recordListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            weightTrackService.deleteWeightRecord(selected.getId());
            loadData();
            System.out.println("[WeightTrack] 体重记录已删除，刷新首页体重趋势");
            if (mainLayoutController != null) {
                mainLayoutController.refreshDashboard();
            }
            showAlert("删除成功");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
