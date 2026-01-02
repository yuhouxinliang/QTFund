package com.makemoney.qtfund.service;

import com.makemoney.qtfund.entity.StockAnalysisResult;
import com.makemoney.qtfund.repository.StockAnalysisResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 股票分析结果Service层
 */
@Service
public class StockAnalysisResultService {

    @Autowired
    private StockAnalysisResultRepository repository;

    /**
     * 创建/保存股票分析结果
     */
    public StockAnalysisResult save(StockAnalysisResult stockAnalysisResult) {
        return repository.save(stockAnalysisResult);
    }

    /**
     * 保存或更新股票分析结果
     * 如果存在相同的exchangeId、instrumentId、targetDate，则更新；否则插入
     */
    public StockAnalysisResult saveOrUpdate(StockAnalysisResult stockAnalysisResult) {
        // 检查是否存在相同的记录
        Optional<StockAnalysisResult> existing = repository.findByExchangeIdAndInstrumentIdAndTargetDate(
                stockAnalysisResult.getExchangeId(),
                stockAnalysisResult.getInstrumentId(),
                stockAnalysisResult.getTargetDate());

        if (existing.isPresent()) {
            // 如果存在，则更新
            StockAnalysisResult existingResult = existing.get();
            existingResult.setExchangeId(stockAnalysisResult.getExchangeId());
            existingResult.setInstrumentId(stockAnalysisResult.getInstrumentId());
            existingResult.setInstrumentName(stockAnalysisResult.getInstrumentName());
            existingResult.setClose(stockAnalysisResult.getClose());
            existingResult.setAmount(stockAnalysisResult.getAmount());
            existingResult.setScore(stockAnalysisResult.getScore());
            existingResult.setRanking(stockAnalysisResult.getRanking());
            existingResult.setScoreChange(stockAnalysisResult.getScoreChange());
            existingResult.setRankingChange(stockAnalysisResult.getRankingChange());
            existingResult.setTargetDate(stockAnalysisResult.getTargetDate());
            return repository.save(existingResult);
        } else {
            // 如果不存在，则插入
            return repository.save(stockAnalysisResult);
        }
    }

    /**
     * 根据ID查询
     */
    public Optional<StockAnalysisResult> findById(String id) {
        return repository.findById(id);
    }

    /**
     * 查询所有
     */
    public List<StockAnalysisResult> findAll() {
        return repository.findAll();
    }

    /**
     * 根据交易所代码和合约代码查询
     */
    public List<StockAnalysisResult> findByExchangeIdAndInstrumentId(String exchangeId, String instrumentId) {
        return repository.findByExchangeIdAndInstrumentId(exchangeId, instrumentId);
    }

    /**
     * 根据目标日期查询
     */
    public List<StockAnalysisResult> findByTargetDate(Date targetDate) {
        return repository.findByTargetDate(targetDate);
    }

    /**
     * 根据交易所代码、合约代码和目标日期查询
     */
    public Optional<StockAnalysisResult> findByExchangeIdAndInstrumentIdAndTargetDate(
            String exchangeId, String instrumentId, Date targetDate) {
        return repository.findByExchangeIdAndInstrumentIdAndTargetDate(exchangeId, instrumentId, targetDate);
    }

    /**
     * 根据排名范围查询
     */
    public List<StockAnalysisResult> findByRankingBetween(Integer minRanking, Integer maxRanking) {
        return repository.findByRankingBetween(minRanking, maxRanking);
    }

    /**
     * 根据得分范围查询
     */
    public List<StockAnalysisResult> findByScoreBetween(Double minScore, Double maxScore) {
        return repository.findByScoreBetween(minScore, maxScore);
    }

    /**
     * 更新股票分析结果
     */
    public StockAnalysisResult update(String id, StockAnalysisResult stockAnalysisResult) {
        Optional<StockAnalysisResult> existing = repository.findById(id);
        if (existing.isPresent()) {
            StockAnalysisResult existingResult = existing.get();
            existingResult.setExchangeId(stockAnalysisResult.getExchangeId());
            existingResult.setInstrumentId(stockAnalysisResult.getInstrumentId());
            existingResult.setInstrumentName(stockAnalysisResult.getInstrumentName());
            existingResult.setClose(stockAnalysisResult.getClose());
            existingResult.setAmount(stockAnalysisResult.getAmount());
            existingResult.setScore(stockAnalysisResult.getScore());
            existingResult.setRanking(stockAnalysisResult.getRanking());
            existingResult.setScoreChange(stockAnalysisResult.getScoreChange());
            existingResult.setRankingChange(stockAnalysisResult.getRankingChange());
            existingResult.setTargetDate(stockAnalysisResult.getTargetDate());
            return repository.save(existingResult);
        }
        return null;
    }

    /**
     * 根据ID删除
     */
    public boolean deleteById(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 删除所有
     */
    public void deleteAll() {
        repository.deleteAll();
    }

    /**
     * 获取最新日期
     */
    public Date findLatestDate() {
        StockAnalysisResult result = repository.findFirstByOrderByTargetDateDesc();
        return result != null ? result.getTargetDate() : null;
    }
}


