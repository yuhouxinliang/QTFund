package com.makemoney.qtfund.dto;

import com.makemoney.qtfund.enums.StockType;
import java.util.Date;

public class StockSearchCriteria {
    private String exchangeId;
    private String instrumentId;
    private StockType stockType;
    private Date targetDate;
    private Integer minRanking;
    private Integer maxRanking;
    private Double minScore;
    private Double maxScore;
    private Double minAmount;
    private Double maxAmount;

    // Getters and Setters
    public String getExchangeId() { return exchangeId; }
    public void setExchangeId(String exchangeId) { this.exchangeId = exchangeId; }

    public String getInstrumentId() { return instrumentId; }
    public void setInstrumentId(String instrumentId) { this.instrumentId = instrumentId; }

    public StockType getStockType() { return stockType; }
    public void setStockType(StockType stockType) { this.stockType = stockType; }

    public Date getTargetDate() { return targetDate; }
    public void setTargetDate(Date targetDate) { this.targetDate = targetDate; }

    public Integer getMinRanking() { return minRanking; }
    public void setMinRanking(Integer minRanking) { this.minRanking = minRanking; }

    public Integer getMaxRanking() { return maxRanking; }
    public void setMaxRanking(Integer maxRanking) { this.maxRanking = maxRanking; }

    public Double getMinScore() { return minScore; }
    public void setMinScore(Double minScore) { this.minScore = minScore; }

    public Double getMaxScore() { return maxScore; }
    public void setMaxScore(Double maxScore) { this.maxScore = maxScore; }

    public Double getMinAmount() { return minAmount; }
    public void setMinAmount(Double minAmount) { this.minAmount = minAmount; }

    public Double getMaxAmount() { return maxAmount; }
    public void setMaxAmount(Double maxAmount) { this.maxAmount = maxAmount; }
}
