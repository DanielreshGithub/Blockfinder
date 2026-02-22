package com.blockfinder.render;

import com.blockfinder.BlockFinderClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.Map;

public class HudRenderer {

    public static void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getDebugHud().shouldShowDebugHud()) return;

        TextRenderer textRenderer = client.textRenderer;
        int x = 4;
        int y = 4;
        int lineHeight = 11;

        if (BlockFinderClient.enabled) {
            // Status header
            drawContext.drawTextWithShadow(textRenderer, "BlockFinder: ON", x, y, 0x55FF55);
            y += lineHeight;

            int totalCount = BlockFinderClient.scanner.getCount();
            int requestedRadius = BlockFinderClient.config.scanRadius;
            int effectiveRadius = BlockFinderClient.scanner.getEffectiveRadius();
            String radiusText = requestedRadius == effectiveRadius
                    ? ("r=" + requestedRadius)
                    : ("r=" + requestedRadius + "->" + effectiveRadius);
            drawContext.drawTextWithShadow(textRenderer,
                    "Found: " + totalCount + " blocks (" + radiusText + ")",
                    x, y, 0xAAAAAA);
            y += lineHeight;

            // Per-block-type counts
            Map<String, Integer> counts = BlockFinderClient.scanner.getCountByCategory();
            if (!counts.isEmpty()) {
                y += 2; // small gap
                for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                    String line = "  " + entry.getKey() + ": " + entry.getValue();
                    drawContext.drawTextWithShadow(textRenderer, line, x, y, 0xCCCCCC);
                    y += lineHeight;
                }
            }

            // Controls hint
            y += 4;
            drawContext.drawTextWithShadow(textRenderer, "[G] Toggle | [H] Config", x, y, 0x666666);
        } else {
            drawContext.drawTextWithShadow(textRenderer, "BlockFinder: OFF [G]", x, y, 0x888888);
        }
    }
}
