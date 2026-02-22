package com.blockfinder.scanner;

import com.blockfinder.BlockFinderClient;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockScanner {

    private final Map<BlockPos, Block> foundBlocks = new ConcurrentHashMap<>();
    private BlockPos lastScanCenter = null;
    private int tickCounter = 0;
    private static final int SCAN_INTERVAL = 20; // ticks between full scans
    private static final int MOVE_THRESHOLD = 8; // blocks before rescan

    public Map<BlockPos, Block> getFoundBlocks() {
        return foundBlocks;
    }

    public void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        tickCounter++;

        BlockPos playerPos = client.player.getBlockPos();
        boolean needsScan = false;

        if (lastScanCenter == null) {
            needsScan = true;
        } else if (tickCounter >= SCAN_INTERVAL) {
            needsScan = true;
        } else if (playerPos.getManhattanDistance(lastScanCenter) > MOVE_THRESHOLD) {
            needsScan = true;
        }

        if (needsScan) {
            scan(client, playerPos);
            lastScanCenter = playerPos;
            tickCounter = 0;
        }
    }

    private void scan(MinecraftClient client, BlockPos center) {
        Set<Block> targets = BlockFinderClient.config.getTargetBlocks();
        if (targets.isEmpty()) {
            foundBlocks.clear();
            return;
        }

        int radius = BlockFinderClient.config.scanRadius;
        Map<BlockPos, Block> newFound = new HashMap<>();

        int minX = center.getX() - radius;
        int maxX = center.getX() + radius;
        int minZ = center.getZ() - radius;
        int maxZ = center.getZ() + radius;
        int minY = Math.max(client.world.getBottomY(), center.getY() - radius);
        int worldTopY = client.world.getTopY(Heightmap.Type.WORLD_SURFACE, center.getX(), center.getZ()) - 1;
        int maxY = Math.min(worldTopY, center.getY() + radius);

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    mutable.set(x, y, z);
                    Block block = client.world.getBlockState(mutable).getBlock();
                    if (targets.contains(block)) {
                        newFound.put(mutable.toImmutable(), block);
                    }
                }
            }
        }

        foundBlocks.clear();
        foundBlocks.putAll(newFound);
    }

    public void invalidateCache() {
        lastScanCenter = null;
        tickCounter = SCAN_INTERVAL;
    }

    public int getCount() {
        return foundBlocks.size();
    }

    public Map<String, Integer> getCountByCategory() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        Map<Block, Integer> blockCounts = new HashMap<>();

        for (Block block : foundBlocks.values()) {
            blockCounts.merge(block, 1, Integer::sum);
        }

        // Group by display name
        for (Map.Entry<Block, Integer> entry : blockCounts.entrySet()) {
            String name = entry.getKey().getName().getString();
            counts.merge(name, entry.getValue(), Integer::sum);
        }

        return counts;
    }
}
