package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.service.TicketService;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping(path = "/verification/{ticketCode}")
    public ModelAndView verifyTicket(@PathVariable String ticketCode) {
        ModelAndView responseTemplatePage = new ModelAndView();
        responseTemplatePage.setViewName(ticketService.verifyTicket(ticketCode));
        return responseTemplatePage;
    }

    @GetMapping("/sold-per-day-in-last-days/{days}")
    public List<Integer> getSoldTicketsCountPerDayForTheThirtyDays(@PathVariable int days) {
        return ticketService.getSoldTicketsCountPerDayForTheLastSelectedDays(days);
    }
}
