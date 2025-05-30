package me.themoonis.ticketSystem.ui.impl;

import me.themoonis.ticketSystem.TicketSystem;
import me.themoonis.ticketSystem.report.TicketStorage;
import me.themoonis.ticketSystem.ui.AbstractUserInterface;
import me.themoonis.ticketSystem.ui.api.UserInterfaceData;
import me.themoonis.ticketSystem.ui.managers.impl.PlayerManager;
import net.kyori.adventure.text.Component;

public class AllTicketsUserInterface extends AbstractUserInterface {

    private final TicketStorage ticketStorage;

    public AllTicketsUserInterface(TicketSystem ticketSystem) {
        super(ticketSystem.getPlayerManager(), UserInterfaceData.create(data -> {
            data.addContextObject("title", Component.text("All Tickets"));
            data.addContextObject("size",54);
        }));
        this.ticketStorage = ticketSystem.getTicketStorage();
    }

    @Override
    public void loadItems() {

    }

    @Override
    public void loadButtons() {



    }

    public void update(){
        userInterfaceActions.clear();
        
    }

}
