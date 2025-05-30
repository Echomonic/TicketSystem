package me.themoonis.ticketSystem.ui.listener;

import lombok.RequiredArgsConstructor;
import me.themoonis.ticketSystem.ui.api.IUserInterface;
import me.themoonis.ticketSystem.ui.api.IUserInterfaceCloseAction;
import me.themoonis.ticketSystem.ui.api.IUserInterfaceDragAction;
import me.themoonis.ticketSystem.ui.managers.impl.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@RequiredArgsConstructor
public class InventoryListener implements Listener {

    private final PlayerManager playerManager;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        InventoryHolder holder = event.getClickedInventory().getHolder();

        if (holder instanceof IUserInterface userInterface)
            userInterface.run(event);

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getInventory().getHolder() instanceof IUserInterface userInterface)
            userInterface.addPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof IUserInterface userInterface) {
            if (userInterface instanceof IUserInterfaceCloseAction closeAction) {
                closeAction.run(event);
                return;
            }
            userInterface.removePlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        if(playerManager.exists(player.getUniqueId()))
            playerManager.remove(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof IUserInterface userInterface)) return;
        if (!(userInterface instanceof IUserInterfaceDragAction dragAction)) return;

        dragAction.run(event);
    }
}

