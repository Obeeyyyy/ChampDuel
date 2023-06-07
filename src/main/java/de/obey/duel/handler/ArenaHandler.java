package de.obey.duel.handler;
/*

    Author - Obey -> ChampDuel
       05.06.2023 / 19:03

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.objects.Arena;
import de.obey.duel.utils.InventoryUtil;
import de.obey.duel.utils.ItemBuilder;
import de.obey.duel.utils.LocationUtil;
import de.obey.duel.utils.Util;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ArenaHandler {

    private final File file;
    private final YamlConfiguration cfg;

    @Getter
    private final Map<String, Arena> arenas = new HashMap<>();

    @Getter
    private final Inventory arenaInventory = Bukkit.createInventory(null, 9*5, "§6§lMaps");

    public ArenaHandler(final String path) {
        file = new File(path + "/arenas.yml");
        cfg = YamlConfiguration.loadConfiguration(file);

        if(!file.exists()) {
            try {
                file.createNewFile();

                save();
            } catch (final IOException ignored) {}
            return;
        }

        loadArenas();
        updateArenaInventory();
    }

    public Arena getFreeArena() {
        Arena found = null;

        for (final Arena map : arenas.values()) {
            if(map.getState() == 0) {
                found = map;
                break;
            }
        }

        return found;
    }

    private void loadArenas() {
        if(!cfg.contains("arena"))
            return;

        for (String arenaName : cfg.getConfigurationSection("arena").getKeys(false)) {
            arenas.put(arenaName, new Arena(arenaName, cfg));
            Util.console("§8- §a§oloaded arena §f" + arenaName);
        }
    }

    public boolean createNewArena(final String name) {

        if(arenas.containsKey(name))
            return false;

        cfg.set("arena." + name + ".locationOne", LocationUtil.encode(Bukkit.getWorlds().get(0).getSpawnLocation()));
        cfg.set("arena." + name + ".locationTwo", LocationUtil.encode(Bukkit.getWorlds().get(0).getSpawnLocation()));
        cfg.set("arena." + name + ".locationSpec", LocationUtil.encode(Bukkit.getWorlds().get(0).getSpawnLocation()));
        cfg.set("arena." + name + ".showMaterial", Material.GLASS_BOTTLE.name());
        cfg.set("arena." + name + ".showSlot", 10);

        save();

        arenas.put(name, new Arena(name, cfg));

        updateArenaInventory();

        return true;
    }

    public boolean deleteArena(final String name){

        if(!arenas.containsKey(name))
            return false;

        arenas.remove(name);

        cfg.set("arena." + name, null);

        save();
        updateArenaInventory();

        return true;
    }

    public boolean exist(final String name) {
        return arenas.containsKey(name);
    }

    public Arena getArena(final String name) {
        return arenas.get(name);
    }

    public void updateArenaInventory() {
        arenaInventory.clear();

        InventoryUtil.fill(arenaInventory, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setDisplayname("§7-§8/-§7").build());

        if(arenas.isEmpty())
            return;

        arenas.values().forEach(arena ->
                arenaInventory.setItem(arena.getShowSlot(),
                new ItemBuilder(arena.getShowMaterial())
                        .setDisplayname("§f§l" + arena.getArenaName())
                        .setLore("",
                                "§8» §7Status§8: " + (arena.getState() == 0 ? "§afrei" : "§cbesetzt"),
                                "",
                                "§8» §7Linksklick§8: §f§oTeleportiert dich auf einen Zuschauerplatz§8.",
                                ""
                        )
                        .build()));
    }

    public void saveArena(final String name) {
        if(!exist(name))
            return;

        final Arena arena = arenas.get(name);

        cfg.set("arena." + name + ".locationOne", LocationUtil.encode(arena.getLocationOne()));
        cfg.set("arena." + name + ".locationTwo", LocationUtil.encode(arena.getLocationTwo()));
        cfg.set("arena." + name + ".locationSpec", LocationUtil.encode(arena.getLocationSpec()));
        cfg.set("arena." + name + ".showMaterial", arena.getShowMaterial().name());
        cfg.set("arena." + name + ".showSlot", arena.getShowSlot());

        save();
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (final IOException ignored) {}
    }

}
