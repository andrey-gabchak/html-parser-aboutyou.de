package com.gabchak.services;

import com.gabchak.models.Product;

import java.util.Set;

public interface ConverterService {

    void convertToJson(Set<Product> productSet);

    void convertToXml(Set<Product> productSet);
}
