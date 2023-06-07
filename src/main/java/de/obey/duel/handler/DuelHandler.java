package de.obey.duel.handler;
/*

    Author - Obey -> ChampDuel
       06.06.2023 / 00:00

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.Init;
import de.obey.duel.objects.Arena;
import de.obey.duel.objects.Duel;
import de.obey.duel.objects.Queue;
import de.obey.duel.utils.ItemBuilder;
import de.obey.duel.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class DuelHandler {

    private Queue queue;
    private final ArenaHandler arenaHandler;

    @Getter
    private final HashMap<Player, Duel> duels = new HashMap<>();

    @Getter
    private final HashMap<Player, Player> duelPartners = new HashMap<>();
    private final HashMap<Player, Player> duelRequests = new HashMap<>();
    private final HashMap<Player, Inventory> invCache = new HashMap<>();

    @Getter
    private final ArrayList<Player> partners = new ArrayList<>();

    @Getter @Setter
    private ItemStack helmet = null, chestplate = null, leggings = null, boots = null;

    @Getter @Setter
    private ArrayList<ItemStack> items = new ArrayList<>();

    private final File file;
    @Getter
    private final YamlConfiguration cfg;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DuelHandler(final ArenaHandler arenaHandler) {
        this.arenaHandler = arenaHandler;

        new BukkitRunnable() {
            @Override
            public void run() {
                queue = Init.getInstance().getQueue();
            }
        }.runTaskLater(Init.getInstance(), 10);

        file = new File(Init.getInstance().getDataFolder().getPath() + "/items.yml");
        cfg = YamlConfiguration.loadConfiguration(file);

        if(!file.exists()) {
            try {
                file.createNewFile();
                saveCfg();
            } catch (final IOException ignored) {}
        } else {
            helmet = cfg.getItemStack("helmet");
            chestplate = cfg.getItemStack("chestplate");
            leggings = cfg.getItemStack("leggings");
            boots = cfg.getItemStack("boots");
            items = (ArrayList<ItemStack>) cfg.getList("items");
            saveCfg();
        }
    }

    public void saveCfg() {
        executor.submit(() -> {
            try {
                cfg.set("helmet", helmet);
                cfg.set("chestplate", chestplate);
                cfg.set("leggings", leggings);
                cfg.set("boots", boots);
                cfg.set("items", items);
                cfg.save(file);
            } catch (final IOException ignored) {
            }
        });
    }

    public boolean isDuelPartner(final Player player) {
        return partners.contains(player);
    }

    public void accepptRequest(final Player player, final Player from) {
        if(!duelRequests.containsKey(from) ||  duelRequests.get(from) != player) {
            Util.sendMessage(player, "Du hast keine Duelanfrage von " + from.getName() + "§8.");
            return;
        }

        duelRequests.remove(from);
        duelPartners.put(from, player);
        partners.add(player);

        Util.sendMessage(player, "Du hast die Duelanfrage von " + from.getName() + " §aangenommen§8.");
        Util.sendMessage(from, player.getName() + " hat deine Duelanfrage §aangenommen§8.");

        queue.joinQueue(from);
    }

    public void denyRequest(final Player player, final Player from) {
        if(!duelRequests.containsKey(from) ||  duelRequests.get(from) != player) {
            Util.sendMessage(player, "Du hast keine Duelanfrage von " + from.getName() + "§8.");
            return;
        }

        duelRequests.remove(from);

        Util.sendMessage(player, "Du hast die Duelanfrage von " + from.getName() + " §cabgelehnt§8.");
        Util.sendMessage(from, player.getName() + " hat deine Duelanfrage §cabgelehnt§8.");
    }

    public void sendDuelRequest(final Player to, final Player from) {
        if(duelRequests.containsKey(from) && duelRequests.get(from) == to) {
            Util.sendMessage(from, "Du hast " + to.getName() + " bereits eine Duelanfrage gesendet§8.");
            return;
        }

        duelRequests.put(from, to);
        Util.sendMessage(from, "Du hast §a" + to.getName() + "§7 eine Duelanfrage gesendet§8.");
        Util.sendMessage(to, "§a" + from.getName() + " §7hat dir eine Duelanfrage gesendet§8.");
        Util.sendMessage(to, "Nutze §a/duel accept " + from.getName() + " §7 oder §c/duel deny " + from.getName() + "§8.");
    }

    public boolean hasDuelPartner(final Player player) {
        return duelPartners.containsKey(player);
    }

    public Player getDuelPartner(final Player player) {
        return duelPartners.get(player);
    }

    public Duel getPlayerDuel(final Player player) {
        return duels.get(player);
    }

    public boolean isInDuel(final Player player) {
        return duels.containsKey(player);
    }

    public void startNewDuel(final Arena map, final Player one, final Player two) {
        if(isInDuel(one) || isInDuel(two))
            return;

        if(hasDuelPartner(one)) {
            duelPartners.remove(one);
            partners.remove(two);
        }

        queue.getQueue().remove(one);
        queue.getQueue().remove(two);

        final Duel duel = new Duel(map, one, two);

        duels.put(one, duel);
        duels.put(two, duel);

        equip(one);
        equip(two);
    }

    private void equip(final Player player) {
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        final AtomicInteger slot = new AtomicInteger();

        if(!items.isEmpty()) {
            items.forEach(item -> {
                if(item == null) {
                    slot.getAndIncrement();
                } else {
                    player.getInventory().setItem(slot.getAndIncrement(), item);
                }
            });
        }
    }

    public void openDuelGUI(final Player player) {
        final Inventory inv = invCache.containsKey(player) ? invCache.get(player) : Bukkit.createInventory(null, 9*3, "§6§lDuel");

      updateInventory(inv, player);

        if(!invCache.containsKey(player))
            invCache.put(player, inv);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
    }

    public void updateInventory(final Inventory inv, final Player player) {
        executor.submit(() -> {
            inv.setItem(4, new ItemBuilder(Material.SIGN)
                    .setDisplayname("§8» §7Informationen")
                    .setLore("",
                            "§8× §7Spieler in der Warteschlange§8: §f" + queue.getQueue().size(),
                            "§8× §7Arenen §8: §f" + arenaHandler.getArenas().size(),
                            "")
                    .build());

            if(!queue.isInQueue(player)) {
                inv.setItem(11, new ItemBuilder(Material.INK_SACK, 1, (byte) 10)
                        .setDisplayname("§8» §a§lQueue betreten")
                        .setLore("",
                                "§8» §7Linksklick§8: §aTrete der Warteschlange bei§8.",
                                "")
                        .build());
            } else {
                inv.setItem(11, new ItemBuilder(Material.INK_SACK, 1, (byte) 1)
                        .setDisplayname("§8» §c§lQueue verlassen")
                        .setLore("",
                                "§8» §7Linksklick§8: §cVerlasse die Warteschlange§8.",
                                "")
                        .build());
            }

            inv.setItem(15, new ItemBuilder(Material.GRASS)
                    .setDisplayname("§8» §2§lMaps")
                    .setLore("",
                            "§8» §7Linksklick§8: §2§fÖffnet das Map GUI§8.",
                            "")
                    .build());
        });
    }

}
