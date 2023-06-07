package de.obey.duel.listener;
/*

    Author - Obey -> ChampDuel
       07.06.2023 / 15:47

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.Init;
import de.obey.duel.handler.DuelHandler;
import de.obey.duel.utils.InventoryUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public final class DuelDeathListener implements Listener {

    public final DuelHandler duelHandler;

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if(!duelHandler.isInDuel(player))
            return;

        InventoryUtil.clearInventoryAndArmor(player);
        duelHandler.getPlayerDuel(player).death(player);
    }

    @EventHandler
    public void on(final PlayerDeathEvent event) {

        final Player player = event.getEntity();

        if(!duelHandler.isInDuel(player))
            return;

        event.setDeathMessage("");

        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setKeepLevel(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().respawn();
                duelHandler.getPlayerDuel(player).death(player);
            }
        }.runTaskLater(Init.getInstance(), 5);
    }
}
