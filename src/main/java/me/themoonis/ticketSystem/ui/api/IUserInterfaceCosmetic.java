package me.themoonis.ticketSystem.ui.api;

import me.themoonis.ticketSystem.ui.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public interface IUserInterfaceCosmetic {

    void border(ItemStack stack);

    void fill(ItemStack stack);

    default void border(Material material) {
        ItemStack stack = ItemBuilder.build(material, itemBuilder -> {
            itemBuilder.setDisplayName("");
            itemBuilder.addItemFlags(ItemFlag.values());
        });

        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(NamespacedKey.minecraft("border"), PersistentDataType.INTEGER, 1);
        meta.setHideTooltip(true);
        stack.setItemMeta(meta);
        border(stack);
    }
    default void fill(Material material){
        ItemStack stack = ItemBuilder.build(material, itemBuilder -> {
            itemBuilder.setDisplayName("");
            itemBuilder.addItemFlags(ItemFlag.values());
        });

        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(NamespacedKey.minecraft("border"), PersistentDataType.INTEGER, 1);
        meta.setHideTooltip(true);

        stack.setItemMeta(meta);
        fill(stack);
    }
}

