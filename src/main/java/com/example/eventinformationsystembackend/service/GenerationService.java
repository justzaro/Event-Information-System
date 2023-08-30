package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.dto.OrderItemDtoResponse;
import com.example.eventinformationsystembackend.dto.TicketDtoResponse;
import com.example.eventinformationsystembackend.model.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.pdf.BaseFont;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import static com.example.eventinformationsystembackend.common.QRCodeDetails.*;
import static com.example.eventinformationsystembackend.common.EmailTexts.*;
import static com.example.eventinformationsystembackend.common.FilePaths.*;

@Service
public class GenerationService {

    private final OrderItemService orderItemService;
    private final EmailService emailService;

    @Autowired
    public GenerationService(OrderItemService orderItemService,
                             EmailService emailService) {
        this.orderItemService = orderItemService;
        this.emailService = emailService;
    }

    public List<String> generateCodes(int codesQuantity,
                                      int codeLength,
                                      String codeAlphabet) {
        Random random = new Random();

        List<String> generatedCouponCodes = new ArrayList<>();

        for (int i = 0; i < codesQuantity; i++) {
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < codeLength; j++) {
                int index = random.nextInt(codeAlphabet.length());
                char randomChar = codeAlphabet.charAt(index);
                sb.append(randomChar);
            }

            String generatedString = sb.toString();
            generatedCouponCodes.add(generatedString);
        }

