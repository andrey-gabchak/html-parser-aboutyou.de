package com.gabchak;

import com.gabchak.models.Product;
import com.gabchak.services.ConverterService;
import com.gabchak.services.HtmlParserService;
import com.gabchak.services.impl.ConverterServiceImpl;
import com.gabchak.services.impl.HtmlParserServiceImpl;

import java.util.Set;

public class WebParser {

    private final static String SITE_URL = "https://www.aboutyou.de/maenner/bekleidung";

    public static void main(String[] args) {
        HtmlParserService htmlParserService = new HtmlParserServiceImpl();
        Set<Product> products = htmlParserService.getProducts(SITE_URL);
        Set<Product> productsWithColorsAndSizes = htmlParserService.getColorAndSize(products);

        System.out.println("Amount of extracted products is " + productsWithColorsAndSizes.size());

        ConverterService converterService = new ConverterServiceImpl();
        converterService.convertToJson(productsWithColorsAndSizes);
        converterService.convertToXml(productsWithColorsAndSizes);
    }
}
