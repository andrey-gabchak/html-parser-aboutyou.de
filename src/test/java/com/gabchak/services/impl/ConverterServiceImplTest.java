package com.gabchak.services.impl;

import com.gabchak.models.Product;
import com.gabchak.models.ProductDetail;
import com.gabchak.services.ConverterService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConverterServiceImplTest {

    private static Set<Product> productSet = new LinkedHashSet<>();

    @BeforeAll
    static void setUp() {
        Set<ProductDetail> productDetails = new LinkedHashSet<>();

        Set<String> sizes = new LinkedHashSet<>();
        sizes.add("S");
        sizes.add("M");
        sizes.add("L");
        sizes.add("XL");
        sizes.add("XXL");

        ProductDetail productDetail1 = ProductDetail.getInstance();
        productDetail1.setUrl("/p/jack-und-jones/boxershorts-chuey-3915350");
        productDetail1.setPrice("50");
        productDetail1.setColor("red");
        productDetail1.setSizeSet(sizes);
        productDetails.add(productDetail1);

        ProductDetail productDetail2 = ProductDetail.getInstance();
        productDetail2.setUrl("/p/jack-und-jones/boxershorts-chuey-3915350");
        productDetail2.setPrice("50");
        productDetail2.setColor("blue");
        productDetail2.setSizeSet(sizes);
        productDetails.add(productDetail2);

        ProductDetail productDetail3 = ProductDetail.getInstance();
        productDetail3.setUrl("/p/jack-und-jones/boxershorts-chuey-3915350");
        productDetail3.setPrice("50");
        productDetail3.setColor("black");
        productDetail3.setSizeSet(sizes);
        productDetails.add(productDetail3);

        Product product1 = Product.getInstance();
        product1.setName("Boxershorts 'CHUEY'");
        product1.setBrand("JACK & JONES");
        product1.setProductDetails(productDetails);
        productSet.add(product1);

        Product product2 = Product.getInstance();
        product2.setName("T-Shirt 'PRADO'");
        product2.setBrand("ELLESSE");
        product2.setProductDetails(productDetails);
        productSet.add(product2);

        Product product3 = Product.getInstance();
        product3.setName("Jacke 'Bela'");
        product3.setBrand("MAGIC FOX x ABOUT YOU");
        product3.setProductDetails(productDetails);
        productSet.add(product3);

        Product product4 = Product.getInstance();
        product4.setName("T-Shirt '3-Stripes'");
        product4.setBrand("ADIDAS ORIGINALS");
        product4.setProductDetails(productDetails);
        productSet.add(product4);

        Product product5 = Product.getInstance();
        product5.setName("Hose");
        product5.setBrand("Urban Classics");
        product5.setProductDetails(productDetails);
        productSet.add(product5);

        Product product6 = Product.getInstance();
        product6.setName("Sandale 'Adilette Aqua'");
        product6.setBrand("ADIDAS ORIGINALS");
        product6.setProductDetails(productDetails);
        productSet.add(product6);
    }

    @Test
    void convertToJson() throws IOException {
        String filePath = "src/test/resources/testProduct.json";
        ConverterService converterService = new ConverterServiceImpl();
        converterService.convertToJson(productSet, filePath);

        File converted = new File(filePath);
        File example = new File("src/test/resources/testExample.json");

        assertEquals(FileUtils.readFileToString(example, "utf-8"),
                FileUtils.readFileToString(converted, "utf-8"));
    }

    @Test
    void convertToXml() throws IOException {
        String filePath = "src/test/resources/testProduct.xml";

        ConverterService converterService = new ConverterServiceImpl();
        converterService.convertToXml(productSet, filePath);

        File converted = new File(filePath);
        File example = new File("src/test/resources/testExample.xml");

        assertEquals(FileUtils.readFileToString(example, "utf-8"),
                FileUtils.readFileToString(converted, "utf-8"));
    }
}