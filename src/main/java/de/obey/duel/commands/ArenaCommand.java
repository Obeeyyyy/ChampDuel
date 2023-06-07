package de.obey.duel.commands;
/*

    Author - Obey -> ChampDuel
       05.06.2023 / 19:17

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.handler.ArenaHandler;
import de.obey.duel.objects.Arena;
import de.obey.duel.utils.InventoryUtil;
import de.obey.duel.utils.Util;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class ArenaCommand implements CommandExecutor {

    final ArenaHandler arenaHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(!player.hasPermission("system.admin")) {
            Util.sendMessage(player, "§c§oDu hast keine Rechte dafür§8. (§7system.admin§8)");
            return false;
        }

        if(args.length == 1) {

            if(args[0].equalsIgnoreCase("list")) {

                if(arenaHandler.getArenas().isEmpty()) {
                    Util.sendMessage(player, "Es existieren noch keine Arenen§8.");
                    return false;
                }

                Util.sendMessage(player, "Alle Arenen§8:");

                for (String name : arenaHandler.getArenas().keySet())
                    player.sendMessage("§8- §f" + name);

                return false;
            }

        }

        if(args.length == 2) {
            final String arenaName = args[1].toLowerCase();

            if (args[0].equalsIgnoreCase("create")) {

                if(!arenaHandler.createNewArena(arenaName)) {
                    Util.sendMessage(player, "Es existiert bereits eine Arena mit dem Namen " + arenaName);
                    return false;
                }

                Util.sendMessage(player, arenaName + " wurde erstellt§8.");

                return false;
            }

            if (args[0].equalsIgnoreCase("delete")) {

                if(!arenaHandler.deleteArena(arenaName)) {
                    Util.sendMessage(player, "Es existiert keine Arena mit dem Namen " + arenaName);
                    return false;
                }

                Util.sendMessage(player, arenaName +" wurde gelöscht§8.");

                return false;
            }

            if (args[0].equalsIgnoreCase("setmat")) {

                if(!arenaHandler.exist(arenaName)) {
                    Util.sendMessage(player, "Es existiert keine Arena mit dem Namen " + arenaName);
                    return false;
                }

                if(!InventoryUtil.hasItemInHand(player))
                    return false;

                arenaHandler.getArena(arenaName).setShowMaterial(player.getItemInHand().getType());
                Util.sendMessage(player, "ShowMaterial für " + arenaName + " gesetzt§8.");

                return false;
            }
        }

        if(args.length == 3) {
            final String arenaName = args[1].toLowerCase();

            if (args[0].equalsIgnoreCase("setloc")) {

                if (!arenaHandler.exist(arenaName)) {
                    Util.sendMessage(player, "Es existiert keine Arena mit dem Namen " + arenaName);
                    return false;
                }

                final String locName = args[2].toLowerCase();

                if (!locName.equalsIgnoreCase("locationOne") &&
                        !locName.equalsIgnoreCase("locationTwo") &&
                        !locName.equalsIgnoreCase("locationSpec")) {
                    Util.sendMessage(player, "Bitte gebe eine richtige Location an§8.");
                    Util.sendMessage(player, "locationOne, locationTwo, locationSpec");
                    return false;
                }

                final Arena arena = arenaHandler.getArena(arenaName);

                switch (locName) {
                    case "locationone":
                        arena.setLocationOne(player.getLocation());
                        break;
                    case "locationtwo":
                        arena.setLocationTwo(player.getLocation());
                        break;
                    case "locationspec":
                        arena.setLocationSpec(player.getLocation());
                        break;
                }

                Util.sendMessage(player, locName + " für " + arenaName + " gesetzt§8.");
                arenaHandler.saveArena(arenaName);

                return false;
            }

            if (args[0].equalsIgnoreCase("setshowslot")) {
                if (!arenaHandler.exist(arenaName)) {
                    Util.sendMessage(player, "Es existiert keine Arena mit dem Namen " + arenaName);
                    return false;
                }

                try {
                    final int slot = Integer.parseInt(args[2]);

                    arenaHandler.getArena(arenaName).setShowSlot(slot);
                    arenaHandler.saveArena(arenaName);

                    Util.sendMessage(player, slot + " slot für " + arenaName + " gesetzt§8.");

                } catch (final NumberFormatException exception) {
                    Util.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        Util.sendSyntax(player,
                "/arena list",
                "/arena create <name>",
                "/arena delete <name>",
                "/arena setmat <name>",
                "/arena setloc <name> <location> | locationOne, locationTwo, locationSpec",
                "/arena setshowslot <name> <slot>"
                );

        return false;
    }
}
