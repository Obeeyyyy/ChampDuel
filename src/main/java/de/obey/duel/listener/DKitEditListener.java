package de.obey.duel.listener;
/*

    Author - Obey -> ChampDuel
       07.06.2023 / 16:48

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.handler.DuelHandler;
import de.obey.duel.utils.InventoryUtil;
import de.obey.duel.utils.Util;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

@RequiredArgsConstructor
public final class DKitEditListener implements Listener {

    private final DuelHandler duelHandler;

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if(event.getInventory().getTitle() == null)
            return;

        if(!InventoryUtil.isInventoryTitle(event.getInventory(), "§9Obey was here"))
            return;

        final ArrayList<ItemStack> temp = new ArrayList<>();

        Collections.addAll(temp, event.getInventory().getContents());

        duelHandler.setItems(temp);
        duelHandler.saveCfg();

        Util.sendMessage((Player) event.getPlayer(), "DKit Items gesetzt§8.");

    }

}
