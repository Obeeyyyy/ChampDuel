package de.obey.duel.commands;
/*

    Author - Obey -> ChampDuel
       05.06.2023 / 23:58

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.handler.DuelHandler;
import de.obey.duel.objects.Queue;
import de.obey.duel.utils.Util;
import lombok.RequiredArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class DuelCommand implements CommandExecutor {

    private final DuelHandler duelHandler;
    private final Queue queue;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0) {
            duelHandler.openDuelGUI(player);
            return false;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("join")) {

                if(queue.isInQueue(player)) {
                    queue.leaveQueue(player);
                    return false;
                }

                queue.joinQueue(player);
                return false;
            }

            final Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()) {
                Util.sendMessage(player, args[0] + " ist nicht Online§8.");
                return false;
            }

            duelHandler.sendDuelRequest(target, player);
            return false;
        }

        if(args.length == 2) {

            final Player target = Bukkit.getPlayer(args[1]);

            if(target == null || !target.isOnline()) {
                Util.sendMessage(player, args[1] + " ist nicht Online§8.");
                return false;
            }

            if (args[0].equalsIgnoreCase("accept")) {
                duelHandler.accepptRequest(player, target);
                return false;
            }

            if (args[0].equalsIgnoreCase("deny")) {
                duelHandler.denyRequest(player, target);
                return false;
            }
        }

        return false;
    }
}
