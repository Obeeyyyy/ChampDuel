package de.obey.duel.objects;
/*

    Author - Obey -> ChampDuel
       02.06.2023 / 16:22

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.Init;
import de.obey.duel.utils.InventoryUtil;
import de.obey.duel.utils.Util;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@Getter
public final class Duel {

    private int state = 0; // 0 = waiting - 1 = running

    private BukkitTask runnable;
    private long startedMillis;

    private final Location playerOnePreDuelLocation, playerTwoPreDuelLocation;
    private final Player one, two;

    public Duel(final Arena map, final Player one, final Player two) {
        this.one = one;
        this.two = two;

        playerOnePreDuelLocation = one.getLocation();
        playerTwoPreDuelLocation = two.getLocation();

        readyUp(one);
        readyUp(two);

        one.teleport(map.getLocationOne());
        two.teleport(map.getLocationTwo());

        sound(Sound.ENDERMAN_TELEPORT, 1, .5f);

        runnable = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if(ticks == 3) {
                    state = 1;
                    startedMillis = System.currentTimeMillis();
                    message("Der Kampf kann beginnen§8, §7viel Glück§8!");
                    cancel();
                    return;
                }

                message("Das Duel beginnt in " + (3 - ticks) + "§8.");
                sound(Sound.CHICKEN_EGG_POP, 1, .5f);

                ticks++;
            }
        }.runTaskTimer(Init.getInstance(), 20, 20);
    }

    public void death(final Player player) {
        final Player winner = (player == one ? two : one);

        InventoryUtil.clearInventoryAndArmor(winner);

        readyUp(one);
        readyUp(two);

        one.teleport(playerOnePreDuelLocation);
        two.teleport(playerTwoPreDuelLocation);

        sound(Sound.ENDERDRAGON_HIT, .5f, 1);
        message("Das Duel ist beendet§8,§7 gewonnen hat §a§o" + winner.getName() + "§7 in");
        message("§8 ->§7 " + Util.getTimeFromMillis(System.currentTimeMillis() - startedMillis));

        Init.getInstance().getDuelHandler().getDuels().remove(one);
        Init.getInstance().getDuelHandler().getDuels().remove(two);
    }

    private void readyUp(final Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.getActivePotionEffects().clear();
    }

    private void sound(final Sound sound, final float volume, final float pitch) {
        one.playSound(one.getLocation(), sound, volume, pitch);
        two.playSound(two.getLocation(), sound, volume, pitch);
    }

    private void message(final String message) {
        Util.sendMessage(one, message);
        Util.sendMessage(two, message);
    }

}
