package com.gabchak.enums;

public enum ProductProperties {

    TITLE("data-test-id", "ProductTileDefault"),
    NAME("data-test-id", "ProductName"),
    BRAND("data-test-id", "ProductBrandName"),
    PRICE("class", "productPrices _normal_4348f"),
    COLOR("data-test-id", "VariantColor"),
    SIZE("class", "_column_c09af");

    private String propertyName;
    private String propertyValue;

    ProductProperties(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
}
