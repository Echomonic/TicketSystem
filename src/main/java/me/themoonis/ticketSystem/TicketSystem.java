package me.themoonis.ticketSystem;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import me.themoonis.ticketSystem.files.TicketStorageFile;
import me.themoonis.ticketSystem.report.Ticket;
import me.themoonis.ticketSystem.report.TicketReference;
import me.themoonis.ticketSystem.report.TicketStorage;
import me.themoonis.ticketSystem.ui.listener.InventoryListener;
import me.themoonis.ticketSystem.ui.managers.impl.PlayerManager;
import me.themoonis.ticketSystem.ui.managers.impl.UserInterfaceManager;
import me.themoonis.ticketSystem.utils.Colorful;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.UUID;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.player;

public final class TicketSystem extends JavaPlugin {

    @Getter
    private Timestamp expiryTimeStamp;

    @Getter
    private TicketStorage ticketStorage;

    @Getter
    private TicketStorageFile ticketStorageFile;

    @Getter
    private PlayerManager playerManager;

    @Getter
    private UserInterfaceManager userInterfaceManager;

    @Override
    public void onEnable() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.DAY_OF_MONTH, -30);
        expiryTimeStamp = new Timestamp(cal.getTimeInMillis());

        this.playerManager = new PlayerManager(this);
        this.userInterfaceManager = new UserInterfaceManager();

        ticketStorage = new TicketStorage(this);
        ticketStorageFile = new TicketStorageFile(this);

        ticketStorageFile.load();
        ticketStorageFile.read();

        LifecycleEventManager<Plugin> eventManager = this.getLifecycleManager();

        final UUID ticketUUID = UUID.fromString("37f27d35-faf0-488c-a21c-b8feb22111d5");
        final UUID reporter = UUID.fromString("fce129c8-c38e-4bbf-995e-fcb3fd1419fd");
        final UUID reported = UUID.fromString("d80f10e4-7c3b-4e9c-b828-bc7f7264142b");

        final String[] reasons = new String[]{"Hacking", "Spamming", "Inappropriate Skin"};
        final String description = "He was bhopping, flying, and using killaura. He also had a naked hitler skin. He was spamming the n word after killing people";

        eventManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();


            commands.register(literal("ticket")
                    .then(literal("report").then(argument("target", player()).executes(context -> {
                        CommandSourceStack source = context.getSource();
                        CommandSender sender = source.getSender();

                        if(!(sender instanceof Player player)){
                            sender.sendMessage(Colorful.plugin("<red>You must be a player to perform this command."));
                            return 0;
                        }
                        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("target", PlayerSelectorArgumentResolver.class);
                        final Player target = targetResolver.resolve(source).getFirst();


                        return Command.SINGLE_SUCCESS;
                    }))).then(literal("test").executes(context -> {
                        CommandSender sender = context.getSource().getSender();
                        ticketStorage.addTicket(ticketUUID, new Ticket(reporter, reported, Timestamp.from(Instant.now()), reasons, description));
                        sender.sendMessage(Colorful.plugin("<green>Successfully added test ticket."));
                        long delay = 40L;

                        Bukkit.getScheduler().runTaskLater(this, () -> {
                            if (!ticketStorage.has(ticketUUID)) {
                                sender.sendMessage(Colorful.plugin("<red>Failed to grab test ticket."));
                                return;
                            }
                            sender.sendMessage(Colorful.plugin("<green>Successfully grabbed test ticket."));
                            sender.sendMessage(Colorful.plugin(ticketStorage.getTicket(ticketUUID).toString()));
                        }, delay);

                        Bukkit.getScheduler().runTaskLater(this, () -> {
                            TicketReference ticketReference = ticketStorage.getTicket(ticketUUID);
                            ticketStorageFile.save(ticketReference);
                            ticketStorageFile.write();
                            sender.sendMessage(Colorful.plugin("<green>Successfully wrote test ticket to json."));
                            sender.sendMessage(Colorful.plugin(ticketStorageFile.getJsonString()));
                        }, delay + 40L);

                        Bukkit.getScheduler().runTaskLater(this, () -> {
                            ticketStorage.removeTicket(ticketUUID);
                            ticketStorageFile.delete(ticketUUID);
                            sender.sendMessage(Colorful.plugin("<green>Successfully deleted test ticket to json."));
                            sender.sendMessage(Colorful.plugin(ticketStorageFile.getJsonString()));
                        }, delay + 80L*2L);

                        return Command.SINGLE_SUCCESS;
                    }))
                    .build());

        });


        Bukkit.getPluginManager().registerEvents(new InventoryListener(),this);
    }

    @Override
    public void onDisable() {
        expiryTimeStamp = null;

        playerManager.clear();
        userInterfaceManager.clear();

        ticketStorage.save();

        ticketStorage.clear();
        ticketStorageFile.write();
    }
}
