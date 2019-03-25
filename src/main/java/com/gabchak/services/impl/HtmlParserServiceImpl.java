package com.gabchak.services.impl;

import com.gabchak.models.Product;
import com.gabchak.models.ProductDetail;
import com.gabchak.services.HtmlParserService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gabchak.enums.ProductProperties.ARTICLE_ID;
import static com.gabchak.enums.ProductProperties.BRAND;
import static com.gabchak.enums.ProductProperties.COLORS_THUMBNAIL;
import static com.gabchak.enums.ProductProperties.COLOR_NAME;
import static com.gabchak.enums.ProductProperties.COLOR_URL;
import static com.gabchak.enums.ProductProperties.NAME;
import static com.gabchak.enums.ProductProperties.PRICE;
import static com.gabchak.enums.ProductProperties.SIZES_CONTAINER;
import static com.gabchak.enums.ProductProperties.TITLE;

public class HtmlParserServiceImpl implements HtmlParserService {

    private final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private static volatile AtomicInteger requestsAmount = new AtomicInteger(0);

    @Override
    public Set<Product> getProducts(String url) {
        Document htmlDocument = getHtmlDocument(url);

        return parseProductsList(htmlDocument);
    }

    //TODO: maybe I should remove the method because it do iteration only.
    // Or implement multithreading.
    @Override
    public void getProductDetails(Set<Product> productSet) {

        for (Product product : productSet) {
            parseProductDetails(product);
        }
    }


    private Document getHtmlDocument(String url) {
        Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
        Document htmlDocument = null;

            try {
                htmlDocument = connection.get();
            } catch (IOException e) {
                System.out.println("Failed to load the document.");
            }
            requestsAmount.incrementAndGet();
        return htmlDocument;
    }

    private Set<Product> parseProductsList(Document htmlDocument) {
        Elements productsAsElements = htmlDocument.getElementsByAttributeValue(TITLE.getPropertyName(),
                TITLE.getPropertyValue());

        Set<Product> products = new CopyOnWriteArraySet<>();

        productsAsElements.parallelStream().forEach(htmlProduct -> {
            String name = htmlProduct.getElementsByAttributeValue(NAME.getPropertyName(),
                    NAME.getPropertyValue()).text();
            String brand = htmlProduct.getElementsByAttributeValue(BRAND.getPropertyName(),
                    BRAND.getPropertyValue()).text();
            String price = htmlProduct.getElementsByAttributeValue(PRICE.getPropertyName(),
                    PRICE.getPropertyValue()).text();
            String url = htmlProduct.select("a").first()
                    .attr("abs:href");


            products.add(createProductWithDetails(name, brand, url));
        });

        return products;
    }

    private Product createProductWithDetails(String name, String brand, String url) {
        ProductDetail productDetails = ProductDetail.getInstance();
        productDetails.setUrl(url);

        Product product = Product.getInstance();
        product.setName(name);
        product.setBrand(brand);
        product.addProductDetails(productDetails);
        return product;
    }

    private void parseProductDetails(Product product) {
        ProductDetail defaultProductDetail = product.getProductDetails().stream().findFirst().get();
        Document htmlDocument = getHtmlDocument(defaultProductDetail.getUrl());
        Set<ProductDetail> colors = parseAllProductColors(htmlDocument);

        colors.forEach(color -> {
            if (!product.getProductDetails().add(color)) {
                product.getProductDetails().remove(color);
                color.setHtmlDocument(htmlDocument);
            }
            if (color.getHtmlDocument() == null) {
                Document htmlDoc = getHtmlDocument(color.getUrl());
                color.setHtmlDocument(htmlDoc);
            }
            parseProductPage(color);
            product.getProductDetails().add(color);
        });
    }

    private Set<ProductDetail> parseAllProductColors(Document htmlDocument) {
        Elements colors = htmlDocument.getElementsByAttributeValue(COLORS_THUMBNAIL.getPropertyName(),
                COLORS_THUMBNAIL.getPropertyValue());
        Set<ProductDetail> result = new HashSet<>();
        for (Element color : colors) {
            String url = color.getElementsByAttributeValue(COLOR_URL.getPropertyName(),
                    COLOR_URL.getPropertyValue())
                    .attr("abs:href");
            String colorName = color.getElementsByAttributeValue(COLOR_NAME.getPropertyName(),
                    COLOR_NAME.getPropertyValue())
                    .text();

            ProductDetail productDetail = ProductDetail.getInstance();
            productDetail.setUrl(url);
            productDetail.setColor(colorName);
            result.add(productDetail);
        }
        return result;
    }

    private void parseProductPage(ProductDetail productDetail) {
        Document htmlDocument = productDetail.getHtmlDocument();
        String price = htmlDocument.getElementsByAttributeValue(
                PRICE.getPropertyName(), PRICE.getPropertyValue()).first().text();
        String articleId = htmlDocument.getElementsByAttributeValueContaining(
                ARTICLE_ID.getPropertyName(), ARTICLE_ID.getPropertyValue()).first().text();

        price = price.replaceAll("\\D,", "").replace("€", "");
        articleId = articleId.replace("Artikel-Nr: ", "");

        Elements sizesElements = htmlDocument.getElementsByAttributeValue(
                SIZES_CONTAINER.getPropertyName(), SIZES_CONTAINER.getPropertyValue());

        Set<String> sizes = new HashSet<>();

        sizesElements.forEach(element -> sizes.add(element.text()));

        productDetail.setPrice(price);
        productDetail.setArticleId(articleId);
        productDetail.setSizeSet(sizes);
        productDetail.setHtmlDocument(null);
    }

    public static AtomicInteger getRequestsAmount() {
        return requestsAmount;
    }
}
