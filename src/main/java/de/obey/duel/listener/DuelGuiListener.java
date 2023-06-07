package de.obey.duel.listener;
/*

    Author - Obey -> ChampDuel
       06.06.2023 / 00:26

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.handler.ArenaHandler;
import de.obey.duel.handler.DuelHandler;
import de.obey.duel.objects.Queue;
import de.obey.duel.utils.InventoryUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
public final class DuelGuiListener implements Listener {

    private final ArenaHandler arenaHandler;
    private final DuelHandler duelHandler;
    private final Queue queue;

    @EventHandler
    public void on(final InventoryClickEvent event) {

        if(!InventoryUtil.isInventoryTitle(event.getInventory(), "§6§lDuel"))
            return;

        event.setCancelled(true);

        if(!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§6§lDuel"))
            return;

        final Player player = (Player) event.getWhoClicked();

        if(event.getSlot() == 15) {
            player.openInventory(arenaHandler.getArenaInventory());
            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
            return;
        }

        if(event.getSlot() == 11) {
            if(queue.isInQueue(player)) {
                queue.leaveQueue(player);
                duelHandler.updateInventory(event.getInventory(), player);
                return;
            }

            queue.joinQueue(player);
            duelHandler.updateInventory(event.getInventory(), player);
        }

    }

}
