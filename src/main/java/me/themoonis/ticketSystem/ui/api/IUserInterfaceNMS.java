package me.themoonis.ticketSystem.ui.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface IUserInterfaceNMS {

    boolean changeTitle(Component title);
    boolean changeTitle(Player entity, Component title);
}

