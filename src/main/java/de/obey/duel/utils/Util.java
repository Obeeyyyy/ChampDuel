package de.obey.duel.utils;
/*

    Author - Obey -> ClanWars
       24.04.2023 / 20:35

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@UtilityClass
public final class Util {

    public void console(final String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void sendMessage(final Player player, final String message) {
        player.sendMessage("§6§lDUEL §8»§7 " + ChatColor.translateAlternateColorCodes('&', message));
    }

    public void sendSyntax(final Player player, final String... lines) {
        final String prefix ="§6§lDUEL §8»§7 ";
        player.sendMessage(prefix + "Bitte nutze§8:");

        for (final String line : lines)
            player.sendMessage("§8 -§7" + ChatColor.translateAlternateColorCodes('&', line));

    }

    public String getTimeFromMillis(long millis) {
        int minutes = 0;
        int seconds = 0;

        while (millis >= 1000) {
            seconds++;
            millis -= 1000;
        }

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        return (minutes > 0 ? minutes + "min " : "") +(seconds > 0 ? seconds + "sek " : "");
    }

}
