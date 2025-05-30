package me.themoonis.ticketSystem.report;


import java.util.UUID;

public record TicketReference(UUID uuid, Ticket reference) {

    @Override
    public String toString() {
        return uuid + " " + reference;
    }
}
