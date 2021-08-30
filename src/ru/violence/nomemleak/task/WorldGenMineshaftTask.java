package ru.violence.nomemleak.task;

import co.aikar.timings.TimedChunkGenerator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.server.v1_12_R1.ChunkGenerator;
import net.minecraft.server.v1_12_R1.ChunkProviderGenerate;
import net.minecraft.server.v1_12_R1.ChunkProviderServer;
import net.minecraft.server.v1_12_R1.IChunkProvider;
import net.minecraft.server.v1_12_R1.PersistentStructure;
import net.minecraft.server.v1_12_R1.StructureGenerator;
import net.minecraft.server.v1_12_R1.StructureStart;
import net.minecraft.server.v1_12_R1.World;
import net.minecraft.server.v1_12_R1.WorldGenMineshaft;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.generator.InternalChunkGenerator;
import org.bukkit.craftbukkit.v1_12_R1.generator.NormalChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;
import ru.violence.nomemleak.NoMemLeakPlugin;

import java.lang.reflect.Field;

public class WorldGenMineshaftTask extends BukkitRunnable {
    private final NoMemLeakPlugin plugin;
    private final Field chunkProviderField;
    private final Field chunkGeneratorField;
    private final Field timedGeneratorField;
    private final Field generatorField;
    private final Field yField;
    private final Field aField;
    private final Field cField;
    private final Field allStructuresField;

    public WorldGenMineshaftTask(NoMemLeakPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.chunkProviderField = net.minecraft.server.v1_12_R1.World.class.getDeclaredField("chunkProvider");
        this.chunkProviderField.setAccessible(true);
        this.chunkGeneratorField = ChunkProviderServer.class.getDeclaredField("chunkGenerator");
        this.chunkGeneratorField.setAccessible(true);
        this.timedGeneratorField = TimedChunkGenerator.class.getDeclaredField("timedGenerator");
        this.timedGeneratorField.setAccessible(true);
        this.generatorField = NormalChunkGenerator.class.getDeclaredField("generator");
        this.generatorField.setAccessible(true);
        this.yField = ChunkProviderGenerate.class.getDeclaredField("y");
        this.yField.setAccessible(true);
        this.aField = StructureGenerator.class.getDeclaredField("a");
        this.aField.setAccessible(true);
        this.cField = StructureGenerator.class.getDeclaredField("c");
        this.cField.setAccessible(true);
        this.allStructuresField = StructureGenerator.class.getDeclaredField("allStructures");
        this.allStructuresField.setAccessible(true);
    }

    @Override
    public void run() {
        int removed = 0;

        for (org.bukkit.World world : Bukkit.getWorlds()) {
            ChunkProviderServer chunkProvider = (ChunkProviderServer) getIChunkProvider(((CraftWorld) world).getHandle());
            TimedChunkGenerator timedChunkGenerator = (TimedChunkGenerator) getChunkGenerator(chunkProvider);
            InternalChunkGenerator internalChunkGenerator = getTimedGenerator(timedChunkGenerator);

            // Skip non-normal chunk generators
            if (!(internalChunkGenerator instanceof NormalChunkGenerator)) continue;

            ChunkGenerator chunkGenerator = getGenerator((NormalChunkGenerator) internalChunkGenerator);

            if (!(chunkGenerator instanceof ChunkProviderGenerate)) continue;

            ChunkProviderGenerate chunkProviderGenerate = (ChunkProviderGenerate) chunkGenerator;
            WorldGenMineshaft worldGenMineshaft = getY(chunkProviderGenerate);

            removed += clearStructureGenerator(worldGenMineshaft);
        }

        this.plugin.logCleared("WorldGenMineshaft", removed);
    }

    public IChunkProvider getIChunkProvider(World world) {
        try {
            return (IChunkProvider) this.chunkProviderField.get(world);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ChunkGenerator getChunkGenerator(ChunkProviderServer chunkProviderServer) {
        try {
            return (ChunkGenerator) this.chunkGeneratorField.get(chunkProviderServer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InternalChunkGenerator getTimedGenerator(TimedChunkGenerator timedChunkGenerator) {
        try {
            return (InternalChunkGenerator) this.timedGeneratorField.get(timedChunkGenerator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ChunkGenerator getGenerator(NormalChunkGenerator normalChunkGenerator) {
        try {
            return (ChunkGenerator) this.generatorField.get(normalChunkGenerator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public WorldGenMineshaft getY(ChunkProviderGenerate chunkProviderGenerate) {
        try {
            return (WorldGenMineshaft) this.yField.get(chunkProviderGenerate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public int clearStructureGenerator(StructureGenerator structureGenerator) {
        try {
            int cleared = 0;

            PersistentStructure a = (PersistentStructure) this.aField.get(structureGenerator);
            Long2ObjectMap<StructureStart> c = (Long2ObjectMap<StructureStart>) this.cField.get(structureGenerator);
            Long2ObjectMap<StructureStart> allStructures = (Long2ObjectMap<StructureStart>) this.allStructuresField.get(structureGenerator);

            cleared += a.a().map.size();
            cleared += c.size();
            cleared += allStructures.size();

            a.a().map.clear();
            c.clear();
            allStructures.clear();

            return cleared;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
