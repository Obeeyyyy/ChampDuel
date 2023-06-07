package de.obey.duel.objects;
/*

    Author - Obey -> ChampDuel
       02.06.2023 / 16:22

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.Init;
import de.obey.duel.handler.ArenaHandler;
import de.obey.duel.handler.DuelHandler;
import de.obey.duel.utils.InventoryUtil;
import de.obey.duel.utils.Util;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public final class Queue {

    private final ArenaHandler arenaHandler;
    private DuelHandler duelHandler;

    @Getter
    private final ArrayList<Player> queue = new ArrayList<>();

    private BukkitTask runnable;
    private long lastStartedQueue = System.currentTimeMillis();

    public Queue(final ArenaHandler arenaHandler) {
        this.arenaHandler = arenaHandler;

        new BukkitRunnable() {
            @Override
            public void run() {
                duelHandler = Init.getInstance().getDuelHandler();
            }
        }.runTaskLater(Init.getInstance(), 10);
    }

    public boolean isInQueue(final Player player) {
        return queue.contains(player);
    }

    public void joinQueue(final Player player) {
        if(duelHandler.isDuelPartner(player)) {
            Util.sendMessage(player, "Du bist ein Duelpartner§8, §7bitte warte bis eine Arena frei ist§8.");
            return;
        }

        if(!InventoryUtil.isEmpty(player)) {
            Util.sendMessage(player, "Bitte leere dein Inventar und deine ArmorSlots§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
            return;
        }

        if(queue.size() == 0 && runnable == null)
            startQueue();

        queue.add(player);

        Util.sendMessage(player, "Du hast die Warteschlange betreten§8. (§f§l" + queue.size() + "§8)");
    }

    public void leaveQueue(final Player player) {
        queue.remove(player);

        if(duelHandler.hasDuelPartner(player)) {
            final Player partner = duelHandler.getDuelPartner(player);

            Util.sendMessage(partner, player.getName() + " hat die Warteschlange §cverlassen §7und das Duel somit §cabgebrochen§8.");
            Util.sendMessage(player, "Du hast die Warteschlange §cverlassen §7und das Duel somit §cabgebrochen§8.");

            duelHandler.getDuelPartners().remove(player);
            duelHandler.getPartners().remove(partner);

            return;
        }

        Util.sendMessage(player, "Du hast die Warteschlange verlassen§8.");
    }

    private void startQueue() {
        if(runnable != null)
            return;

        lastStartedQueue = System.currentTimeMillis();
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(queue.size() == 0 && System.currentTimeMillis() - lastStartedQueue >= 1000*10) {
                    runnable = null;
                    cancel();
                    return;
                }

                if(queue.size() > 0) {
                    final Player one = queue.get(0);
                    Player opponent = null;

                    if(duelHandler.hasDuelPartner(one))
                        opponent = duelHandler.getDuelPartner(one);

                    if(queue.size() < 2 && opponent == null) {
                        queue.forEach(player -> Util.sendMessage(player, "Warte auf Mitspieler §8..."));
                        return;
                    }

                    if(opponent == null)
                        opponent = queue.get(1);

                    final Arena foundMap = arenaHandler.getFreeArena();

                    if(foundMap == null) {
                        queue.forEach(player -> Util.sendMessage(player, "Warte auf Arena §8..."));
                        return;
                    }

                    foundMap.setState(1);
                    duelHandler.startNewDuel(foundMap, one, opponent);
                }
            }
        }.runTaskTimer(Init.getInstance(), 20, 20*3);
    }

}
