package ru.violence.nomemleak;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldGuardMetaTask extends BukkitRunnable {
    private final NoMemLeakPlugin plugin;
    private final Plugin worldGuard;
    private final Field field_metadataMap_MetadataStoreBase;

    public WorldGuardMetaTask(NoMemLeakPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        this.field_metadataMap_MetadataStoreBase = Utils.getFieldAccessible(MetadataStoreBase.class, "metadataMap");
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    public void run() {
        Map<String, Map<Plugin, MetadataValue>> metadataMap = (Map<String, Map<Plugin, MetadataValue>>) this.field_metadataMap_MetadataStoreBase.get(((CraftServer) Bukkit.getServer()).getEntityMetadata());
        AtomicInteger cleared = new AtomicInteger();

        metadataMap.entrySet().removeIf((Map.Entry<String, Map<Plugin, MetadataValue>> entry) -> {
            Map<Plugin, MetadataValue> map = entry.getValue();
            if (map.get(this.worldGuard) == null) return false;

            if (Bukkit.getEntity(UUID.fromString(entry.getKey().substring(0, 36))) == null) {
                cleared.incrementAndGet();
                map.remove(this.worldGuard);
                return map.isEmpty();
            }

            return false;
        });

        this.plugin.logCleared("WorldGuardMetadata", cleared.get());
    }
}
