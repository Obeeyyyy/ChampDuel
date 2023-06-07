package de.obey.duel.utils;
/*

    Author - Obey -> ChampDuel
       05.06.2023 / 19:13

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@UtilityClass
public final class LocationUtil {

    public Location decode(final String crypt) {

        final String[] cryptedData = crypt.split("#");
        final Location location = new Location(Bukkit.getWorld(cryptedData[0]),
                Double.parseDouble(cryptedData[1]),
                Double.parseDouble(cryptedData[2]),
                Double.parseDouble(cryptedData[3]),
                Float.parseFloat(cryptedData[4]),
                Float.parseFloat(cryptedData[5]));

        if(location.getWorld() == null)
            return null;

        return location;
    }

    public String encode(final Location location) {
        return location.getWorld().getName() +  "#" + location.getX() + "#" + location.getY() + "#" + location.getZ() + "#" + location.getYaw() + "#" + location.getPitch();
    }

}
