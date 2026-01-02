package com.makemoney.qtfund;

import com.makemoney.qtfund.entity.StockAnalysisResult;
import com.makemoney.qtfund.service.StockAnalysisResultService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QtFundApplicationTests {

    @Autowired
    private StockAnalysisResultService service;

    private StockAnalysisResult testData1;
    private StockAnalysisResult testData2;
    private StockAnalysisResult testData3;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        service.deleteAll();

        // 创建测试数据
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 15, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date1 = cal.getTime();

        cal.set(2024, Calendar.JANUARY, 16, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date2 = cal.getTime();

        cal.set(2024, Calendar.JANUARY, 17, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date3 = cal.getTime();

        testData1 = new StockAnalysisResult();
        testData1.setExchangeId("SZ");
        testData1.setInstrumentId("159206");
        testData1.setInstrumentName("卫星ETF");
        testData1.setClose(1.25);
        testData1.setAmount(1000000.0);
        testData1.setScore(85.5);
        testData1.setRanking(10);
        testData1.setScoreChange(2.5);
        testData1.setRankingChange(-2);
        testData1.setTargetDate(date1);

        testData2 = new StockAnalysisResult();
        testData2.setExchangeId("SZ");
        testData2.setInstrumentId("159206");
        testData2.setInstrumentName("卫星ETF");
        testData2.setClose(1.30);
        testData2.setAmount(1200000.0);
        testData2.setScore(88.0);
        testData2.setRanking(8);
        testData2.setScoreChange(2.5);
        testData2.setRankingChange(-2);
        testData2.setTargetDate(date2);

        testData3 = new StockAnalysisResult();
        testData3.setExchangeId("SH");
        testData3.setInstrumentId("510300");
        testData3.setInstrumentName("沪深300ETF");
        testData3.setClose(4.50);
        testData3.setAmount(5000000.0);
        testData3.setScore(92.0);
        testData3.setRanking(5);
        testData3.setScoreChange(1.5);
        testData3.setRankingChange(0);
        testData3.setTargetDate(date3);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据 - 已注释，如需保留数据查看，请保持注释状态
        // service.deleteAll();
    }

    @Test
    void contextLoads() {
        assertNotNull(service);
    }

    /**
     * 测试创建数据
     */
    @Test
    void testCreate() {
        StockAnalysisResult saved = service.save(testData1);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("SZ", saved.getExchangeId());
        assertEquals("159206", saved.getInstrumentId());
        assertEquals("卫星ETF", saved.getInstrumentName());
        assertEquals(1.25, saved.getClose());
        assertEquals(1000000.0, saved.getAmount());
        assertEquals(85.5, saved.getScore());
        assertEquals(10, saved.getRanking());
    }

    /**
     * 测试根据ID查询
     */
    @Test
    void testFindById() {
        StockAnalysisResult saved = service.save(testData1);
        String id = saved.getId();

        Optional<StockAnalysisResult> found = service.findById(id);

        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
        assertEquals("SZ", found.get().getExchangeId());
        assertEquals("159206", found.get().getInstrumentId());
    }

    /**
     * 测试查询所有数据
     */
    @Test
    void testFindAll() {
        service.save(testData1);
        service.save(testData2);
        service.save(testData3);

        List<StockAnalysisResult> results = service.findAll();

        assertEquals(3, results.size());
    }

    /**
     * 测试根据交易所代码和合约代码查询
     */
    @Test
    void testFindByExchangeIdAndInstrumentId() {
        service.save(testData1);
        service.save(testData2);
        service.save(testData3);

        List<StockAnalysisResult> results = service.findByExchangeIdAndInstrumentId("SZ", "159206");

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> "SZ".equals(r.getExchangeId()) && "159206".equals(r.getInstrumentId())));
    }

    /**
     * 测试根据目标日期查询
     */
    @Test
    void testFindByTargetDate() {
        StockAnalysisResult saved = service.save(testData1);
        Date targetDate = saved.getTargetDate();

        List<StockAnalysisResult> results = service.findByTargetDate(targetDate);

        assertEquals(1, results.size());
        assertEquals(targetDate, results.get(0).getTargetDate());
    }

    /**
     * 测试根据交易所代码、合约代码和目标日期查询
     */
    @Test
    void testFindByExchangeIdAndInstrumentIdAndTargetDate() {
        StockAnalysisResult saved = service.save(testData1);
        Date targetDate = saved.getTargetDate();

        Optional<StockAnalysisResult> result = service.findByExchangeIdAndInstrumentIdAndTargetDate(
                "SZ", "159206", targetDate);

        assertTrue(result.isPresent());
        assertEquals("SZ", result.get().getExchangeId());
        assertEquals("159206", result.get().getInstrumentId());
        assertEquals(targetDate, result.get().getTargetDate());
    }

    /**
     * 测试根据排名范围查询
     */
    @Test
    void testFindByRankingBetween() {
        service.save(testData1); // ranking = 10
        service.save(testData2); // ranking = 8
        service.save(testData3); // ranking = 5

        List<StockAnalysisResult> results = service.findByRankingBetween(5, 9);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r.getRanking() >= 5 && r.getRanking() <= 9));
    }

    /**
     * 测试根据得分范围查询
     */
    @Test
    void testFindByScoreBetween() {
        service.save(testData1); // score = 85.5
        service.save(testData2); // score = 88.0
        service.save(testData3); // score = 92.0

        List<StockAnalysisResult> results = service.findByScoreBetween(86.0, 90.0);

        assertEquals(1, results.size());
        assertEquals(88.0, results.get(0).getScore());
    }

    /**
     * 测试更新数据
     */
    @Test
    void testUpdate() {
        StockAnalysisResult saved = service.save(testData1);
        String id = saved.getId();

        // 创建更新数据
        StockAnalysisResult updateData = new StockAnalysisResult();
        updateData.setExchangeId("SH");
        updateData.setInstrumentId("510300");
        updateData.setInstrumentName("沪深300ETF");
        updateData.setClose(4.60);
        updateData.setAmount(6000000.0);
        updateData.setScore(95.0);
        updateData.setRanking(3);
        updateData.setScoreChange(3.0);
        updateData.setRankingChange(-2);
        updateData.setTargetDate(new Date());

        StockAnalysisResult updated = service.update(id, updateData);

        assertNotNull(updated);
        assertEquals(id, updated.getId());
        assertEquals("SH", updated.getExchangeId());
        assertEquals("510300", updated.getInstrumentId());
        assertEquals("沪深300ETF", updated.getInstrumentName());
        assertEquals(4.60, updated.getClose());
        assertEquals(95.0, updated.getScore());
        assertEquals(3, updated.getRanking());
    }

    /**
     * 测试更新不存在的数据
     */
    @Test
    void testUpdateNonExistent() {
        StockAnalysisResult updateData = new StockAnalysisResult();
        updateData.setExchangeId("SH");
        updateData.setInstrumentId("510300");

        StockAnalysisResult updated = service.update("non-existent-id", updateData);

        assertNull(updated);
    }

    /**
     * 测试根据ID删除
     */
    @Test
    void testDeleteById() {
        StockAnalysisResult saved = service.save(testData1);
        String id = saved.getId();

        boolean deleted = service.deleteById(id);

        assertTrue(deleted);
        Optional<StockAnalysisResult> found = service.findById(id);
        assertFalse(found.isPresent());
    }

    /**
     * 测试删除不存在的数据
     */
    @Test
    void testDeleteNonExistent() {
        boolean deleted = service.deleteById("non-existent-id");

        assertFalse(deleted);
    }

    /**
     * 测试删除所有数据
     */
    @Test
    void testDeleteAll() {
        service.save(testData1);
        service.save(testData2);
        service.save(testData3);

        service.deleteAll();

        List<StockAnalysisResult> results = service.findAll();
        assertEquals(0, results.size());
    }

    /**
     * 测试完整的CRUD流程
     */
    @Test
    void testFullCrudWorkflow() {
        // Create
        StockAnalysisResult saved = service.save(testData1);
        assertNotNull(saved.getId());

        // Read
        Optional<StockAnalysisResult> found = service.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("卫星ETF", found.get().getInstrumentName());

        // Update
        found.get().setScore(90.0);
        found.get().setRanking(5);
        StockAnalysisResult updated = service.update(saved.getId(), found.get());
        assertEquals(90.0, updated.getScore());
        assertEquals(5, updated.getRanking());

        // Delete
        boolean deleted = service.deleteById(saved.getId());
        assertTrue(deleted);
        Optional<StockAnalysisResult> afterDelete = service.findById(saved.getId());
        assertFalse(afterDelete.isPresent());
    }

    /**
     * 测试数据写入并保留数据（用于在MongoDB Compass中查看）
     * 注意：此测试不会清理数据，数据会保留在数据库中
     */
    @Test
    void testSaveDataForViewing() {
        // 创建测试数据
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 20, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date testDate = cal.getTime();

        StockAnalysisResult testData = new StockAnalysisResult();
        testData.setExchangeId("SZ");
        testData.setInstrumentId("159206");
        testData.setInstrumentName("卫星ETF");
        testData.setClose(1.35);
        testData.setAmount(1500000.0);
        testData.setScore(90.5);
        testData.setRanking(7);
        testData.setScoreChange(3.0);
        testData.setRankingChange(-3);
        testData.setTargetDate(testDate);

        // 保存数据
        StockAnalysisResult saved = service.save(testData);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        System.out.println("数据已保存，ID: " + saved.getId());
        System.out.println("请在MongoDB Compass中查看 qtfund.stock_analysis_result 集合");

        // 验证数据已保存
        Optional<StockAnalysisResult> found = service.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("SZ", found.get().getExchangeId());
        assertEquals("159206", found.get().getInstrumentId());
    }
}
