package me.themoonis.ticketSystem.report;

import lombok.RequiredArgsConstructor;
import me.themoonis.ticketSystem.TicketSystem;
import me.themoonis.ticketSystem.files.TicketStorageFile;

import java.util.UUID;

@RequiredArgsConstructor
public class TicketDeleter {

    private final TicketSystem ticketSystem;
    private final UUID uuid;
    private final DeleteType deleteType;

    public boolean delete() {
        TicketStorage ticketStorage = ticketSystem.getTicketStorage();
        TicketStorageFile ticketStorageFile = ticketSystem.getTicketStorageFile();

        switch (deleteType) {

            /*In this case, the uuid references on who was reported.*/
            case REPORTED -> {
                for (TicketReference ticketReference : ticketStorage.getAllTicketsFor(uuid)) {
                    UUID ticketUUID = ticketReference.uuid();
                    ticketStorage.removeTicket(ticketUUID);
                    ticketStorageFile.delete(ticketUUID);
                }
            }
            /*In this case, the uuid references on who reported people.*/
            case REPORTER -> {
                for (TicketReference ticketReference : ticketStorage.getAllTicketsFrom(uuid)) {
                    UUID ticketUUID = ticketReference.uuid();
                    ticketStorage.removeTicket(ticketUUID);
                    ticketStorageFile.delete(ticketUUID);
                }
            }
            case SINGLE -> {
                if (!ticketStorageFile.has(uuid) || !ticketStorage.has(uuid)) break;
                ticketStorageFile.delete(uuid);
                ticketStorage.removeTicket(uuid);
            }
        }

        return true;
    }

    public enum DeleteType {
        REPORTER,
        REPORTED,
        SINGLE
    }
}
