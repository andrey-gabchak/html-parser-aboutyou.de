package com.gabchak.services.impl;

import com.gabchak.models.Product;
import com.gabchak.models.ProductDetails;
import com.gabchak.services.ConverterService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class ConverterServiceImpl implements ConverterService {

    @Override
    public void convertToJson(Set<Product> productSet, String filePath) {
        JSONArray productsArray = new JSONArray();
        productSet.forEach(product -> productsArray.put(new JSONObject(product)));

        try (FileWriter file = new FileWriter(filePath)) {

            file.write(productsArray.toString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void convertToXml(Set<Product> productSet, String filePath) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            convertProducts(productSet, doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            File file = new File(filePath);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private void convertProducts(Set<Product> productSet, Document doc) {
        Element rootElement = doc.createElement("products");
        doc.appendChild(rootElement);
        productSet.forEach(product -> {
            Element productElem = doc.createElement("product");

            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(product.getName()));

            Element brand = doc.createElement("brand");
            brand.appendChild(doc.createTextNode(product.getBrand()));

            Element url = doc.createElement("url");
            url.appendChild(doc.createTextNode(product.getUrl()));

            productElem.appendChild(name);
            productElem.appendChild(brand);
            productElem.appendChild(url);

            if (product.getProductDetails() != null && !product.getProductDetails().isEmpty()) {
                Element productDetails = convertProductDetails(doc, product);
                productElem.appendChild(productDetails);
            }
            rootElement.appendChild(productElem);
        });
    }

    private Element convertProductDetails(Document doc, Product product) {
        Element productDetails = doc.createElement("productDetails");

        product.getProductDetails().forEach(details -> {

            Element colorElem = doc.createElement("color");
            colorElem.appendChild(doc.createTextNode(details.getColor()));

            Element price = doc.createElement("price");
            price.appendChild(doc.createTextNode(details.getPrice()));

            productDetails.appendChild(colorElem);
            productDetails.appendChild(price);


            if (details.getSizeSet() != null && !details.getSizeSet().isEmpty()) {
                Element sizes = convertSizes(doc, details);
                productDetails.appendChild(sizes);
            }
        });
        return productDetails;
    }

    private Element convertSizes(Document doc, ProductDetails details) {
        Element sizes = doc.createElement("sizes");
        details.getSizeSet().forEach(size -> {
            Element sizeElem = doc.createElement("size");
            sizeElem.appendChild(doc.createTextNode(size));
            sizes.appendChild(sizeElem);
        });
        return sizes;
    }
}
