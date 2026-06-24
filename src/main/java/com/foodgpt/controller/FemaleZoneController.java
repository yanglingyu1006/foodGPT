package com.foodgpt.controller;

import com.foodgpt.entity.CycleRecord;
import com.foodgpt.entity.HealthGoal;
import com.foodgpt.enums.CyclePhase;
import com.foodgpt.enums.HealthGoalType;
import com.foodgpt.service.CycleService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FemaleZoneController {

    @FXML
    private Label cyclePhaseLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Spinner<Integer> cycleLengthSpinner;
    @FXML
    private Button saveCycleBtn;
    @FXML
    private ToggleGroup goalToggleGroup;
    @FXML
    private RadioButton weightLossRadio;
    @FXML
    private RadioButton muscleGainRadio;
    @FXML
    private RadioButton maintainRadio;

    private CycleService cycleService;

    public void setService(CycleService cycleService) {
        this.cycleService = cycleService;
        loadData();
    }

    private void loadData() {
        String phase = cycleService.getCurrentPhase();
        cyclePhaseLabel.setText("当前阶段: " + CyclePhase.valueOf(phase).getLabel());

        CycleRecord current = cycleService.getCurrentCycle();
        if (current != null) {
            startDatePicker.setValue(current.getStartDate());
            cycleLengthSpinner.getValueFactory().setValue(current.getCycleLength());
        }
    }

    @FXML
    private void initialize() {
        cycleLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 45, 28));
        goalToggleGroup = new ToggleGroup();
        weightLossRadio.setToggleGroup(goalToggleGroup);
        muscleGainRadio.setToggleGroup(goalToggleGroup);
        maintainRadio.setToggleGroup(goalToggleGroup);
        maintainRadio.setSelected(true);
    }

    @FXML
    private void handleSaveCycle() {
        CycleRecord record = new CycleRecord();
        record.setUserId(1);
        record.setStartDate(startDatePicker.getValue());
        record.setCycleLength(cycleLengthSpinner.getValue());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        cycleService.saveCycleRecord(record);
        loadData();
        showAlert("保存成功");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
