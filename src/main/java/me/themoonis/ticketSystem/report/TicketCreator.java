package me.themoonis.ticketSystem.report;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class TicketCreator {

    private UUID uuid;
    private Timestamp timestamp;

    private UUID reporter, reported;
    private String[] reasons;
    private String description;

    public TicketCreator() {
        this.uuid = UUID.randomUUID();
        this.timestamp = Timestamp.from(Instant.now());
    }


    public TicketCreator reporter(UUID reporter) {
        this.reporter = reporter;
        return this;
    }

    public TicketCreator reported(UUID reported) {
        this.reported = reported;
        return this;
    }

    public TicketCreator reasons(String... reasons) {
        this.reasons = reasons;
        return this;
    }

    public TicketCreator description(String description) {
        this.description = description;
        return this;
    }

    public static TicketCreator create() {
        return new TicketCreator();
    }

    public TicketReference reference() {
        Ticket ticket = new Ticket(reporter,reported,timestamp,reasons,description);
        return new TicketReference(uuid,ticket);
    }
}
