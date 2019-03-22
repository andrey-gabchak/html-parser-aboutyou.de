package com.gabchak.services;

import com.gabchak.models.Product;

import java.util.Set;

public interface HtmlParserService {

    Set<Product> getProducts(String url);

    Set<Product> getColorAndSize(Set<Product> productSet);
}
