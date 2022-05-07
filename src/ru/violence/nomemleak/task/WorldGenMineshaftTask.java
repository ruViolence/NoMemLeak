package ru.violence.nomemleak.task;

import co.aikar.timings.TimedChunkGenerator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import lombok.SneakyThrows;
import net.minecraft.server.v1_12_R1.ChunkGenerator;
import net.minecraft.server.v1_12_R1.ChunkProviderGenerate;
import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import net.minecraft.server.v1_12_R1.PersistentStructure;
import net.minecraft.server.v1_12_R1.StructureGenerator;
import net.minecraft.server.v1_12_R1.StructureStart;
import net.minecraft.server.v1_12_R1.World;
import net.minecraft.server.v1_12_R1.WorldGenMineshaft;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.generator.InternalChunkGenerator;
import org.bukkit.craftbukkit.v1_12_R1.generator.NormalChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;
import ru.violence.nomemleak.NoMemLeakPlugin;
import ru.violence.nomemleak.Utils;

import java.lang.reflect.Field;

public class WorldGenMineshaftTask extends BukkitRunnable {
    private final NoMemLeakPlugin plugin;
    private final Field field_chunkGenerator_ChunkProviderServer;
    private final Field field_timedGenerator_TimedChunkGenerator;
    private final Field field_generator_NormalChunkGenerator;
    private final Field field_y_ChunkProviderGenerate;
    private final Field field_a_StructureGenerator;
    private final Field field_c_StructureGenerator;
    private final Field field_allStructures_StructureGenerator;

    public WorldGenMineshaftTask(NoMemLeakPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.field_chunkGenerator_ChunkProviderServer = Utils.getFieldAccessible(ChunkProviderServer.class, "chunkGenerator");
        this.field_timedGenerator_TimedChunkGenerator = Utils.getFieldAccessible(TimedChunkGenerator.class, "timedGenerator");
        this.field_generator_NormalChunkGenerator = Utils.getFieldAccessible(NormalChunkGenerator.class, "generator");
        this.field_y_ChunkProviderGenerate = Utils.getFieldAccessible(ChunkProviderGenerate.class, "y");
        this.field_a_StructureGenerator = Utils.getFieldAccessible(StructureGenerator.class, "a");
        this.field_c_StructureGenerator = Utils.getFieldAccessible(StructureGenerator.class, "c");
        this.field_allStructures_StructureGenerator = Utils.getFieldAccessible(StructureGenerator.class, "allStructures");
    }

    @Override
    @SneakyThrows
    public void run() {
        int removed = 0;

        for (org.bukkit.World bukkitWorld : Bukkit.getWorlds()) {
            WorldServer world = ((CraftWorld) bukkitWorld).getHandle();
            ChunkProviderServer chunkProvider = (ChunkProviderServer) ((World) world).getChunkProvider();
            TimedChunkGenerator timedChunkGenerator = (TimedChunkGenerator) this.field_chunkGenerator_ChunkProviderServer.get(chunkProvider);
            InternalChunkGenerator internalChunkGenerator = (InternalChunkGenerator) this.field_timedGenerator_TimedChunkGenerator.get(timedChunkGenerator);

            // Skip non-normal chunk generators
            if (!(internalChunkGenerator instanceof NormalChunkGenerator)) continue;

            ChunkGenerator chunkGenerator = (ChunkGenerator) this.field_generator_NormalChunkGenerator.get(internalChunkGenerator);

            if (!(chunkGenerator instanceof ChunkProviderGenerate)) continue;

            ChunkProviderGenerate chunkProviderGenerate = (ChunkProviderGenerate) chunkGenerator;
            WorldGenMineshaft worldGenMineshaft = (WorldGenMineshaft) this.field_y_ChunkProviderGenerate.get(chunkProviderGenerate);

            removed += clearStructureGenerator(worldGenMineshaft);
        }

        this.plugin.logCleared("WorldGenMineshaft", removed);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public int clearStructureGenerator(StructureGenerator structureGenerator) {
        int cleared = 0;

        PersistentStructure a = (PersistentStructure) this.field_a_StructureGenerator.get(structureGenerator);
        Long2ObjectMap<StructureStart> c = (Long2ObjectMap<StructureStart>) this.field_c_StructureGenerator.get(structureGenerator);
        Long2ObjectMap<StructureStart> allStructures = (Long2ObjectMap<StructureStart>) this.field_allStructures_StructureGenerator.get(structureGenerator);

        if (a != null) {
            cleared += a.a().map.size();
            a.a().map.clear();
        }
        cleared += c.size();
        cleared += allStructures.size();

        c.clear();
        allStructures.clear();

        return cleared;
    }
}
