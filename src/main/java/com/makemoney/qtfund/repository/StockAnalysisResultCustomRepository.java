package com.makemoney.qtfund.repository;

import com.makemoney.qtfund.dto.StockSearchCriteria;
import com.makemoney.qtfund.entity.StockAnalysisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockAnalysisResultCustomRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<StockAnalysisResult> search(StockSearchCriteria criteria) {
        Query query = new Query();

        if (criteria.getExchangeId() != null) {
            query.addCriteria(Criteria.where("exchange_id").is(criteria.getExchangeId()));
        }

        if (criteria.getInstrumentId() != null) {
            query.addCriteria(Criteria.where("instrument_id").regex(criteria.getInstrumentId(), "i")); // Case-insensitive fuzzy search
        }

        if (criteria.getStockType() != null) {
            query.addCriteria(Criteria.where("stock_type").is(criteria.getStockType()));
        }

        if (criteria.getTargetDate() != null) {
            query.addCriteria(Criteria.where("target_date").is(criteria.getTargetDate()));
        }

        if (criteria.getMinRanking() != null && criteria.getMaxRanking() != null) {
            query.addCriteria(Criteria.where("ranking").gte(criteria.getMinRanking()).lte(criteria.getMaxRanking()));
        }

        if (criteria.getMinScore() != null && criteria.getMaxScore() != null) {
            query.addCriteria(Criteria.where("score").gte(criteria.getMinScore()).lte(criteria.getMaxScore()));
        }

        if (criteria.getMinAmount() != null) {
            query.addCriteria(Criteria.where("amount").gte(criteria.getMinAmount()));
        }
        
        if (criteria.getMaxAmount() != null) {
            query.addCriteria(Criteria.where("amount").lte(criteria.getMaxAmount()));
        }

        return mongoTemplate.find(query, StockAnalysisResult.class);
    }
}
