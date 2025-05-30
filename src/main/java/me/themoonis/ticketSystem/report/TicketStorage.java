package me.themoonis.ticketSystem.report;

import me.themoonis.ticketSystem.TicketSystem;

import javax.imageio.spi.IIOServiceProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TicketStorage {

    private final TicketSystem ticketSystem;

    private HashMap<UUID, Ticket> tickets;

    public TicketStorage(TicketSystem ticketSystem) {
        this.ticketSystem = ticketSystem;
        this.tickets = new HashMap<>();
    }

    public List<TicketReference> getAllTicketsFor(UUID player) {
        return getAllTickets().stream().filter(ticket -> ticket.reference().reported().compareTo(player) == 0).toList();
    }

    public List<TicketReference> getAllTicketsFrom(UUID player) {
        return getAllTickets().stream().filter(ticket -> ticket.reference().reporter().compareTo(player) == 0).toList();
    }

    public TicketReference getTicket(UUID ticketUUID) {
        return new TicketReference(ticketUUID, tickets.get(ticketUUID));
    }

    public void addTicket(UUID ticketUUID, Ticket ticket) {
        tickets.put(ticketUUID, ticket);
    }

    public void removeTicket(UUID ticketUUID) {
        tickets.remove(ticketUUID);
    }

    public void save() {
        for (TicketReference ticketReference : getAllTickets()) {
            ticketSystem.getTicketStorageFile().save(ticketReference);
        }
    }
    public List<TicketReference> getAllTickets() {
        return tickets.entrySet().stream().map(entry -> new TicketReference(entry.getKey(),entry.getValue())).toList();
    }

    public void clear() {
        tickets.clear();
    }

    public boolean has(UUID uuid) {
        return tickets.containsKey(uuid);
    }
}
