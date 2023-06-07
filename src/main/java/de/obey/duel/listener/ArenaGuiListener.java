package de.obey.duel.listener;
/*

    Author - Obey -> ChampDuel
       06.06.2023 / 00:26

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.handler.ArenaHandler;
import de.obey.duel.utils.InventoryUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
public final class ArenaGuiListener implements Listener {

    private final ArenaHandler arenaHandler;

    @EventHandler
    public void on(final InventoryClickEvent event) {

        if(!InventoryUtil.isInventoryTitle(event.getInventory(), "§6§lMaps"))
            return;

        event.setCancelled(true);

        if(!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§6§lMaps"))
            return;

        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        final Player player = (Player) event.getWhoClicked();

        final String arenaName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

        player.teleport(arenaHandler.getArena(arenaName).getLocationSpec());
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 0.5f, 1);


    }

}
