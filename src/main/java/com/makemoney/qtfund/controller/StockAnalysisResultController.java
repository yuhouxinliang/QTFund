package com.makemoney.qtfund.controller;

import com.makemoney.qtfund.entity.StockAnalysisResult;
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
    public ResponseEntity<List<StockAnalysisResult>> getLatest() {
        Date latestDate = service.findLatestDate();
        if (latestDate == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(service.findByTargetDate(latestDate));
    }

    /**
     * 根据交易所代码和合约代码查询
     * GET /api/stock-analysis/search?exchangeId={exchangeId}&instrumentId={instrumentId}
     */
    @GetMapping("/search")
    public ResponseEntity<List<StockAnalysisResult>> search(
            @RequestParam(required = false) String exchangeId,
            @RequestParam(required = false) String instrumentId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date targetDate,
            @RequestParam(required = false) Integer minRanking,
            @RequestParam(required = false) Integer maxRanking,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore) {

        if (exchangeId != null && instrumentId != null && targetDate != null) {
            Optional<StockAnalysisResult> result = service.findByExchangeIdAndInstrumentIdAndTargetDate(
                    exchangeId, instrumentId, targetDate);
            return result.map(r -> ResponseEntity.ok(List.of(r)))
                    .orElse(ResponseEntity.ok(List.of()));
        }

        if (exchangeId != null && instrumentId != null) {
            return ResponseEntity.ok(service.findByExchangeIdAndInstrumentId(exchangeId, instrumentId));
        }

        if (targetDate != null) {
            return ResponseEntity.ok(service.findByTargetDate(targetDate));
        }

        if (minRanking != null && maxRanking != null) {
            return ResponseEntity.ok(service.findByRankingBetween(minRanking, maxRanking));
        }

        if (minScore != null && maxScore != null) {
            return ResponseEntity.ok(service.findByScoreBetween(minScore, maxScore));
        }

        return ResponseEntity.ok(service.findAll());
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


