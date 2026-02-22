package com.blockfinder.render;

import com.blockfinder.BlockFinderClient;
import com.blockfinder.util.ColorUtil;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;

import java.util.Map;

public final class ESPRenderer {
    private ESPRenderer() {
    }

    public static void render(WorldRenderContext context) {
        if (!BlockFinderClient.enabled || BlockFinderClient.scanner == null || BlockFinderClient.config == null) return;

        Map<BlockPos, Block> blocks = BlockFinderClient.scanner.getFoundBlocks();
        if (blocks.isEmpty()) return;

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;

        VertexConsumer lines = consumers.getBuffer(RenderLayers.lines());
        float lineWidth = Math.max(1.0f, BlockFinderClient.config.lineWidth);

        for (Map.Entry<BlockPos, Block> entry : blocks.entrySet()) {
            BlockPos pos = entry.getKey();
            int[] rgb = ColorUtil.getColor(entry.getValue());
            int color = (215 << 24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];

            VertexRendering.drawOutline(
                    context.matrices(),
                    lines,
                    VoxelShapes.fullCube(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    color,
                    lineWidth
            );
        }
    }
}
