package com.koolboks.creditProject.service.salesOrder;






import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

@Service
public class SalesOrderService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.from}")
    private String fromEmail;

    /**
     * Generate Sales Order PDF
     */
    public byte[] generateSalesOrderPDF(Map<String, Object> orderData) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Logo/Header
        Font headerFont = new Font(Font.HELVETICA, 24, Font.BOLD, new Color(247, 98, 59));
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.GRAY);
        Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

        // Company Header
        Paragraph company = new Paragraph("KOOLBOKS", headerFont);
        company.setAlignment(Element.ALIGN_CENTER);
        document.add(company);

        Paragraph salesOrder = new Paragraph("SALES ORDER", titleFont);
        salesOrder.setAlignment(Element.ALIGN_CENTER);
        salesOrder.setSpacingBefore(10);
        salesOrder.setSpacingAfter(20);
        document.add(salesOrder);

        // Order Info Box
        PdfPTable orderInfoTable = new PdfPTable(2);
        orderInfoTable.setWidthPercentage(100);
        orderInfoTable.setSpacingBefore(10);
        orderInfoTable.setSpacingAfter(20);

        addCell(orderInfoTable, "Order ID:", labelFont, String.valueOf(orderData.get("orderId")), valueFont);
        addCell(orderInfoTable, "Order Date:", labelFont, String.valueOf(orderData.get("paygoConfigurationDate")), valueFont);
        addCell(orderInfoTable, "Loan Reference:", labelFont, String.valueOf(orderData.get("customerLoanRef")), valueFont);
        addCell(orderInfoTable, "Loan Duration:", labelFont, orderData.get("customerLoanDuration") + " months", valueFont);

        document.add(orderInfoTable);

        // Customer Information
        document.add(new Paragraph("CUSTOMER INFORMATION", titleFont));
        PdfPTable customerTable = new PdfPTable(2);
        customerTable.setWidthPercentage(100);
        customerTable.setSpacingBefore(10);
        customerTable.setSpacingAfter(20);

        addCell(customerTable, "Full Name:", labelFont,
            orderData.get("customerFirstName") + " " + orderData.get("customerLastName"), valueFont);
        addCell(customerTable, "Email:", labelFont, String.valueOf(orderData.get("customerEmail")), valueFont);
        addCell(customerTable, "Phone:", labelFont, String.valueOf(orderData.get("customerPhoneNumber")), valueFont);

        document.add(customerTable);

        // Agent Information
        document.add(new Paragraph("AGENT INFORMATION", titleFont));
        PdfPTable agentTable = new PdfPTable(2);
        agentTable.setWidthPercentage(100);
        agentTable.setSpacingBefore(10);
        agentTable.setSpacingAfter(20);

        addCell(agentTable, "Agent Name:", labelFont, String.valueOf(orderData.get("agentName")), valueFont);
        addCell(agentTable, "Agent ID:", labelFont, String.valueOf(orderData.get("agentId")), valueFont);
        addCell(agentTable, "Email:", labelFont, String.valueOf(orderData.get("agentEmail")), valueFont);
        addCell(agentTable, "Phone:", labelFont, String.valueOf(orderData.get("agentNumber")), valueFont);

        document.add(agentTable);

        // Product Details
        document.add(new Paragraph("PRODUCT DETAILS", titleFont));
        PdfPTable productTable = new PdfPTable(2);
        productTable.setWidthPercentage(100);
        productTable.setSpacingBefore(10);
        productTable.setSpacingAfter(20);

        addCell(productTable, "Product Name:", labelFont, String.valueOf(orderData.get("productName")), valueFont);
        addCell(productTable, "Brand:", labelFont, String.valueOf(orderData.get("productBrand")), valueFont);
        addCell(productTable, "Size:", labelFont, String.valueOf(orderData.get("productSize")), valueFont);

        document.add(productTable);

        // Store & Payment
        document.add(new Paragraph("STORE & PAYMENT INFORMATION", titleFont));
        PdfPTable storeTable = new PdfPTable(2);
        storeTable.setWidthPercentage(100);
        storeTable.setSpacingBefore(10);
        storeTable.setSpacingAfter(20);

        addCell(storeTable, "Store Name:", labelFont, String.valueOf(orderData.get("storeName")), valueFont);
        addCell(storeTable, "Location:", labelFont, String.valueOf(orderData.get("storeLocation")), valueFont);
        addCell(storeTable, "Payment Date:", labelFont, String.valueOf(orderData.get("paymentDate")), valueFont);

        // Amount (highlighted)
        double amount = Double.parseDouble(String.valueOf(orderData.get("initialInstalment")));
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "NG"));
        String formattedAmount = formatter.format(amount);

        Font amountFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(247, 98, 59));
        PdfPCell amountLabelCell = new PdfPCell(new Phrase("Initial Installment:", labelFont));
        amountLabelCell.setBorder(Rectangle.NO_BORDER);
        amountLabelCell.setPadding(8);
        storeTable.addCell(amountLabelCell);

        PdfPCell amountValueCell = new PdfPCell(new Phrase(formattedAmount, amountFont));
        amountValueCell.setBorder(Rectangle.NO_BORDER);
        amountValueCell.setPadding(8);
        storeTable.addCell(amountValueCell);

        document.add(storeTable);

        // Paygo Configuration
        document.add(new Paragraph("PAYGO CONFIGURATION", titleFont));
        PdfPTable paygoTable = new PdfPTable(2);
        paygoTable.setWidthPercentage(100);
        paygoTable.setSpacingBefore(10);
        paygoTable.setSpacingAfter(20);

        addCell(paygoTable, "Configuration Date:", labelFont, String.valueOf(orderData.get("paygoConfigurationDate")), valueFont);
        addCell(paygoTable, "Configured By:", labelFont, String.valueOf(orderData.get("paygoConfiguredBy")), valueFont);

        document.add(paygoTable);

        // Footer
        Paragraph footer = new Paragraph("\n\nThis is a system-generated sales order. No signature required.",
            new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        return baos.toByteArray();
    }

    private void addCell(PdfPTable table, String label, Font labelFont, String value, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }

    /**
     * Send Sales Order PDF to Agent
     */
    public void sendSalesOrderToAgent(Map<String, Object> orderData, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(String.valueOf(orderData.get("agentEmail")));
            helper.setSubject("📦 Sales Order Confirmed - Order ID: " + orderData.get("orderId"));

            String htmlContent = buildAgentEmailContent(orderData);
            helper.setText(htmlContent, true);

            // Attach PDF
            ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
            helper.addAttachment("SalesOrder_" + orderData.get("orderId") + ".pdf", dataSource);

            mailSender.send(message);

            System.out.println("Sales order sent to agent: " + orderData.get("agentEmail"));

        } catch (MessagingException e) {
            System.err.println("Failed to send sales order to agent: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildAgentEmailContent(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">Sales Order Confirmed</h1>");
        html.append("<p style=\"color:#f7623b;margin:10px 0 0 0;font-size:18px;font-weight:bold;\">Order ID: ")
            .append(data.get("orderId")).append("</p>");
        html.append("</div>");

        // Content
        html.append("<div style=\"padding:30px;\">");
        html.append("<p style=\"font-size:16px;color:#333;\">Dear ").append(data.get("agentName")).append(",</p>");
        html.append("<p style=\"font-size:14px;color:#666;line-height:1.6;\">");
        html.append("The sales order for customer <strong>").append(data.get("customerFirstName"))
            .append(" ").append(data.get("customerLastName")).append("</strong> has been confirmed.</p>");
        html.append("<p style=\"font-size:14px;color:#666;line-height:1.6;\">");
        html.append("Please find the detailed sales order attached as a PDF document.</p>");

        // Order Summary Box
        html.append("<div style=\"background:#f7623b;color:#000;padding:20px;border-radius:8px;margin:20px 0;\">");
        html.append("<h3 style=\"margin:0 0 10px 0;\">Order Summary</h3>");
        html.append("<p style=\"margin:5px 0;\"><strong>Product:</strong> ").append(data.get("productName")).append("</p>");
        html.append("<p style=\"margin:5px 0;\"><strong>Amount:</strong> ₦").append(data.get("initialInstalment")).append("</p>");
        html.append("<p style=\"margin:5px 0;\"><strong>Customer:</strong> ").append(data.get("customerFirstName"))
            .append(" ").append(data.get("customerLastName")).append("</p>");
        html.append("</div>");

        html.append("<p style=\"font-size:12px;color:#999;margin-top:30px;\">This is an automated email. Please do not reply.</p>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background:#000;padding:20px;text-align:center;\">");
        html.append("<p style=\"color:#999;margin:0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }
}