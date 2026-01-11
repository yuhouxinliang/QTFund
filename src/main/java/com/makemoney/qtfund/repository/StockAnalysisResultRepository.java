package com.makemoney.qtfund.repository;

import com.makemoney.qtfund.entity.StockAnalysisResult;
import com.makemoney.qtfund.enums.StockType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 股票分析结果Repository接口
 */
@Repository
public interface StockAnalysisResultRepository extends MongoRepository<StockAnalysisResult, String> {

    /**
     * 根据类型和日期查询
     */
    List<StockAnalysisResult> findByStockTypeAndTargetDate(StockType stockType, Date targetDate);

    /**
     * 根据交易所代码和合约代码查询
     */
    List<StockAnalysisResult> findByExchangeIdAndInstrumentId(String exchangeId, String instrumentId);

    /**
     * 根据目标日期查询
     */
    List<StockAnalysisResult> findByTargetDate(Date targetDate);

    /**
     * 根据交易所代码、合约代码和目标日期查询
     */
    Optional<StockAnalysisResult> findByExchangeIdAndInstrumentIdAndTargetDate(
            String exchangeId, String instrumentId, Date targetDate);

    /**
     * 根据排名范围查询
     */
    List<StockAnalysisResult> findByRankingBetween(Integer minRanking, Integer maxRanking);

    /**
     * 根据得分范围查询
     */
    List<StockAnalysisResult> findByScoreBetween(Double minScore, Double maxScore);

    /**
     * 获取最新日期的一条记录
     */
    StockAnalysisResult findFirstByOrderByTargetDateDesc();

    /**
     * 根据类型获取最新日期的一条记录
     */
    StockAnalysisResult findFirstByStockTypeOrderByTargetDateDesc(StockType stockType);
}


