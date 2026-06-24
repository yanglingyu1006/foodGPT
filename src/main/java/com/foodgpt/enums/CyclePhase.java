package com.foodgpt.enums;

public enum CyclePhase {
    MENSTRUATION("月经期"),
    FOLLICULAR("卵泡期"),
    OVULATION("排卵期"),
    LUTEAL("黄体期");

    private final String label;

    CyclePhase(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CyclePhase fromLabel(String label) {
        for (CyclePhase phase : values()) {
            if (phase.label.equals(label)) {
                return phase;
            }
        }
        return FOLLICULAR;
    }
}
