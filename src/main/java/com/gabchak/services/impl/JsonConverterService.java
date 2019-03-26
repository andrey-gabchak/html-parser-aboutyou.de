package com.gabchak.services.impl;

import com.gabchak.models.Product;
import com.gabchak.services.ConverterService;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class JsonConverterService implements ConverterService {

    private final static Logger LOGGER = Logger.getLogger(JsonConverterService.class);

    @Override
    public void convert(Set<Product> productSet, String filePath) {
        JSONArray productsArray = new JSONArray();
        productSet.forEach(product -> productsArray.put(new JSONObject(product)));

        try (FileWriter file = new FileWriter(filePath)) {

            file.write(productsArray.toString());
            file.flush();

        } catch (IOException e) {
            LOGGER.error("The app could not create file ", e);
        }
    }


}
