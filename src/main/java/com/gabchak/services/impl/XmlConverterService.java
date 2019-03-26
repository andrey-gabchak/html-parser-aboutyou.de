package com.gabchak.services.impl;

import com.gabchak.models.Product;
import com.gabchak.models.ProductDetail;
import com.gabchak.services.ConverterService;
import org.apache.log4j.Logger;
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
import java.util.Set;

public class XmlConverterService implements ConverterService {

    private final static Logger LOGGER = Logger.getLogger(XmlConverterService.class);

    @Override
    public void convert(Set<Product> productSet, String filePath) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            appendProducts(productSet, doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            File file = new File(filePath);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            LOGGER.error(e);
        }
    }

    private void appendProducts(Set<Product> productSet, Document doc) {
        Element rootElement = doc.createElement("products");
        doc.appendChild(rootElement);
        productSet.forEach(product -> {
            Element productElem = doc.createElement("product");

            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(product.getName()));

            Element brand = doc.createElement("brand");
            brand.appendChild(doc.createTextNode(product.getBrand()));



            productElem.appendChild(name);
            productElem.appendChild(brand);

            Set<ProductDetail> productDetails1 = product.getProductDetails();

            if (productDetails1 != null && !productDetails1.isEmpty()) {
                Element productDetails = appendProductDetails(doc, product);
                productElem.appendChild(productDetails);
            }
            rootElement.appendChild(productElem);
        });
    }

    private Element appendProductDetails(Document doc, Product product) {
        Element productDetails = doc.createElement("productDetails");

        product.getProductDetails().forEach(productDetail -> {

            Element url = doc.createElement("url");
            url.appendChild(doc.createTextNode(productDetail.getUrl()));

            Element colorElem = doc.createElement("color");
            colorElem.appendChild(doc.createTextNode(productDetail.getColor()));

            Element price = doc.createElement("price");
            price.appendChild(doc.createTextNode(productDetail.getPrice()));

            productDetails.appendChild(url);
            productDetails.appendChild(colorElem);
            productDetails.appendChild(price);

            Set<String> sizeSet = productDetail.getSizeSet();

            if (sizeSet != null && !sizeSet.isEmpty()) {
                Element sizes = convertSizes(doc, productDetail);
                productDetails.appendChild(sizes);
            }
        });
        return productDetails;
    }

    private Element convertSizes(Document doc, ProductDetail details) {
        Element sizes = doc.createElement("sizes");
        details.getSizeSet().forEach(size -> {
            Element sizeElem = doc.createElement("size");
            sizeElem.appendChild(doc.createTextNode(size));
            sizes.appendChild(sizeElem);
        });
        return sizes;
    }
}
