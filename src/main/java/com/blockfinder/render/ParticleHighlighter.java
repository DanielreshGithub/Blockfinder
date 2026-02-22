package com.blockfinder.render;

import com.blockfinder.BlockFinderClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public final class ParticleHighlighter {
    private static int tickCounter = 0;
    private static final int PARTICLE_INTERVAL = 4;
    private static final int MAX_PARTICLE_BLOCKS = 160;

    private ParticleHighlighter() {
    }

    public static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        tickCounter++;
        if (tickCounter < PARTICLE_INTERVAL) return;
        tickCounter = 0;

        Map<BlockPos, ?> blocks = BlockFinderClient.scanner.getFoundBlocks();
        if (blocks.isEmpty()) return;

        double px = client.player.getX();
        double py = client.player.getY();
        double pz = client.player.getZ();

        int shown = 0;
        for (BlockPos pos : blocks.keySet()) {
            if (shown >= MAX_PARTICLE_BLOCKS) break;

            double dx = pos.getX() + 0.5 - px;
            double dy = pos.getY() + 0.5 - py;
            double dz = pos.getZ() + 0.5 - pz;
            if (dx * dx + dy * dy + dz * dz > 128 * 128) continue;

            // Keep the marker inside the target block so underground targets line up with ground position.
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            client.particleManager.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.0, 0.0);
            shown++;
        }
    }
}
