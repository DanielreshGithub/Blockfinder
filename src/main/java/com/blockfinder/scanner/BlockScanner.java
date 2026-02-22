package com.blockfinder.scanner;

import com.blockfinder.BlockFinderClient;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockScanner {

    private final Map<BlockPos, Block> foundBlocks = new ConcurrentHashMap<>();
    private BlockPos lastScanCenter = null;
    private int tickCounter = 0;
    private static final int SCAN_INTERVAL = 20; // ticks between full scans
    private static final int MOVE_THRESHOLD = 8; // blocks before rescan
    private static final int CHUNK_SIZE = 16;
    private static final boolean CHUNK_LOCAL_MODE = true;
    private static final int MAX_EFFECTIVE_RADIUS = 32;
    private static final int MAX_SCAN_BLOCKS = 750_000;
    private static final int MIN_SCAN_HEIGHT = 48;
    private static final int UPWARD_SCAN_MARGIN = 24;
    private int lastEffectiveRadius = 32;

    public Map<BlockPos, Block> getFoundBlocks() {
        return foundBlocks;
    }

    public int getEffectiveRadius() {
        return lastEffectiveRadius;
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

        int requestedRadius = BlockFinderClient.config.scanRadius;
        int radius = Math.min(requestedRadius, MAX_EFFECTIVE_RADIUS);
        lastEffectiveRadius = radius;
        Map<BlockPos, Block> newFound = new HashMap<>();

        int minX;
        int maxX;
        int minZ;
        int maxZ;

        if (CHUNK_LOCAL_MODE) {
            int chunkStartX = (center.getX() >> 4) << 4;
            int chunkStartZ = (center.getZ() >> 4) << 4;
            minX = chunkStartX;
            maxX = chunkStartX + (CHUNK_SIZE - 1);
            minZ = chunkStartZ;
            maxZ = chunkStartZ + (CHUNK_SIZE - 1);
            lastEffectiveRadius = 8;
        } else {
            minX = center.getX() - radius;
            maxX = center.getX() + radius;
            minZ = center.getZ() - radius;
            maxZ = center.getZ() + radius;
        }

        int worldBottom = client.world.getBottomY();
        int maxY = Math.min(client.world.getTopYInclusive(), center.getY() + UPWARD_SCAN_MARGIN);
        int minY = worldBottom;

        int sizeX = (maxX - minX + 1);
        int sizeZ = (maxZ - minZ + 1);
        int columns = Math.max(1, sizeX * sizeZ);
        int maxHeightByBudget = Math.max(MIN_SCAN_HEIGHT, MAX_SCAN_BLOCKS / columns);
        minY = Math.max(worldBottom, maxY - maxHeightByBudget + 1);

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
        BlockFinderClient.LOGGER.info(
                "BlockFinder scan complete: found={} requestedRadius={} effectiveRadius={} yRange=[{},{}]",
                foundBlocks.size(),
                requestedRadius,
                radius,
                minY,
                maxY
        );
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
