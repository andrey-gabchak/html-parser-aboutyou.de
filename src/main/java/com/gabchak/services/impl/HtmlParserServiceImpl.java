package com.gabchak.services.impl;

import com.gabchak.Factory;
import com.gabchak.models.Product;
import com.gabchak.models.ProductDetail;
import com.gabchak.services.HtmlParserService;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    private AtomicInteger requestsAmount = new AtomicInteger(0);

    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private final static Logger LOGGER = Logger.getLogger(HtmlParserServiceImpl.class);

    @Override
    public Set<Product> getProducts(String url) {
        Document htmlDocument = getHtmlDocument(url);

        return parseProductsList(htmlDocument);
    }

    @Override
    public void getProductDetails(Set<Product> productSet) throws InterruptedException {
        ExecutorService executorService = Executors.newWorkStealingPool();

        Set<Callable<Set<ProductDetail>>> callableSet = new CopyOnWriteArraySet<>();

        productSet.parallelStream().forEach(product ->
                callableSet.add(() -> parseProductDetails(product)));

        executorService.invokeAll(callableSet);

        shutdownExecutorService(executorService);
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
            String url = htmlProduct.select("a").first()
                    .attr("abs:href");

            products.add(createProductWithDetails(name, brand, url));
        });

        return products;
    }

    private Product createProductWithDetails(String name, String brand, String url) {
        ProductDetail productDetails = Factory.getProductDetail();
        productDetails.setUrl(url);

        Product product = Factory.getProduct();
        product.setName(name);
        product.setBrand(brand);
        product.addProductDetails(productDetails);
        return product;
    }

    private Set<ProductDetail> parseProductDetails(Product product) {
        ProductDetail defaultProductDetail = product.getProductDetails().stream().findFirst().get();
        Document htmlDocument = getHtmlDocument(defaultProductDetail.getUrl());
        Set<ProductDetail> colors = parseAllProductColors(htmlDocument);

        colors.parallelStream().forEach(color -> {
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
        return colors;
    }

    private Set<ProductDetail> parseAllProductColors(Document htmlDocument) {
        Elements colors = htmlDocument.getElementsByAttributeValue(COLORS_THUMBNAIL.getPropertyName(),
                COLORS_THUMBNAIL.getPropertyValue());
        Set<ProductDetail> result = new CopyOnWriteArraySet<>();

        colors.parallelStream().forEach(color -> {
            String url = color.getElementsByAttributeValue(COLOR_URL.getPropertyName(),
                    COLOR_URL.getPropertyValue())
                    .attr("abs:href");
            String colorName = color.getElementsByAttributeValue(COLOR_NAME.getPropertyName(),
                    COLOR_NAME.getPropertyValue())
                    .text();

            ProductDetail productDetail = Factory.getProductDetail();
            productDetail.setUrl(url);
            productDetail.setColor(colorName);
            result.add(productDetail);
        });
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

    public AtomicInteger getRequestsAmount() {
        return requestsAmount;
    }

    private void shutdownExecutorService(ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Tasks interrupted", e);
        } finally {
            if (!executor.isTerminated()) {
                LOGGER.error("Cancel non-finished tasks");
            }
            executor.shutdownNow();
        }
    }
}
