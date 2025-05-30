package me.themoonis.ticketSystem.ui.cosmetic;

import me.themoonis.ticketSystem.ui.api.IUserInterfaceCosmetic;
import me.themoonis.ticketSystem.ui.api.IUserInterfacePhysical;
import org.bukkit.inventory.ItemStack;

public record UserInterfaceCosmeticsImpl(IUserInterfacePhysical actions) implements IUserInterfaceCosmetic {
    @Override
    public void border(ItemStack stack) {
        for (int i = 0; i < actions.size(); i++) {
            if ((i <= 8) || (i >= actions.size() - 9) // bottom and top
                    || i == 9 || i == 18 // borders
                    || i == 27 || i == 36
                    || i == 17 || i == 26
                    || i == 35 || i == 44)
                actions.setItem(stack, i);
        }
    }

    @Override
    public void fill(ItemStack stack) {
        for(int i = 0; i < actions.size(); i++){
            if(actions.getItem(i).isPresent()) continue;
            actions.setItem(stack,i);
        }
    }
}
