package de.obey.duel.listener;
/*

    Author - Obey -> ChampDuel
       07.06.2023 / 15:17

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.handler.DuelHandler;
import de.obey.duel.objects.Duel;
import de.obey.duel.utils.Util;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public final class InDuelBlockListener implements Listener {

    private final DuelHandler duelHandler;

    @EventHandler
    public void on(final PlayerMoveEvent event) {

        if(!duelHandler.isInDuel(event.getPlayer()))
            return;

        final Duel duel = duelHandler.getPlayerDuel(event.getPlayer());

        if(duel.getState() != 0)
            return;

        final Location to = event.getTo();
        final Location from = event.getFrom();

        if(to.getX() != from.getX() || to.getZ() != from.getZ())
            event.setCancelled(true);

    }

    @EventHandler
    public void on(final PlayerCommandPreprocessEvent event) {

        if(!duelHandler.isInDuel(event.getPlayer()))
            return;

        if(!event.getMessage().startsWith("/"))
            return;

        event.setCancelled(true);
        Util.sendMessage(event.getPlayer(),"Im Duel sind alle Commands deaktiviert§8.");
    }

    @EventHandler
    public void on(final PlayerDropItemEvent event) {
        if(!duelHandler.isInDuel(event.getPlayer()))
            return;

        event.setCancelled(true);
    }


}
