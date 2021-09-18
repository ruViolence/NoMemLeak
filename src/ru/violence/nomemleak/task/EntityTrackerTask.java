package ru.violence.nomemleak.task;

import lombok.SneakyThrows;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTrackerEntry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.scheduler.BukkitRunnable;
import ru.violence.nomemleak.NoMemLeakPlugin;
import ru.violence.nomemleak.Utils;

import java.lang.reflect.Field;
import java.util.Set;

public class EntityTrackerTask extends BukkitRunnable {
    private final NoMemLeakPlugin plugin;
    private final Field field_tracker_Entity;

    public EntityTrackerTask(NoMemLeakPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.field_tracker_Entity = Utils.getFieldAccessible(Entity.class, "tracker");
    }

    @Override
    public void run() {
        int removed = 0;
        for (World world : Bukkit.getWorlds()) {
            for (org.bukkit.entity.Entity bukkitEntity : world.getEntities()) {
                Entity entity = ((CraftEntity) bukkitEntity).getHandle();
                Set<EntityPlayer> trackedPlayers = getTracker(entity).trackedPlayers;
                int prevSize = trackedPlayers.size();

                trackedPlayers.removeIf(trackedPlayer -> Bukkit.getPlayer(trackedPlayer.getUniqueID()) == null);

                removed += prevSize - trackedPlayers.size();
            }
        }
        this.plugin.logCleared("EntityTrackerEntry", removed);
    }

    @SneakyThrows
    private EntityTrackerEntry getTracker(Entity entity) {
        return (EntityTrackerEntry) this.field_tracker_Entity.get(entity);
    }
}
