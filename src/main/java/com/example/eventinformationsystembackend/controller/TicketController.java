package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.model.Ticket;
import com.example.eventinformationsystembackend.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(path = "/tickets")
public class TicketController {
    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping(path = "/verification/{ticketCode}")
    public String verifyTicket(@PathVariable("ticketCode") String ticketCode) {
        return ticketService.verifyTicket(ticketCode);
    }
/*    @GetMapping(path = "/verification/{ticketCode}")
    public RedirectView verifyTicket(@PathVariable("ticketCode") String ticketCode) {
        String externalUrl = "https://www.google.com/";
        RedirectView redirectView = new RedirectView(externalUrl);
        return redirectView;
    }*/
}
