package com.makemoney.qtfund.enums;

public enum StockType {
    STOCK("股票/ETF"),
    INDEX("宽基指数");

    private final String description;

    StockType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
