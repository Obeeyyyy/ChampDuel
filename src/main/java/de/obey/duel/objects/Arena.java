package de.obey.duel.objects;
/*

    Author - Obey -> ChampDuel
       02.06.2023 / 16:22

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter @Setter
public final class Arena {

    private final String arenaName;
    private Location locationOne, locationTwo, locationSpec;

    private int state = 0; // 0 = free, 1 = occupied

    private int showSlot = 0;
    private Material showMaterial;

    public Arena(final String arenaName, final YamlConfiguration cfg) {
        this.arenaName = arenaName;

        locationOne = LocationUtil.decode(cfg.getString("arena." + arenaName + ".locationOne"));
        locationTwo = LocationUtil.decode(cfg.getString("arena." + arenaName + ".locationTwo"));
        locationSpec = LocationUtil.decode(cfg.getString("arena." + arenaName + ".locationSpec"));

        showSlot = cfg.getInt("arena." + arenaName + ".showSlot");
        showMaterial = Material.getMaterial(cfg.getString("arena." + arenaName + ".showMaterial"));
    }
}
