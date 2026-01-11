package com.makemoney.qtfund.controller;

import com.makemoney.qtfund.entity.StockAnalysisResult;
import com.makemoney.qtfund.enums.StockType;
import com.makemoney.qtfund.service.StockAnalysisResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 股票分析结果Controller层
 * 提供RESTful API接口
 */
@RestController
@RequestMapping("/api/stock-analysis")
public class StockAnalysisResultController {

    @Autowired
    private StockAnalysisResultService service;

    /**
     * 创建股票分析结果
     * 如果已存在相同的exchangeId、instrumentId、targetDate，则更新；否则插入
     * POST /api/stock-analysis
     */
    @PostMapping
    public ResponseEntity<StockAnalysisResult> create(@RequestBody StockAnalysisResult stockAnalysisResult) {
        // 检查是否存在相同的记录
        Optional<StockAnalysisResult> existing = service.findByExchangeIdAndInstrumentIdAndTargetDate(
                stockAnalysisResult.getExchangeId(),
                stockAnalysisResult.getInstrumentId(),
                stockAnalysisResult.getTargetDate());

        // 使用saveOrUpdate方法，如果存在则更新，不存在则插入
        StockAnalysisResult result = service.saveOrUpdate(stockAnalysisResult);
        
        // 根据是否存在决定返回的状态码
        HttpStatus status = existing.isPresent() ? HttpStatus.OK : HttpStatus.CREATED;
        
        return ResponseEntity.status(status).body(result);
    }

    /**
     * 根据ID查询
     * GET /api/stock-analysis/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockAnalysisResult> getById(@PathVariable String id) {
        Optional<StockAnalysisResult> result = service.findById(id);
        return result.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 查询所有
     * GET /api/stock-analysis
     */
    @GetMapping
    public ResponseEntity<List<StockAnalysisResult>> getAll() {
        List<StockAnalysisResult> results = service.findAll();
        return ResponseEntity.ok(results);
    }

    /**
     * 获取最新日期的所有数据
     * GET /api/stock-analysis/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<List<StockAnalysisResult>> getLatest(
            @RequestParam(required = false) StockType stockType) {
        Date latestDate = service.findLatestDate(stockType);
        if (latestDate == null) {
            return ResponseEntity.ok(List.of());
        }
        
        if (stockType != null) {
            return ResponseEntity.ok(service.findByStockTypeAndTargetDate(stockType, latestDate));
        }
        return ResponseEntity.ok(service.findByTargetDate(latestDate));
    }

    /**
     * 获取股票/指数详情（包含最新数据、历史走势及分析指标）
     * GET /api/stock-analysis/detail/{exchangeId}/{instrumentId}
     */
    @GetMapping("/detail/{exchangeId}/{instrumentId}")
    public ResponseEntity<com.makemoney.qtfund.dto.StockDetailResponse> getDetail(
            @PathVariable String exchangeId,
            @PathVariable String instrumentId) {
        com.makemoney.qtfund.dto.StockDetailResponse detail = service.getStockDetail(exchangeId, instrumentId);
        if (detail != null) {
            return ResponseEntity.ok(detail);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据交易所代码和合约代码查询
     * GET /api/stock-analysis/search
     */
    @GetMapping("/search")
    public ResponseEntity<List<StockAnalysisResult>> search(
            @RequestParam(required = false) String exchangeId,
            @RequestParam(required = false) String instrumentId,
            @RequestParam(required = false) StockType stockType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date targetDate,
            @RequestParam(required = false) Integer minRanking,
            @RequestParam(required = false) Integer maxRanking,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore,
            @RequestParam(required = false) Double minAmount) {

        com.makemoney.qtfund.dto.StockSearchCriteria criteria = new com.makemoney.qtfund.dto.StockSearchCriteria();
        criteria.setExchangeId(exchangeId);
        criteria.setInstrumentId(instrumentId);
        criteria.setStockType(stockType);
        criteria.setTargetDate(targetDate);
        criteria.setMinRanking(minRanking);
        criteria.setMaxRanking(maxRanking);
        criteria.setMinScore(minScore);
        criteria.setMaxScore(maxScore);
        // 前端传来的单位是万，后端存储的是原始值，需要转换
        if (minAmount != null) {
            criteria.setMinAmount(minAmount * 10000);
        }

        List<StockAnalysisResult> results = service.search(criteria);
        return ResponseEntity.ok(results);
    }

    /**
     * 更新股票分析结果
     * PUT /api/stock-analysis/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockAnalysisResult> update(
            @PathVariable String id,
            @RequestBody StockAnalysisResult stockAnalysisResult) {
        StockAnalysisResult updated = service.update(id, stockAnalysisResult);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 根据ID删除
     * DELETE /api/stock-analysis/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        boolean deleted = service.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 删除所有
     * DELETE /api/stock-analysis
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }
}


