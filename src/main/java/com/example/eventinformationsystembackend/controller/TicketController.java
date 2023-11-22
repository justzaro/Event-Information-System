package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.service.implementation.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketServiceImpl ticketServiceImpl;

    @Autowired
    public TicketController(TicketServiceImpl ticketServiceImpl) {
        this.ticketServiceImpl = ticketServiceImpl;
    }

    @GetMapping(path = "/verification/{ticketCode}")
    public ModelAndView verifyTicket(@PathVariable String ticketCode) {
        ModelAndView responseTemplatePage = new ModelAndView();
        responseTemplatePage.setViewName(ticketServiceImpl.verifyTicket(ticketCode));
        return responseTemplatePage;
    }

    @GetMapping("/sold-per-day-in-last-days/{days}")
    public List<Integer> getSoldTicketsCountPerDayForTheThirtyDays(@PathVariable int days) {
        return ticketServiceImpl.getSoldTicketsCountPerDayForTheLastSelectedDays(days);
    }
}
