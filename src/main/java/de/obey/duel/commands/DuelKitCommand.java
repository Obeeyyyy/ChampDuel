package de.obey.duel.commands;
/*

    Author - Obey -> ChampDuel
       07.06.2023 / 16:41

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.handler.DuelHandler;
import de.obey.duel.utils.InventoryUtil;
import de.obey.duel.utils.Util;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public final class DuelKitCommand implements CommandExecutor {

    private final DuelHandler duelHandler;

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
            if(args[0].equalsIgnoreCase("chest")) {
                if(!InventoryUtil.hasItemInHand(player))
                    return false;

                Util.sendMessage(player, "Chestplate gesetzt§8.");

                duelHandler.setChestplate(player.getItemInHand());
                duelHandler.saveCfg();
                return false;
            }

            if(args[0].equalsIgnoreCase("legs")) {
                if(!InventoryUtil.hasItemInHand(player))
                    return false;

                Util.sendMessage(player, "Hose gesetzt§8.");

                duelHandler.setLeggings(player.getItemInHand());
                duelHandler.saveCfg();
                return false;
            }

            if(args[0].equalsIgnoreCase("boots")) {
                if(!InventoryUtil.hasItemInHand(player))
                    return false;

                Util.sendMessage(player, "Boots gesetzt§8.");

                duelHandler.setBoots(player.getItemInHand());
                duelHandler.saveCfg();
                return false;
            }

            if(args[0].equalsIgnoreCase("helmet")) {
                if(!InventoryUtil.hasItemInHand(player))
                    return false;

                Util.sendMessage(player, "Helm gesetzt§8.");

                duelHandler.setHelmet(player.getItemInHand());
                duelHandler.saveCfg();
                return false;
            }

            if(args[0].equalsIgnoreCase("items")) {

                final Inventory inv = Bukkit.createInventory(null, 9*5, "§9Obey was here");

                final AtomicInteger slot = new AtomicInteger();
                duelHandler.getItems().forEach(item -> inv.setItem(slot.getAndIncrement(), item));

                player.openInventory(inv);

                return false;
            }
        }

        Util.sendSyntax(player, "/dkit helmet",
                "/dkit chest",
                "/dkit legs",
                "/dkit boots",
                "/dkit items");

        return false;
    }
}
