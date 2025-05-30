package me.themoonis.ticketSystem.ui.impl;

import me.themoonis.ticketSystem.TicketSystem;
import me.themoonis.ticketSystem.report.Ticket;
import me.themoonis.ticketSystem.report.TicketReference;
import me.themoonis.ticketSystem.report.TicketStorage;
import me.themoonis.ticketSystem.ui.PaginatedUserInterface;
import me.themoonis.ticketSystem.ui.api.IUserInterface;
import me.themoonis.ticketSystem.ui.api.UserInterfaceData;
import me.themoonis.ticketSystem.ui.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class AllTicketsUserInterface extends PaginatedUserInterface {

    private final TicketStorage ticketStorage;
    private final TicketSystem ticketSystem;

    public AllTicketsUserInterface(@NotNull TicketSystem ticketSystem) {
        super(ticketSystem.getPlayerManager(), UserInterfaceData.create(data -> {
            data.addContextObject("title", Component.text("All Tickets"));
            data.addContextObject("size", 54);
        }));
        this.ticketStorage = ticketSystem.getTicketStorage();
        this.ticketSystem = ticketSystem;
    }

    @Override
    protected void miscLoad() {
        super.miscLoad();
        List<ItemStack> ticketItems = new ArrayList<>();

        for (TicketReference ticketReference : ticketStorage.getAllTickets()) {
            UUID ticketUUID = ticketReference.uuid();
            Ticket ticket = ticketReference.reference();

            OfflinePlayer reportedPlayer = ticket.getReportedPlayer();
            List<String> lore = generateLore(ticketUUID,ticket);

            if(ticket.isAvailableExpire(ticketSystem)) {
                lore.add("");
                lore.add("<red><bold>THIS TICKET IS OVER 30 DAYS OLD!</red>");
            }

            ticketItems.add(ItemBuilder.build(Material.PLAYER_HEAD, builder -> {

                builder.setHead(reportedPlayer);
                builder.setDisplayName("<yellow>" + reportedPlayer.getName());

                builder.setLore(lore);
            }));
        }
        this.stacks(ticketItems);
    }

    @Override
    public void loadItems() {
        super.loadItems();
    }

    @Override
    public void loadButtons() {
        super.loadButtons();
    }

    public void update() {
        userInterfaceActions.clear();

        List<ItemStack> ticketItems = new ArrayList<>();

        for (TicketReference ticketReference : ticketStorage.getAllTickets()) {
            UUID ticketUUID = ticketReference.uuid();
            Ticket ticket = ticketReference.reference();

            OfflinePlayer reportedPlayer = ticket.getReportedPlayer();
            List<String> lore = generateLore(ticketUUID,ticket);

            if(ticket.isAvailableExpire(ticketSystem)) {
                lore.add("");
                lore.add("<red><bold>THIS TICKET IS OVER 30 DAYS OLD!</red>");
            }

            ticketItems.add(ItemBuilder.build(Material.PLAYER_HEAD, builder -> {

                builder.setHead(reportedPlayer);
                builder.setDisplayName("<yellow>" + reportedPlayer.getName());

                builder.setLore(lore);
            }));
        }
        this.stacks(ticketItems);
        for (Map.Entry<UUID, IUserInterface> entry : playerManager.entries()) {
            UUID player = entry.getKey();
            IUserInterface userInterface = entry.getValue();
            if(!(userInterface instanceof AllTicketsUserInterface)) continue;

            this.open(Bukkit.getPlayer(player));
        }
    }

    private static @NotNull List<String> generateLore(UUID ticketUUID,Ticket ticket) {
        OfflinePlayer reporterPlayer = ticket.getReporterPlayer();

        Timestamp timestamp = ticket.reportStamp();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");

        String description = ticket.description();

        if (description.length() > 16)
            description = description.substring(0, 16) + "...";

        List<String> lore = Arrays.asList(
                "<gray>UUID:</gray> <yellow>%s</yellow>".formatted(ticketUUID.toString()),
                "<gray>Reported:</gray> <yellow>%s</yellow>".formatted(dateFormat.format(timestamp)),
                "",
                "<gray>Reasons:</gray> <green>%s</green>".formatted(Arrays.toString(ticket.reasons())),
                "<gray>Description: <white>\"<aqua>%s</aqua>\"</white>".formatted(description),
                "",
                "<gray>Reported By:</gray> <gold>%s</gold>".formatted(reporterPlayer.getName()),
                "<yellow>Click to see full report.</yellow>"
        );
        return lore;
    }

}
