package com.blockfinder.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil {

    private static final Map<Block, int[]> BLOCK_COLORS = new HashMap<>();

    static {
        // Ores - distinct colors for each
        BLOCK_COLORS.put(Blocks.DIAMOND_ORE, new int[]{0, 255, 255});       // Cyan
        BLOCK_COLORS.put(Blocks.DEEPSLATE_DIAMOND_ORE, new int[]{0, 255, 255});
        BLOCK_COLORS.put(Blocks.EMERALD_ORE, new int[]{0, 255, 0});         // Green
        BLOCK_COLORS.put(Blocks.DEEPSLATE_EMERALD_ORE, new int[]{0, 255, 0});
        BLOCK_COLORS.put(Blocks.GOLD_ORE, new int[]{255, 215, 0});          // Gold
        BLOCK_COLORS.put(Blocks.DEEPSLATE_GOLD_ORE, new int[]{255, 215, 0});
        BLOCK_COLORS.put(Blocks.NETHER_GOLD_ORE, new int[]{255, 215, 0});
        BLOCK_COLORS.put(Blocks.IRON_ORE, new int[]{210, 180, 160});        // Light brown
        BLOCK_COLORS.put(Blocks.DEEPSLATE_IRON_ORE, new int[]{210, 180, 160});
        BLOCK_COLORS.put(Blocks.LAPIS_ORE, new int[]{0, 50, 255});          // Blue
        BLOCK_COLORS.put(Blocks.DEEPSLATE_LAPIS_ORE, new int[]{0, 50, 255});
        BLOCK_COLORS.put(Blocks.REDSTONE_ORE, new int[]{255, 0, 0});        // Red
        BLOCK_COLORS.put(Blocks.DEEPSLATE_REDSTONE_ORE, new int[]{255, 0, 0});
        BLOCK_COLORS.put(Blocks.COPPER_ORE, new int[]{180, 100, 50});       // Copper orange
        BLOCK_COLORS.put(Blocks.DEEPSLATE_COPPER_ORE, new int[]{180, 100, 50});
        BLOCK_COLORS.put(Blocks.COAL_ORE, new int[]{60, 60, 60});           // Dark gray
        BLOCK_COLORS.put(Blocks.DEEPSLATE_COAL_ORE, new int[]{60, 60, 60});
        BLOCK_COLORS.put(Blocks.NETHER_QUARTZ_ORE, new int[]{255, 255, 255}); // White
        BLOCK_COLORS.put(Blocks.ANCIENT_DEBRIS, new int[]{160, 80, 40});    // Dark brown

        // Containers
        BLOCK_COLORS.put(Blocks.CHEST, new int[]{255, 200, 0});             // Yellow-orange
        BLOCK_COLORS.put(Blocks.TRAPPED_CHEST, new int[]{255, 100, 0});     // Orange
        BLOCK_COLORS.put(Blocks.ENDER_CHEST, new int[]{100, 0, 200});       // Purple
        BLOCK_COLORS.put(Blocks.BARREL, new int[]{160, 120, 60});           // Wood brown

        // Shulker boxes - all magenta/pink
        BLOCK_COLORS.put(Blocks.SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.WHITE_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.ORANGE_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.MAGENTA_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.LIGHT_BLUE_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.YELLOW_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.LIME_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.PINK_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.GRAY_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.LIGHT_GRAY_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.CYAN_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.PURPLE_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.BLUE_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.BROWN_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.GREEN_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.RED_SHULKER_BOX, new int[]{200, 100, 200});
        BLOCK_COLORS.put(Blocks.BLACK_SHULKER_BOX, new int[]{200, 100, 200});

        // Spawners
        BLOCK_COLORS.put(Blocks.SPAWNER, new int[]{255, 50, 50});           // Bright red
        BLOCK_COLORS.put(Blocks.TRIAL_SPAWNER, new int[]{255, 100, 50});    // Orange-red
    }

    public static int[] getColor(Block block) {
        return BLOCK_COLORS.getOrDefault(block, new int[]{255, 255, 255});
    }

    public static float[] getColorFloat(Block block) {
        int[] c = getColor(block);
        return new float[]{c[0] / 255f, c[1] / 255f, c[2] / 255f};
    }

    public static String getCategory(Block block) {
        if (isOre(block)) return "Ores";
        if (isContainer(block)) return "Containers";
        if (isSpawner(block)) return "Spawners";
        return "Custom";
    }

    public static boolean isOre(Block block) {
        return block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE
                || block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE
                || block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE
                || block == Blocks.NETHER_GOLD_ORE
                || block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE
                || block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE
                || block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE
                || block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE
                || block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE
                || block == Blocks.NETHER_QUARTZ_ORE || block == Blocks.ANCIENT_DEBRIS;
    }

    public static boolean isContainer(Block block) {
        return block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST
                || block == Blocks.ENDER_CHEST || block == Blocks.BARREL
                || block == Blocks.SHULKER_BOX || block == Blocks.WHITE_SHULKER_BOX
                || block == Blocks.ORANGE_SHULKER_BOX || block == Blocks.MAGENTA_SHULKER_BOX
                || block == Blocks.LIGHT_BLUE_SHULKER_BOX || block == Blocks.YELLOW_SHULKER_BOX
                || block == Blocks.LIME_SHULKER_BOX || block == Blocks.PINK_SHULKER_BOX
                || block == Blocks.GRAY_SHULKER_BOX || block == Blocks.LIGHT_GRAY_SHULKER_BOX
                || block == Blocks.CYAN_SHULKER_BOX || block == Blocks.PURPLE_SHULKER_BOX
                || block == Blocks.BLUE_SHULKER_BOX || block == Blocks.BROWN_SHULKER_BOX
                || block == Blocks.GREEN_SHULKER_BOX || block == Blocks.RED_SHULKER_BOX
                || block == Blocks.BLACK_SHULKER_BOX;
    }

    public static boolean isSpawner(Block block) {
        return block == Blocks.SPAWNER || block == Blocks.TRIAL_SPAWNER;
    }
}
