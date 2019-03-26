package com.gabchak.services;

import com.gabchak.models.Product;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public interface HtmlParserService {

    Set<Product> getProducts(String url);

    void getProductDetails(Set<Product> productSet)
            throws InterruptedException;

    AtomicInteger getRequestsAmount();
}