package com.makemoney.qtfund.dto;

import com.makemoney.qtfund.entity.StockAnalysisResult;
import java.util.List;

public class StockDetailResponse {
    private StockAnalysisResult latest;
    private List<StockAnalysisResult> history;
    private Double averageScore; // Example of backend calculation
    private String trend; // Example: "UP", "DOWN", "STABLE"

    public StockDetailResponse(StockAnalysisResult latest, List<StockAnalysisResult> history) {
        this.latest = latest;
        this.history = history;
    }

    public StockAnalysisResult getLatest() {
        return latest;
    }

    public void setLatest(StockAnalysisResult latest) {
        this.latest = latest;
    }

    public List<StockAnalysisResult> getHistory() {
        return history;
    }

    public void setHistory(List<StockAnalysisResult> history) {
        this.history = history;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}