        return generatedCouponCodes;
    }

    public void generateTicketQrCode(String data, String path) throws IOException, WriterException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter
                .encode(data, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        MatrixToImageWriter.writeToPath(bitMatrix, QR_CODE_IMAGE_FORMAT, Paths.get(path));
    }

    public void generateOrderReceivedEmailTemplate(Order order, User user) throws IOException {
        String tableRows = generateEventInformationTable(order)
                .toString();
        String billingInformation = generateOrderBillingInformation(order);

        List<OrderItemDtoResponse> orderItems = orderItemService.getOrderItems(order.getId());

        String formattedEmail = String.format(ORDER_RECEIVED_EMAIL_TEMPLATE, user.getFirstName(),
                                                                   order.getId(),
                                                                   tableRows,
                                                                   billingInformation);

        emailService.sendOrderEmail(user, formattedEmail, orderItems);
    }

    public StringBuilder generateEventInformationTable(Order order) {
        List<OrderItemDtoResponse> orderItems = orderItemService.getOrderItems(order.getId());

        StringBuilder sb = new StringBuilder();

        String eventInformationTable =
                            """
                             <tr>
                                <td><img src="cid:%s" alt="Event Image" width="200" height="120"></td>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%.2f лв.</td>
                                <td>%.2f лв.</td>
                             </tr>
                            """;

        for (OrderItemDtoResponse orderItem : orderItems) {
            List<TicketDtoResponse> tickets = orderItem.getTickets();

            EventDtoResponse event = orderItem.getTickets().get(0).getEvent();

            String formattedTableRow =
                    String.format(eventInformationTable, event.getId(),
                                                         event.getName(),
                                                         event.getLocation(),
                                                         tickets.size(),
                                                         event.getTicketPrice(),
                                                         tickets.size() * event.getTicketPrice());

            sb.append(formattedTableRow).append("\n");
        }

        return sb;
    }

    public String generateOrderBillingInformation(Order order) {
        double discountAmount = 0;
        double totalOrderPrice = 0;
        double amountToPay;

        String couponCode = "NONE";

        List<OrderItem> orderItems = order.getOrderItems();

        for (OrderItem orderItem : orderItems) {
            totalOrderPrice += orderItem.getTickets().get(0).getEvent().getTicketPrice() * orderItem.getTickets().size();
        }

        amountToPay = totalOrderPrice;

        if (order.getCoupon() != null) {
            Coupon coupon = order.getCoupon();
            couponCode = coupon.getCouponCode();
            discountAmount = totalOrderPrice - (totalOrderPrice * (coupon.getDiscountPercentage() / 100));
            amountToPay -= discountAmount;
        }

        String billingInformation =
                """
                 <p><strong style="font-size:18px">Total Order Price:</strong> <span style="color: black;">%.2f лв.</span></p>
                 <p><strong style="font-size:18px">Coupon Code:</strong> <span style="color: black;">%s</span></p>
                 <p><strong style="font-size:18px">Discount amount:</strong> <span style="color: black;">%.2f лв.</span></p>
                 <p><strong style="font-size:18px">Amount To Pay:</strong> <span style="color: black;">%.2f лв.</span>
                """;

        return String.format(billingInformation, totalOrderPrice,
                                                 couponCode,
                                                 discountAmount,
                                                 amountToPay);
    }

    public void generateOrderedTicketsEmailTemplate(Order order, User user) {
        List<OrderItemDtoResponse> orderItems = orderItemService.getOrderItems(order.getId());
        List<TicketDtoResponse> ticketsForOrderItem = new ArrayList<>();

        String currentTicketPdfFilePath;
        List<String> ticketFilePaths = new ArrayList<>();
        List<TicketDtoResponse> tickets = new ArrayList<>();

        for (OrderItemDtoResponse orderItem : orderItems) {
            ticketsForOrderItem.addAll(orderItem.getTickets());
            EventDtoResponse event = orderItem.getTickets().get(0).getEvent();

            for (TicketDtoResponse ticket : ticketsForOrderItem){
                String htmlFilePath = createHtmlFileForTicket(ticket, event, user);

                try {
                    currentTicketPdfFilePath = createPdfFileFromHtmlFile(htmlFilePath);
                    ticketFilePaths.add(currentTicketPdfFilePath);
                    tickets.add(ticket);
                } catch (IOException e) {

                }
            }
            ticketsForOrderItem.clear();
        }

        String formattedText = String.format(TICKETS_RECEIVED_EMAIL_TEMPLATE, user.getFirstName());

        emailService.sendTicketsEmail(user, formattedText, ticketFilePaths, tickets);
    }

    private String createHtmlFileForTicket(TicketDtoResponse ticket, EventDtoResponse event, User user) {
        String htmlFileName = ticket.getCode() + ".html";
        String htmlFilePath = TICKET_PDFS_FOLDER_PATH + htmlFileName;

        File htmlFile = new File(htmlFilePath);

        String ticketCodeEncodedInBase64 = null;

        try {
            ticketCodeEncodedInBase64 = generateBase64StringForTicketCode(ticket);
        } catch (IOException e) {

        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        String formattedEventStartDate = event.getStartDate().format(formatter);

        String formattedTemplate =
                String.format(SINGULAR_TICKET_PDF_TEMPLATE, event.getName(),
                                                            formattedEventStartDate,
                                                            event.getLocation(),
                                                            ticketCodeEncodedInBase64,
                                                            user.getEmail());

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
            bw.write(formattedTemplate);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return htmlFilePath;
    }

    private String generateBase64StringForTicketCode(TicketDtoResponse ticket) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(new File(ticket.getQrCodeImagePath()));
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private String createPdfFileFromHtmlFile(String htmlFilePath) throws IOException {
        //Change substring index values in case of HTML files' path modification
        //The substring extracts the name of the ticket from the ticket .html file without the extension
        String htmlFileName = htmlFilePath.substring(61, 77);
        String pdfFileName = htmlFileName + ".pdf";
        String outputPdfFilePath = TICKET_PDFS_FOLDER_PATH + pdfFileName;

        File inputHtml = new File(htmlFilePath);

        Document document = Jsoup.parse(inputHtml, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        try (OutputStream outputStream = new FileOutputStream(outputPdfFilePath);) {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();

            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);
            renderer.getFontResolver().addFont(VERDANA_FONT_PATH, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            renderer.setDocumentFromString(document.html());
            renderer.layout();
            renderer.createPDF(outputStream);
        }

        return outputPdfFilePath;
    }
}
