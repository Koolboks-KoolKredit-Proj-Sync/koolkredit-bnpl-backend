package com.koolboks.creditProject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Converts an HTML string into PDF bytes.
 *
 * Dependencies to add to pom.xml:
 *
 * <!-- Flying Saucer PDF renderer (uses OpenPDF internally) -->
 * <dependency>
 *     <groupId>org.xhtmlrenderer</groupId>
 *     <artifactId>flying-saucer-pdf-openpdf</artifactId>
 *     <version>9.1.22</version>
 * </dependency>
 *
 * <!-- OpenPDF (LGPL fork of iText 2) -->
 * <dependency>
 *     <groupId>com.github.librepdf</groupId>
 *     <artifactId>openpdf</artifactId>
 *     <version>1.3.30</version>
 * </dependency>
 */
@Service
public class PdfGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(PdfGeneratorService.class);

    /**
     * Converts an XHTML string to a PDF and returns the raw bytes.
     *
     * @param html valid XHTML content (Flying Saucer requires well-formed XHTML)
     * @return PDF as byte array
     * @throws RuntimeException if PDF generation fails
     */
    public byte[] generatePdfFromHtml(String html) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);
            log.debug("PDF generated successfully ({} bytes)", baos.size());
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF from HTML: {}", e.getMessage(), e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }
}