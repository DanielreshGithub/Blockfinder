package com.blockfinder.config;

import com.blockfinder.BlockFinderClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BlockFinderConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("blockfinder.json");

    public int scanRadius = 32;
    public boolean enableOres = true;
    public boolean enableContainers = true;
    public boolean enableSpawners = true;
    public List<String> customBlockIds = new ArrayList<>();
    public float lineWidth = 2.0f;

    // Individual ore toggles
    public boolean findDiamond = true;
    public boolean findEmerald = true;
    public boolean findGold = true;
    public boolean findIron = true;
    public boolean findLapis = true;
    public boolean findRedstone = true;
    public boolean findCopper = true;
    public boolean findCoal = false;
    public boolean findNetherQuartz = true;
    public boolean findAncientDebris = true;

    // Individual container toggles
    public boolean findChest = true;
    public boolean findTrappedChest = true;
    public boolean findEnderChest = true;
    public boolean findBarrel = true;
    public boolean findShulkerBox = true;

    // Individual spawner toggles
    public boolean findSpawner = true;
    public boolean findTrialSpawner = true;

    public Set<Block> getTargetBlocks() {
        Set<Block> targets = new HashSet<>();

        if (enableOres) {
            if (findDiamond) { targets.add(Blocks.DIAMOND_ORE); targets.add(Blocks.DEEPSLATE_DIAMOND_ORE); }
            if (findEmerald) { targets.add(Blocks.EMERALD_ORE); targets.add(Blocks.DEEPSLATE_EMERALD_ORE); }
            if (findGold) { targets.add(Blocks.GOLD_ORE); targets.add(Blocks.DEEPSLATE_GOLD_ORE); targets.add(Blocks.NETHER_GOLD_ORE); }
            if (findIron) { targets.add(Blocks.IRON_ORE); targets.add(Blocks.DEEPSLATE_IRON_ORE); }
            if (findLapis) { targets.add(Blocks.LAPIS_ORE); targets.add(Blocks.DEEPSLATE_LAPIS_ORE); }
            if (findRedstone) { targets.add(Blocks.REDSTONE_ORE); targets.add(Blocks.DEEPSLATE_REDSTONE_ORE); }
            if (findCopper) { targets.add(Blocks.COPPER_ORE); targets.add(Blocks.DEEPSLATE_COPPER_ORE); }
            if (findCoal) { targets.add(Blocks.COAL_ORE); targets.add(Blocks.DEEPSLATE_COAL_ORE); }
            if (findNetherQuartz) { targets.add(Blocks.NETHER_QUARTZ_ORE); }
            if (findAncientDebris) { targets.add(Blocks.ANCIENT_DEBRIS); }
        }

        if (enableContainers) {
            if (findChest) { targets.add(Blocks.CHEST); }
            if (findTrappedChest) { targets.add(Blocks.TRAPPED_CHEST); }
            if (findEnderChest) { targets.add(Blocks.ENDER_CHEST); }
            if (findBarrel) { targets.add(Blocks.BARREL); }
            if (findShulkerBox) {
                targets.add(Blocks.SHULKER_BOX);
                targets.add(Blocks.WHITE_SHULKER_BOX);
                targets.add(Blocks.ORANGE_SHULKER_BOX);
                targets.add(Blocks.MAGENTA_SHULKER_BOX);
                targets.add(Blocks.LIGHT_BLUE_SHULKER_BOX);
                targets.add(Blocks.YELLOW_SHULKER_BOX);
                targets.add(Blocks.LIME_SHULKER_BOX);
                targets.add(Blocks.PINK_SHULKER_BOX);
                targets.add(Blocks.GRAY_SHULKER_BOX);
                targets.add(Blocks.LIGHT_GRAY_SHULKER_BOX);
                targets.add(Blocks.CYAN_SHULKER_BOX);
                targets.add(Blocks.PURPLE_SHULKER_BOX);
                targets.add(Blocks.BLUE_SHULKER_BOX);
                targets.add(Blocks.BROWN_SHULKER_BOX);
                targets.add(Blocks.GREEN_SHULKER_BOX);
                targets.add(Blocks.RED_SHULKER_BOX);
                targets.add(Blocks.BLACK_SHULKER_BOX);
            }
        }

        if (enableSpawners) {
            if (findSpawner) { targets.add(Blocks.SPAWNER); }
            if (findTrialSpawner) { targets.add(Blocks.TRIAL_SPAWNER); }
        }

        // Custom blocks
        for (String id : customBlockIds) {
            try {
                Identifier blockId = Identifier.of(id);
                Block block = Registries.BLOCK.get(blockId);
                if (block != Blocks.AIR) {
                    targets.add(block);
                }
            } catch (Exception e) {
                BlockFinderClient.LOGGER.warn("Invalid custom block ID: {}", id);
            }
        }

        return targets;
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            BlockFinderClient.LOGGER.error("Failed to save config", e);
        }
    }

    public static BlockFinderConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                BlockFinderConfig config = GSON.fromJson(json, BlockFinderConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (Exception e) {
                BlockFinderClient.LOGGER.error("Failed to load config, using defaults", e);
            }
        }
        BlockFinderConfig config = new BlockFinderConfig();
        config.save();
        return config;
    }
}
