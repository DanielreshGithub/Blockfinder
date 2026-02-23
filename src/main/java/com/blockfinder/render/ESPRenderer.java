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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class ESPRenderer {
    private static final int MAX_RENDERED_BLOCKS = 80;
    private static final int DEBUG_COLOR = 0xFF00FFFF; // opaque bright cyan

    private ESPRenderer() {
    }

    public static void render(WorldRenderContext context) {
        if (!BlockFinderClient.enabled || BlockFinderClient.scanner == null || BlockFinderClient.config == null) return;

        Map<BlockPos, Block> blocks = BlockFinderClient.scanner.getFoundBlocks();
        if (blocks.isEmpty()) return;

        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;

        VertexConsumer lines = consumers.getBuffer(RenderLayers.lines());
        float lineWidth = Math.max(4.0f, BlockFinderClient.config.lineWidth);
        double camX = 0.0;
        double camY = 0.0;
        double camZ = 0.0;

        if (context.worldState() != null
                && context.worldState().cameraRenderState != null
                && context.worldState().cameraRenderState.pos != null) {
            Vec3d camPos = context.worldState().cameraRenderState.pos;
            camX = camPos.x;
            camY = camPos.y;
            camZ = camPos.z;
        }
        final double cameraX = camX;
        final double cameraY = camY;
        final double cameraZ = camZ;

        List<Map.Entry<BlockPos, Block>> nearest = blocks.entrySet().stream()
                .sorted(Comparator.comparingDouble(entry -> {
                    BlockPos p = entry.getKey();
                    double dx = (p.getX() + 0.5) - cameraX;
                    double dy = (p.getY() + 0.5) - cameraY;
                    double dz = (p.getZ() + 0.5) - cameraZ;
                    return dx * dx + dy * dy + dz * dz;
                }))
                .limit(MAX_RENDERED_BLOCKS)
                .toList();

        for (Map.Entry<BlockPos, Block> entry : nearest) {
            BlockPos pos = entry.getKey();
            int[] rgb = ColorUtil.getColor(entry.getValue());
            int color = DEBUG_COLOR;
            if (rgb[0] != 255 || rgb[1] != 255 || rgb[2] != 255) {
                color = (255 << 24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
            }

            VertexRendering.drawOutline(
                    context.matrices(),
                    lines,
                    VoxelShapes.fullCube(),
                    pos.getX() - camX,
                    pos.getY() - camY,
                    pos.getZ() - camZ,
                    color,
                    lineWidth
            );
        }
    }
}
