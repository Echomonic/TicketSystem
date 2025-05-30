package me.themoonis.ticketSystem.ui.api;

import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.function.Consumer;

public interface IUserInterfaceDragAction {

    void drag(Consumer<InventoryDragEvent> eventConsumer);

    void run(InventoryDragEvent event);

}
