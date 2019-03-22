package com.gabchak.services.impl;

import com.gabchak.models.Product;
import com.gabchak.models.ProductDetails;
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
        Set<ProductDetails> productDetails = new LinkedHashSet<>();

        Set<String> sizes = new LinkedHashSet<>();
        sizes.add("S");
        sizes.add("M");
        sizes.add("L");
        sizes.add("XL");
        sizes.add("XXL");
        productDetails.add(new ProductDetails("red", "50", sizes));
        productDetails.add(new ProductDetails("blue", "50", sizes));
        productDetails.add(new ProductDetails("black", "50", null));

        productSet.add(new Product("Boxershorts 'CHUEY'", "JACK & JONES",
                "test", productDetails));
        productSet.add(new Product("T-Shirt 'PRADO'", "ELLESSE",
                "/p/jack-und-jones/boxershorts-chuey-3915350", null));
        productSet.add(new Product("Jacke 'Bela'", "MAGIC FOX x ABOUT YOU",
                "/p/jack-und-jones/boxershorts-chuey-3915350", null));
        productSet.add(new Product("T-Shirt '3-Stripes'", "ADIDAS ORIGINALS",
                "/p/jack-und-jones/boxershorts-chuey-3915350", null));
        productSet.add(new Product("Hose", "Urban Classics",
                "/p/jack-und-jones/boxershorts-chuey-3915350", null));
        productSet.add(new Product("Sandale 'Adilette Aqua'", "ADIDAS ORIGINALS",
                "/p/jack-und-jones/boxershorts-chuey-3915350", null));
    }

    @Test
    void convertToJson() {
        ConverterService converterService = new ConverterServiceImpl();
        converterService.convertToJson(productSet);
    }

    @Test
    void convertToXml() throws IOException {

        ConverterService converterService = new ConverterServiceImpl();
        converterService.convertToXml(productSet);

        File converted = new File("products.xml");
        File example = new File("testExample.xml");

        assertEquals(FileUtils.readFileToString(example, "utf-8"),
                FileUtils.readFileToString(converted, "utf-8"));
    }
}