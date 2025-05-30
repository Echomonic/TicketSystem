package me.themoonis.ticketSystem.ui;

import me.themoonis.ticketSystem.ui.api.*;
import me.themoonis.ticketSystem.ui.button.IUserInterfaceButton;
import me.themoonis.ticketSystem.ui.cosmetic.UserInterfaceCosmeticsImpl;
import me.themoonis.ticketSystem.ui.managers.impl.PlayerManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractUserInterface implements IUserInterface, IUserInterfaceButtonStorage, IUserInterfaceCloseAction, IUserInterfaceDragAction {

    private final PlayerManager playerManager;
    private final Inventory inventory;

    protected IUserInterfacePhysical userInterfaceActions;
    protected UIButtonHandler userInterfaceButtons;
    protected IUserInterfaceCosmetic userInterfaceCosmetics;
    private Consumer<InventoryClickEvent> clickEventConsumer;
    private Consumer<InventoryCloseEvent> closeEventConsumer;
    private Consumer<InventoryDragEvent> dragEventConsumer;

    public AbstractUserInterface(PlayerManager playerManager, UserInterfaceData<?> data) {
        this.playerManager = playerManager;

        Component title = data.isContextObjectInstance("title", String.class) ? Component.text(data.getContextObject("title", String.class)) : data.getContextObject("title", Component.class);
        int size = data.isContextObjectInstance("size", Integer.class) ? data.getContextObject("size", Integer.class) : 27;
        InventoryType type = data.isContextObjectInstance("type", InventoryType.class) ? data.getContextObject("type", InventoryType.class) : InventoryType.CHEST;

        this.inventory = type == InventoryType.CHEST ? Bukkit.createInventory(this, size, title) : Bukkit.createInventory(this, type, title);

        this.userInterfaceActions = new UserInterfaceActions(inventory);
        this.userInterfaceCosmetics = new UserInterfaceCosmeticsImpl(userInterfaceActions);
        this.userInterfaceButtons = new UIButtonHandler();

        data.clearContextObjects();

        if (autoLoad())
            miscLoad();

    }

    protected void miscLoad() {
        loadButtons();
        loadItems();
        userInterfaceButtons.updateButtons(this);
    }

    protected boolean autoLoad() {

        return true;
    }

    @Override
    public void close(Consumer<InventoryCloseEvent> eventConsumer) {
        this.closeEventConsumer = eventConsumer;
    }

    @Override
    public void open(Player player) {
        addPlayer(player.getUniqueId());
        player.openInventory(inventory);
    }

    @Override
    public void click(Consumer<InventoryClickEvent> eventConsumer) {
        this.clickEventConsumer = eventConsumer;
    }

    @Override
    public void addPlayer(UUID uuid) {
        if (!playerManager.exists(uuid))
            playerManager.add(uuid, this);
    }

    @Override
    public void removePlayer(UUID uuid) {
        if (playerManager.exists(uuid))
            playerManager.remove(uuid);
    }

    @Override
    public void close(Player player) {
        player.closeInventory();
    }


    public void run(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        //if (event.getCurrentItem() == null) return;
        int slot = event.getSlot();

        if (userInterfaceButtons.exists(slot)) {
            IUserInterfaceButton button = userInterfaceButtons.get(slot);
            button.run(event);
            event.setCancelled(true);
            return;
        }

        if (clickEventConsumer != null)
            clickEventConsumer.accept(event);
    }

    @Override
    public void run(InventoryCloseEvent event) {
        if (closeEventConsumer == null) return;
        closeEventConsumer.accept(event);
        this.removePlayer(event.getPlayer().getUniqueId());
    }

    @Override
    public void drag(Consumer<InventoryDragEvent> eventConsumer) {
        this.dragEventConsumer = eventConsumer;
    }

    @Override
    public void run(InventoryDragEvent event) {
        if(dragEventConsumer == null) return;
        dragEventConsumer.accept(event);
    }


    @Override
    public void unload() {
        userInterfaceActions.clear();
        userInterfaceActions = null;

        userInterfaceButtons.clear();
        userInterfaceButtons = null;

        System.gc();
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public IUserInterfacePhysical getUserInterfaceActions() {
        return userInterfaceActions;
    }

    public record UserInterfaceActions(Inventory inventory) implements IUserInterfacePhysical {

        @Override
        public void clear() {
            inventory.clear();
        }

        @Override
        public int size() {
            return inventory.getSize();
        }

        @Override
        public void setItem(ItemStack stack, int slot) {
            inventory.setItem(slot, stack);
        }

        @Override
        public void addItem(ItemStack... stacks) {
            inventory.addItem(stacks);
        }

        @Override
        public Optional<ItemStack> getItem(int slot) {
            return Optional.ofNullable(inventory.getItem(slot));
        }

        @Override
        public void clearItem(int slot) {
            setItem(new ItemStack(Material.AIR), slot);
        }
    }
}
