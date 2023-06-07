package de.obey.duel;
/*

    Author - Obey -> ChampDuel
       29.05.2023 / 15:14

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.duel.commands.ArenaCommand;
import de.obey.duel.commands.DuelCommand;
import de.obey.duel.commands.DuelKitCommand;
import de.obey.duel.handler.ArenaHandler;
import de.obey.duel.handler.DuelHandler;
import de.obey.duel.listener.*;
import de.obey.duel.objects.Queue;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Init extends JavaPlugin {

    private ArenaHandler arenaHandler;
    private DuelHandler duelHandler;
    private Queue queue;

    @Override
    public void onEnable() {

        if(!getDataFolder().exists())
            getDataFolder().mkdir();

        arenaHandler = new ArenaHandler(getDataFolder().getPath());
        queue = new Queue(arenaHandler);
        duelHandler = new DuelHandler(arenaHandler);

        loadCommands();
        loadListener();
    }

    private void loadCommands() {
        getCommand("arena").setExecutor(new ArenaCommand(arenaHandler));
        getCommand("duelkit").setExecutor(new DuelKitCommand(duelHandler));
        getCommand("duel").setExecutor(new DuelCommand(duelHandler, queue));
    }

    private void loadListener() {
        final PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new DuelGuiListener(arenaHandler, duelHandler, queue), this);
        pm.registerEvents(new DKitEditListener(duelHandler), this);
        pm.registerEvents(new ArenaGuiListener(arenaHandler), this);
        pm.registerEvents(new InDuelBlockListener(duelHandler), this);
        pm.registerEvents(new DuelDeathListener(duelHandler), this);
    }

    public static Init getInstance() {
        return getPlugin(Init.class);
    }
}
