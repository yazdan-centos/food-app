package com.mapnaom.foodapp.security;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class PublicUrlsProvider {

    private static final String RESOURCE_PATH = "security/public-urls.xml";

    private final List<String> publicUrls;

    public PublicUrlsProvider() {
        this.publicUrls = loadPublicUrls();
    }

    public List<String> getPublicUrls() {
        return publicUrls;
    }

    private List<String> loadPublicUrls() {
        try (InputStream is = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList nodes = doc.getElementsByTagName("url");
            List<String> urls = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                String value = nodes.item(i).getTextContent();
                if (value != null) {
                    String trimmed = value.trim();
                    if (!trimmed.isEmpty()) {
                        urls.add(trimmed);
                    }
                }
            }
            return List.copyOf(urls);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public URLs from " + RESOURCE_PATH, e);
        }
    }
}
