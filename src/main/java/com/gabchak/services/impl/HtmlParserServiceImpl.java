package com.gabchak.services.impl;

import com.gabchak.models.Product;
import com.gabchak.services.HtmlParserService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.gabchak.enums.ProductProperties.BRAND;
import static com.gabchak.enums.ProductProperties.COLOR;
import static com.gabchak.enums.ProductProperties.NAME;
import static com.gabchak.enums.ProductProperties.PRICE;
import static com.gabchak.enums.ProductProperties.TITLE;

public class HtmlParserServiceImpl implements HtmlParserService {

    private final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private int requestsAmount;


    @Override
    public Set<Product> getProducts(String url) {
        Document htmlDocument = getHtmlDocument(url);

        return parseProductsList(htmlDocument);
    }

    @Override
    public Set<Product> getColorAndSize(Set<Product> productSet) {

        for (Product product : productSet) {
            Document htmlDocument = getHtmlDocument(product.getUrl());

        }
        return null;
    }


    private Document getHtmlDocument(String url) {
        Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
        Document htmlDocument = null;

        if ((connection.response().statusCode() == 200) ||
                (connection.response().contentType().contains("text/html"))) {
            try {
                htmlDocument = connection.get();
            } catch (IOException e) {
                System.out.println("Failed to load the document.");
            }
            this.requestsAmount++;
        }
        return htmlDocument;
    }

    private Set<Product> parseProductsList(Document htmlDocument) {
        Elements productsAsElements = htmlDocument.getElementsByAttributeValue(TITLE.getPropertyName(),
                TITLE.getPropertyValue());

        Set<Product> products = new CopyOnWriteArraySet<>();

        productsAsElements.parallelStream().forEach(product -> {
            String name = product.getElementsByAttributeValue(NAME.getPropertyName(),
                    NAME.getPropertyValue()).text();
            String brand = product.getElementsByAttributeValue(BRAND.getPropertyName(),
                    BRAND.getPropertyValue()).text();
            String price = product.getElementsByAttributeValue(PRICE.getPropertyName(),
                    PRICE.getPropertyValue()).text();
            String url = product.getElementsByAttribute("href").val();

            products.add(new Product(name, brand, url, null));
        });

        return products;
    }

    void parseProductPage(Document htmlDocument, Product product) {
        //TODO: find out how to parse data after changes
        Elements colors = htmlDocument.getElementsByAttributeValue(
                COLOR.getPropertyName(), COLOR.getPropertyValue());
    }
}
