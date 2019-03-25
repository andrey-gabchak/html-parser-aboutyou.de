package com.gabchak;

import com.gabchak.models.Product;
import com.gabchak.services.ConverterService;
import com.gabchak.services.HtmlParserService;
import com.gabchak.services.impl.HtmlParserServiceImpl;

import java.util.Set;

public class WebParser {

    private final static String SITE_URL = "https://www.aboutyou.de/maenner/bekleidung";

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        HtmlParserService htmlParserService = Factory.getHtmlParserService();
        Set<Product> products = htmlParserService.getProducts(SITE_URL);
        htmlParserService.getProductDetails(products);

        System.out.println("Amount of extracted products is " + products.size());
        System.out.println(HtmlParserServiceImpl.getRequestsAmount());

        ConverterService converterService = Factory.getConverterService();
        converterService.convert(products, "products.json");

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Run-time : " + (totalTime / 1000d) + " seconds");
    }
}
