package com.makemoney.qtfund.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 股票分析结果实体类
 */
@Document(collection = "stock_analysis_result")
public class StockAnalysisResult {

    @Id
    private String id;

    @Field("exchange_id")
    private String exchangeId;

    @Field("instrument_id")
    private String instrumentId;

    @Field("instrument_name")
    private String instrumentName;

    @Field("close")
    private Double close;

    @Field("amount")
    private Double amount;

    @Field("score")
    private Double score;

    @Field("ranking")
    private Integer ranking;

    @Field("score_change")
    private Double scoreChange;

    @Field("ranking_change")
    private Integer rankingChange;

    @Field("target_date")
    private Date targetDate;

    // 无参构造函数
    public StockAnalysisResult() {
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Double getScoreChange() {
        return scoreChange;
    }

    public void setScoreChange(Double scoreChange) {
        this.scoreChange = scoreChange;
    }

    public Integer getRankingChange() {
        return rankingChange;
    }

    public void setRankingChange(Integer rankingChange) {
        this.rankingChange = rankingChange;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    @Override
    public String toString() {
        return "StockAnalysisResult{" +
                "id='" + id + '\'' +
                ", exchangeId='" + exchangeId + '\'' +
                ", instrumentId='" + instrumentId + '\'' +
                ", instrumentName='" + instrumentName + '\'' +
                ", close=" + close +
                ", amount=" + amount +
                ", score=" + score +
                ", ranking=" + ranking +
                ", scoreChange=" + scoreChange +
                ", rankingChange=" + rankingChange +
                ", targetDate=" + targetDate +
                '}';
    }
}


