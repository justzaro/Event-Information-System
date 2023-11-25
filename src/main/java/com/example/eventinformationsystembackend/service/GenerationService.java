package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.model.Order;
import com.example.eventinformationsystembackend.model.SupportTicket;
import com.example.eventinformationsystembackend.model.SupportTicketReply;
import com.example.eventinformationsystembackend.model.User;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.List;

public interface GenerationService {
    void generateTicketQrCode(String data, String path) throws IOException, WriterException;

    void generateOrderReceivedEmailTemplate(Order order, User user);

    void generateOrderedTicketsEmailTemplate(Order order, User user);

    String generateSupportTicketReceivedTemplate(SupportTicket supportTicket, User user);

    String generateSupportTicketResponseTemplate(SupportTicketReply supportTicketReply, User user);

    List<String> generateCodes(int codesQuantity, int codeLength, String codeAlphabet);
}
