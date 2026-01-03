package com.makemoney.qtfund;

import com.makemoney.qtfund.enums.StockType;
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
import java.util.Random;

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
     * 批量插入模拟数据 (10只股票 + 10只指数 x 100天)
     */
    @Test
    void insertMockData() {
        // 清理旧数据
        service.deleteAll();

        // 10只股票
        String[][] stocks = {
            {"SH", "510300", "沪深300ETF", "4.0"},
            {"SH", "588000", "科创50ETF", "0.9"},
            {"SZ", "159915", "创业板ETF", "2.1"},
            {"SH", "512660", "军工ETF", "1.1"},
            {"SH", "512480", "半导体ETF", "0.8"},
            {"SZ", "159920", "恒生ETF", "0.7"},
            {"SH", "512880", "证券ETF", "0.9"},
            {"SH", "515030", "新能源车ETF", "1.5"},
            {"SZ", "159605", "中概互联ETF", "0.8"},
            {"SZ", "159995", "芯片ETF", "1.0"}
        };

        // 10只指数
        String[][] indexes = {
            {"SH", "000001", "上证指数", "3000.0"},
            {"SZ", "399001", "深证成指", "9500.0"},
            {"SZ", "399006", "创业板指", "1800.0"},
            {"SH", "000300", "沪深300", "3500.0"},
            {"SH", "000688", "科创50", "850.0"},
            {"SH", "000905", "中证500", "5500.0"},
            {"SH", "000852", "中证1000", "6000.0"},
            {"SH", "000016", "上证50", "2400.0"},
            {"SZ", "399005", "中小板指", "6000.0"},
            {"HK", "HSI", "恒生指数", "17000.0"}
        };

        Random random = new Random();
        Calendar calendar = Calendar.getInstance();
        // 清除时分秒，确保日期精确匹配
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        // 从100天前开始
        calendar.add(Calendar.DAY_OF_YEAR, -100);
        Date startDate = calendar.getTime();

        // 插入股票数据
        insertDataBatch(stocks, startDate, StockType.STOCK, random);
        
        // 插入指数数据
        insertDataBatch(indexes, startDate, StockType.INDEX, random);
        
        System.out.println("成功插入模拟数据：10只股票 + 10只指数！");
    }

    private void insertDataBatch(String[][] items, Date startDate, StockType type, Random random) {
        for (String[] item : items) {
            String exchangeId = item[0];
            String instrumentId = item[1];
            String name = item[2];
            double basePrice = Double.parseDouble(item[3]);
            double currentPrice = basePrice;
            
            // 初始排名和分数
            int currentRank = random.nextInt(100) + 1;
            double currentScore = 50 + random.nextDouble() * 50;

            for (int i = 0; i < 100; i++) {
                Calendar currentCal = Calendar.getInstance();
                currentCal.setTime(startDate);
                currentCal.add(Calendar.DAY_OF_YEAR, i);
                
                // 跳过周末
                int dayOfWeek = currentCal.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    continue;
                }

                StockAnalysisResult data = new StockAnalysisResult();
                data.setStockType(type);
                data.setExchangeId(exchangeId);
                data.setInstrumentId(instrumentId);
                data.setInstrumentName(name);
                
                // 模拟价格波动 (-2% 到 +2%)
                double changePercent = (random.nextDouble() - 0.5) * 0.04;
                currentPrice = currentPrice * (1 + changePercent);
                data.setClose(Math.round(currentPrice * 1000.0) / 1000.0);
                
                // 模拟成交额
                double amount = type == StockType.STOCK ? 
                    (50000000 + random.nextDouble() * 450000000) : // 股票: 5千万-5亿
                    (1000000000 + random.nextDouble() * 5000000000L); // 指数: 10亿-60亿
                data.setAmount(amount);
                
                // 模拟分数和排名变化
                double scoreChange = (random.nextDouble() - 0.5) * 5;
                currentScore = Math.max(0, Math.min(100, currentScore + scoreChange));
                
                int rankChange = (int)((random.nextDouble() - 0.5) * 10);
                currentRank = Math.max(1, Math.min(500, currentRank + rankChange));

                data.setScore(Math.round(currentScore * 10.0) / 10.0);
                data.setRanking(currentRank);
                data.setScoreChange(Math.round(scoreChange * 10.0) / 10.0);
                data.setRankingChange(-rankChange);
                data.setTargetDate(currentCal.getTime());

                service.save(data);
            }
        }
    }
}
