package com.gabchak.enums;

public enum ProductProperties {

    ARTICLE_ID("class", "articleNumber"),
    BRAND("data-test-id", "ProductBrandName"),
    COLOR_NAME("data-test-id", "VariantColor"),
    COLOR_URL("data-test-id", "LinkToUrl"),
    COLORS_THUMBNAIL("data-test-id", "ImageThumbail"),
    NAME("data-test-id", "ProductName"),
    PRICE("data-test-id", "ProductPrices"),
    TITLE("data-test-id", "ProductTileDefault"),
    SIZES_CONTAINER("data-test-id", "SizeSelectorValue");

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
