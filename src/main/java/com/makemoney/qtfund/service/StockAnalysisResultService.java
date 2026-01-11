package com.makemoney.qtfund.service;

import com.makemoney.qtfund.entity.StockAnalysisResult;
import com.makemoney.qtfund.enums.StockType;
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

    @Autowired
    private com.makemoney.qtfund.repository.StockAnalysisResultCustomRepository customRepository;

    /**
     * 根据条件动态搜索，支持时间范围分析
     */
    public List<com.makemoney.qtfund.dto.StockSearchResult> searchWithAnalysis(com.makemoney.qtfund.dto.StockSearchCriteria criteria) {
        // 1. 先进行基础过滤，获取目标日期的结果列表
        List<StockAnalysisResult> baseResults = customRepository.search(criteria);
        
        // 如果没有时间范围要求，或者基础结果为空，直接转换返回
        if (criteria.getTimeRange() == null || criteria.getTimeRange() <= 0 || baseResults.isEmpty()) {
            return baseResults.stream().map(com.makemoney.qtfund.dto.StockSearchResult::new).toList();
        }

        // 2. 准备时间范围分析
        Date endDate = criteria.getTargetDate();
        if (endDate == null) {
            // 如果没传 targetDate，则取 baseResults 中最新的日期（通常 search 默认会查最新，或者前端传了）
            // 这里为了保险，重新查一次最新日期
            endDate = findLatestDate(criteria.getStockType());
        }
        if (endDate == null) return baseResults.stream().map(com.makemoney.qtfund.dto.StockSearchResult::new).toList();

        // 计算开始日期
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(java.util.Calendar.DAY_OF_YEAR, -criteria.getTimeRange());
        Date startDate = cal.getTime();

        // 提取所有涉及的 instrumentIds
        List<String> instrumentIds = baseResults.stream()
                .map(StockAnalysisResult::getInstrumentId)
                .distinct() // 确保 ID 唯一
                .toList();

        if (instrumentIds.isEmpty()) {
             return baseResults.stream().map(com.makemoney.qtfund.dto.StockSearchResult::new).toList();
        }

        // 3. 批量查询历史数据 (Start ~ End)
        List<StockAnalysisResult> historyData = repository.findByInstrumentIdInAndTargetDateBetween(instrumentIds, startDate, endDate);

        // 4. 在内存中分组并计算指标
        java.util.Map<String, List<StockAnalysisResult>> groupedHistory = historyData.stream()
                .collect(java.util.stream.Collectors.groupingBy(StockAnalysisResult::getInstrumentId));

        Date finalEndDate = endDate; // for lambda
        return baseResults.stream().map(current -> {
            com.makemoney.qtfund.dto.StockSearchResult dto = new com.makemoney.qtfund.dto.StockSearchResult(current);
            List<StockAnalysisResult> history = groupedHistory.get(current.getInstrumentId());

            if (history != null && !history.isEmpty()) {
                // 按日期升序排序
                history.sort((a, b) -> a.getTargetDate().compareTo(b.getTargetDate()));

                // A. 累计涨幅 (CurrentClose - StartClose) / StartClose
                // 注意：StartClose应该是区间内最早的一天的数据，但不一定是 startDate 当天
                StockAnalysisResult startPoint = history.get(0); 
                
                // 如果历史数据里包含了 current 这一天，用 current 的 close 计算更准确（虽然理论上 current 就是最新的）
                // 累计涨幅 = (Current.Close - Start.Close) / Start.Close
                if (startPoint.getClose() != null && startPoint.getClose() > 0 && current.getClose() != null) {
                    double increase = (current.getClose() - startPoint.getClose()) / startPoint.getClose();
                    dto.setCumulativeIncrease(Math.round(increase * 10000.0) / 100.0); // 转百分比并保留2位小数
                }

                // B. 周期内排名上升名次 (计算周期内排名变化的和)
                int sumRankingChange = 0;
                for (StockAnalysisResult record : history) {
                    if (record.getRankingChange() != null) {
                        sumRankingChange += record.getRankingChange();
                    }
                }
                dto.setPeriodRankingChange(sumRankingChange);

                // C. 连续上涨天数 (倒序遍历, 按照 rankingChange 统计)
                // 逻辑修正：排名变化(rankingChange)大于1算涨，等于0持平，小于0降低
                // 注意：用户口语"大于1"通常在排名语境下可能指"有提升"(>0)，或者严格指">1"。
                // 考虑到"0是持平，<0是降低"，如果严格>1，则1这个最常见的提升被漏掉。
                // 结合常见逻辑，这里采用 rankingChange > 0 (即有提升) 作为"涨"的判断。
                // 如果用户明确需要严格>1，可改为 change > 1。
                int consecutive = 0;
                int lastIdx = history.size() - 1;
                
                for (int i = lastIdx; i >= 0; i--) {
                    StockAnalysisResult day = history.get(i);
                    // 跳过 current 本身（如果 history 里包含 current）
                    // 比较简单的方法：直接比较 close
                    if (day.getTargetDate().compareTo(current.getTargetDate()) > 0) {
                        continue; // 跳过未来日期(防御性)
                    }
                    
                    Integer change = day.getRankingChange();
                    // 这里判断change > 0，即排名提升
                    if (change != null && change > 0) {
                        consecutive++;
                    } else {
                        // 遇到持平(0)或下降(<0)或null，连续上涨中断
                        break;
                    }
                }
                
                dto.setConsecutiveRisingDays(consecutive);
            }
            return dto;
        }).toList();
    }

    /**
     * 根据条件动态搜索 (旧方法，保留兼容，但 Controller 应切换到 searchWithAnalysis)
     */
    public List<StockAnalysisResult> search(com.makemoney.qtfund.dto.StockSearchCriteria criteria) {
        return customRepository.search(criteria);
    }

    public List<StockAnalysisResult> findByStockTypeAndTargetDate(StockType stockType, Date targetDate) {
        return repository.findByStockTypeAndTargetDate(stockType, targetDate);
    }

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
            existingResult.setStockType(stockAnalysisResult.getStockType());
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
     * 获取股票详情，包含最新数据、历史数据及计算指标
     */
    public com.makemoney.qtfund.dto.StockDetailResponse getStockDetail(String exchangeId, String instrumentId) {
        List<StockAnalysisResult> history = repository.findByExchangeIdAndInstrumentId(exchangeId, instrumentId);
        
        if (history.isEmpty()) {
            return null;
        }

        // 按日期降序排序
        history.sort((a, b) -> {
            if (a.getTargetDate() == null && b.getTargetDate() == null) return 0;
            if (a.getTargetDate() == null) return 1;
            if (b.getTargetDate() == null) return -1;
            return b.getTargetDate().compareTo(a.getTargetDate());
        });
        
        StockAnalysisResult latest = history.get(0);
        
        com.makemoney.qtfund.dto.StockDetailResponse response = new com.makemoney.qtfund.dto.StockDetailResponse(latest, history);
        
        // 计算平均分 (所有历史数据的平均分)
        double avgScore = history.stream()
                .mapToDouble(StockAnalysisResult::getScore)
                .average()
                .orElse(0.0);
        // 保留两位小数
        response.setAverageScore(Math.round(avgScore * 100.0) / 100.0);
        
        // 计算趋势
        if (latest.getScoreChange() != null) {
            if (latest.getScoreChange() > 0) {
                response.setTrend("RISING");
            } else if (latest.getScoreChange() < 0) {
                response.setTrend("FALLING");
            } else {
                response.setTrend("STABLE");
            }
        } else {
            response.setTrend("UNKNOWN");
        }
        
        return response;
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
            existingResult.setStockType(stockAnalysisResult.getStockType());
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
    public Date findLatestDate(StockType stockType) {
        StockAnalysisResult result;
        if (stockType != null) {
            result = repository.findFirstByStockTypeOrderByTargetDateDesc(stockType);
        } else {
            result = repository.findFirstByOrderByTargetDateDesc();
        }
        return result != null ? result.getTargetDate() : null;
    }
}


