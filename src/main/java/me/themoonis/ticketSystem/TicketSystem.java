package me.themoonis.ticketSystem;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import me.themoonis.ticketSystem.file.json.YamlFile;
import me.themoonis.ticketSystem.files.TicketStorageFile;
import me.themoonis.ticketSystem.report.Ticket;
import me.themoonis.ticketSystem.report.TicketReference;
import me.themoonis.ticketSystem.report.TicketStorage;
import me.themoonis.ticketSystem.ui.impl.AllTicketsUserInterface;
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

    @Getter
    private YamlFile configYaml;

    @Override
    public void onEnable() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.DAY_OF_MONTH, -30);
        expiryTimeStamp = new Timestamp(cal.getTimeInMillis());

        if(!getDataFolder().exists())
            getDataFolder().mkdirs();

        this.configYaml = YamlFile.create(getDataFolder(),"config");

        this.playerManager = new PlayerManager(this);
        this.userInterfaceManager = new UserInterfaceManager();

        ticketStorage = new TicketStorage(this);
        ticketStorageFile = new TicketStorageFile(this);

        ticketStorageFile.load();
        ticketStorageFile.read();

        configYaml.load();

        userInterfaceManager.add(AllTicketsUserInterface.class, new AllTicketsUserInterface(this));

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

                        if (!(sender instanceof Player player)) {
                            sender.sendMessage(Colorful.plugin("<red>You must be a player to perform this command."));
                            return 0;
                        }
                        final PlayerSelectorArgumentResolver targetResolver = context.getArgument("target", PlayerSelectorArgumentResolver.class);
                        final Player target = targetResolver.resolve(source).getFirst();


                        return Command.SINGLE_SUCCESS;
                    }))).then(literal("view")
                            .requires(source -> source.getSender().hasPermission("ticketsystem.ticket.admin.view")).executes(context -> {
                                CommandSender sender = context.getSource().getSender();

                                if(!(sender instanceof Player player)){
                                    sender.sendMessage(Colorful.plugin("<red>You must be a player to perform this command."));
                                    return 0;
                                }
                                userInterfaceManager.get(AllTicketsUserInterface.class).open(player);
                                return Command.SINGLE_SUCCESS;
                            }))
                    .build());

        });


        Bukkit.getPluginManager().registerEvents(new InventoryListener(playerManager), this);
    }

    @Override
    public void onDisable() {
        expiryTimeStamp = null;

        configYaml.unload();

        playerManager.clear();
        userInterfaceManager.clear();

        ticketStorage.save();
        ticketStorage.clear();

        ticketStorageFile.write();
    }
}
