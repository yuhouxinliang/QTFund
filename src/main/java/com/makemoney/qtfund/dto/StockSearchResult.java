package com.makemoney.qtfund.dto;

import com.makemoney.qtfund.entity.StockAnalysisResult;

public class StockSearchResult extends StockAnalysisResult {
    private Integer consecutiveRisingDays; // 连续上涨天数
    private Double cumulativeIncrease; // 累计涨幅 (百分比, 如 10.5 代表 10.5%)
    private Integer periodRankingChange; // 周期内排名上升名次 (正数代表进步，即排名数字变小)

    // Copy constructor
    public StockSearchResult(StockAnalysisResult result) {
        this.setId(result.getId());
        this.setExchangeId(result.getExchangeId());
        this.setInstrumentId(result.getInstrumentId());
        this.setInstrumentName(result.getInstrumentName());
        this.setStockType(result.getStockType());
        this.setClose(result.getClose());
        this.setAmount(result.getAmount());
        this.setScore(result.getScore());
        this.setRanking(result.getRanking());
        this.setScoreChange(result.getScoreChange());
        this.setRankingChange(result.getRankingChange());
        this.setTargetDate(result.getTargetDate());
    }

    public Integer getConsecutiveRisingDays() {
        return consecutiveRisingDays;
    }

    public void setConsecutiveRisingDays(Integer consecutiveRisingDays) {
        this.consecutiveRisingDays = consecutiveRisingDays;
    }

    public Double getCumulativeIncrease() {
        return cumulativeIncrease;
    }

    public void setCumulativeIncrease(Double cumulativeIncrease) {
        this.cumulativeIncrease = cumulativeIncrease;
    }

    public Integer getPeriodRankingChange() {
        return periodRankingChange;
    }

    public void setPeriodRankingChange(Integer periodRankingChange) {
        this.periodRankingChange = periodRankingChange;
    }
}
