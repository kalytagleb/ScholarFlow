package com.scholarflow.business.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.scholarflow.business.model.Field;
import com.scholarflow.business.model.Paper;

public final class XmlService {
    private static final Logger log = LoggerFactory.getLogger(XmlService.class);

    private final FieldService fieldService;

    public XmlService(final FieldService fieldService) {
        this.fieldService = fieldService;
    }

    public void exportPapers(final List<Paper> papers, final File outputFile) {
        Objects.requireNonNull(papers, "papers list required");
        Objects.requireNonNull(outputFile, "output file required");

        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            final Element root = doc.createElement("papers");
            doc.appendChild(root);

            for (Paper paper : papers) {
                final Element paperEl = doc.createElement("paper");
                appendText(doc, paperEl, "id", paper.id().map(Object::toString).orElse(""));
                appendText(doc, paperEl, "title", paper.title());
                appendText(doc, paperEl, "abstract", paper.paperAbstract());
                appendText(doc, paperEl, "keywords", paper.keywords().orElse(""));
                appendText(doc, paperEl, "status", paper.status().name());
                appendText(doc, paperEl, "fieldId", paper.fieldId().toString());
                appendText(doc, paperEl, "submitterId", paper.submitterId().toString());
                appendText(doc, paperEl, "createdAt",
                    paper.createdAt().map(Object::toString).orElse(""));
                root.appendChild(paperEl);
            }

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(doc), new StreamResult(outputFile));

            log.info("Exported {} papers to {}", papers.size(), outputFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("XML export failed: {}", outputFile.getAbsolutePath(), e);
            throw new RuntimeException("Failed to export papers: " + e.getMessage(), e);
        }
    }

    public List<Field> importFields(final File inputFile) {
        Objects.requireNonNull(inputFile, "input file required");

        final List<Field> imported = new ArrayList<>();

        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();

            final NodeList fieldNodes = doc.getElementsByTagName("field");

            for (int i = 0; i < fieldNodes.getLength(); i++) {
                final Element el = (Element) fieldNodes.item(i);
                final String nameEn = text(el, "name_en");
                final String nameSk = text(el, "name_sk");
                final String description = text(el, "description");

                if (nameEn.isBlank() || nameSk.isBlank()) {
                    log.warn("Skipping field entry #{}: missing name_en or name_sk", i);
                    continue;
                }

                try {
                    final Field created = fieldService.createField(nameEn, nameSk, description);
                    imported.add(created);
                    log.info("Imported field: {}/{}", nameEn, nameSk);
                } catch (IllegalArgumentException e) {
                    log.warn("Skipped duplicate field: {}/{}", nameEn, nameSk);
                }
            }
        } catch (Exception e) {
            log.error("XML import failed: {}", inputFile.getAbsolutePath(), e);
            throw new RuntimeException("Failed to import fields: " + e.getMessage(), e);
        }

        return imported;
    }

    private void appendText(final Document doc, final Element parent, final String tag, final String value) {
        final Element el = doc.createElement(tag);
        el.setTextContent(value != null ? value : "");
        parent.appendChild(el);
    }

    private String text(final Element parent, final String tag) {
        final NodeList nodes = parent.getElementsByTagName(tag);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent().trim() : "";
    }
}
