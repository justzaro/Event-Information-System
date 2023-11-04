package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.model.Ticket;
import com.example.eventinformationsystembackend.service.TicketService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequestMapping(path = "/tickets")
public class TicketController {
    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping(path = "/verification/{ticketCode}")
    public ModelAndView verifyTicket(@PathVariable("ticketCode") String ticketCode) {
        ModelAndView responseTemplatePage = new ModelAndView();
        responseTemplatePage.setViewName(ticketService.verifyTicket(ticketCode));
        return responseTemplatePage;
    }

    @GetMapping("/sold-per-day-in-last-days/{days}")
    public List<Integer> getSoldTicketsCountPerDayForTheThirtyDays(@PathVariable("days") int days) {
        return ticketService.getSoldTicketsCountPerDayForTheLastSelectedDays(days);
    }
}
