package ru.violence.nomemleak;

import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotConfig;
import ru.violence.nomemleak.task.AACTask;
import ru.violence.nomemleak.task.CriterionTriggersTask;
import ru.violence.nomemleak.task.EntityTrackerTask;
import ru.violence.nomemleak.task.WorldGenMineshaftTask;

public class NoMemLeakPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            new EntityTrackerTask(this).runTaskTimer(this, 20, 5 * 60 * 20);
            new WorldGenMineshaftTask(this).runTaskTimer(this, 20, 5 * 60 * 20);
            if (!SpigotConfig.disabledAdvancements.contains("*")) {
                new CriterionTriggersTask(this).runTaskTimer(this, 20, 5 * 60 * 20);
            }

            if (getServer().getPluginManager().isPluginEnabled("AAC")
                    && getServer().getPluginManager().getPlugin("AAC").getDescription().getVersion().equals("4.4.2")) {
                new AACTask(this).runTaskTimerAsynchronously(this, 20, 5 * 60 * 20);
            }
            if (getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
                new WorldGuardMetaTask(this).runTaskTimer(this, 20, 5 * 60 * 20);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            setEnabled(false);
        }
    }

    public void logCleared(String name, int amount) {
        if (amount > 0) {
            getLogger().info("Cleared for " + name + ": " + amount);
        }
    }
}
