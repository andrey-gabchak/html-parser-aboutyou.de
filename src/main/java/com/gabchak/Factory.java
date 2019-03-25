package com.gabchak;

import com.gabchak.models.Product;
import com.gabchak.models.ProductDetail;
import com.gabchak.services.ConverterService;
import com.gabchak.services.HtmlParserService;
import com.gabchak.services.impl.JsonConverterService;
import com.gabchak.services.impl.HtmlParserServiceImpl;

public class Factory {

    public static Product getProduct() {
        return new Product();
    }

    public static ProductDetail getProductDetail() {
        return new ProductDetail();
    }

    public static HtmlParserService getHtmlParserService() {
        return new HtmlParserServiceImpl();
    }

    public static ConverterService getConverterService() {
        return new JsonConverterService();
    }
}
