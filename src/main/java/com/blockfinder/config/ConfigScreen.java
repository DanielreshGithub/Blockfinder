package com.blockfinder.config;

import com.blockfinder.BlockFinderClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private final BlockFinderConfig config;
    private TextFieldWidget customBlockField;

    private static final int COL_LEFT = 20;
    private static final int COL_RIGHT = 230;
    private static final int BTN_W = 180;
    private static final int BTN_H = 20;

    public ConfigScreen(Screen parent) {
        super(Text.literal("BlockFinder Configuration"));
        this.parent = parent;
        this.config = BlockFinderClient.config;
    }

    @Override
    protected void init() {
        int y = 30;

        // === Title is drawn in render ===

        // Scan radius slider
        y += 5;
        this.addDrawableChild(new RadiusSlider(COL_LEFT, y, BTN_W, BTN_H, config.scanRadius));
        y += 28;

        // === ORES SECTION ===
        // Category toggle
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Ores: " + (config.enableOres ? "ON" : "OFF")),
                        btn -> {
                            config.enableOres = !config.enableOres;
                            btn.setMessage(Text.literal("Ores: " + (config.enableOres ? "ON" : "OFF")));
                            onConfigChanged();
                        })
                .dimensions(COL_LEFT, y, 90, BTN_H).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("All ON"), btn -> {
            setAllOres(true);
            reopenSelf();
        }).dimensions(COL_LEFT + 95, y, 62, BTN_H).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("All OFF"), btn -> {
            setAllOres(false);
            reopenSelf();
        }).dimensions(COL_LEFT + 161, y, 72, BTN_H).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Rare"), btn -> {
            applyRarePreset();
            reopenSelf();
        }).dimensions(COL_LEFT + 237, y, 58, BTN_H).build());

        // Individual ore toggles (two columns)
        int oreY = y + 22;
        this.addDrawableChild(makeToggle(COL_LEFT + 100, oreY, 80, "Diamond", config.findDiamond, v -> { config.findDiamond = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_LEFT + 185, oreY, 80, "Emerald", config.findEmerald, v -> { config.findEmerald = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_RIGHT, oreY, 70, "Gold", config.findGold, v -> { config.findGold = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_RIGHT + 75, oreY, 70, "Iron", config.findIron, v -> { config.findIron = v; onConfigChanged(); }));
        oreY += 22;

        this.addDrawableChild(makeToggle(COL_LEFT, oreY, 80, "Lapis", config.findLapis, v -> { config.findLapis = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_LEFT + 85, oreY, 90, "Redstone", config.findRedstone, v -> { config.findRedstone = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_LEFT + 180, oreY, 80, "Copper", config.findCopper, v -> { config.findCopper = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_RIGHT, oreY, 65, "Coal", config.findCoal, v -> { config.findCoal = v; onConfigChanged(); }));
        oreY += 22;

        this.addDrawableChild(makeToggle(COL_LEFT, oreY, 80, "Quartz", config.findNetherQuartz, v -> { config.findNetherQuartz = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_LEFT + 85, oreY, 110, "Anc. Debris", config.findAncientDebris, v -> { config.findAncientDebris = v; onConfigChanged(); }));
        y = oreY + 30;

        // === CONTAINERS SECTION ===
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Containers: " + (config.enableContainers ? "ON" : "OFF")),
                        btn -> {
                            config.enableContainers = !config.enableContainers;
                            btn.setMessage(Text.literal("Containers: " + (config.enableContainers ? "ON" : "OFF")));
                            onConfigChanged();
                        })
                .dimensions(COL_LEFT, y, 110, BTN_H).build());

        this.addDrawableChild(makeToggle(COL_LEFT + 115, y, 70, "Chest", config.findChest, v -> { config.findChest = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_LEFT + 190, y, 95, "Ender Chest", config.findEnderChest, v -> { config.findEnderChest = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_RIGHT + 55, y, 75, "Barrel", config.findBarrel, v -> { config.findBarrel = v; onConfigChanged(); }));
        y += 22;

        this.addDrawableChild(makeToggle(COL_LEFT, y, 90, "Shulkers", config.findShulkerBox, v -> { config.findShulkerBox = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_LEFT + 95, y, 110, "Trapped Chest", config.findTrappedChest, v -> { config.findTrappedChest = v; onConfigChanged(); }));
        y += 30;

        // === SPAWNERS SECTION ===
        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Spawners: " + (config.enableSpawners ? "ON" : "OFF")),
                        btn -> {
                            config.enableSpawners = !config.enableSpawners;
                            btn.setMessage(Text.literal("Spawners: " + (config.enableSpawners ? "ON" : "OFF")));
                            onConfigChanged();
                        })
                .dimensions(COL_LEFT, y, 100, BTN_H).build());

        this.addDrawableChild(makeToggle(COL_LEFT + 105, y, 90, "Spawner", config.findSpawner, v -> { config.findSpawner = v; onConfigChanged(); }));
        this.addDrawableChild(makeToggle(COL_LEFT + 200, y, 110, "Trial Spawner", config.findTrialSpawner, v -> { config.findTrialSpawner = v; onConfigChanged(); }));
        y += 30;

        // === CUSTOM BLOCKS ===
        customBlockField = new TextFieldWidget(this.textRenderer, COL_LEFT, y, 250, BTN_H,
                Text.literal("Custom block ID"));
        customBlockField.setPlaceholder(Text.literal("e.g. minecraft:budding_amethyst"));
        customBlockField.setMaxLength(256);
        this.addDrawableChild(customBlockField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Add"), btn -> {
            String id = customBlockField.getText().trim();
            if (!id.isEmpty() && !config.customBlockIds.contains(id)) {
                if (!id.contains(":")) id = "minecraft:" + id;
                config.customBlockIds.add(id);
                customBlockField.setText("");
                onConfigChanged();
            }
        }).dimensions(COL_LEFT + 255, y, 40, BTN_H).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Clear Custom"), btn -> {
            config.customBlockIds.clear();
            onConfigChanged();
        }).dimensions(COL_LEFT + 300, y, 90, BTN_H).build());
        y += 28;

        // === DONE BUTTON ===
        y += 10;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> close())
                .dimensions(this.width / 2 - 50, y, 100, BTN_H).build());
    }

    private void setAllOres(boolean enabled) {
        config.enableOres = true;
        config.findDiamond = enabled;
        config.findEmerald = enabled;
        config.findGold = enabled;
        config.findIron = enabled;
        config.findLapis = enabled;
        config.findRedstone = enabled;
        config.findCopper = enabled;
        config.findCoal = enabled;
        config.findNetherQuartz = enabled;
        config.findAncientDebris = enabled;
        onConfigChanged();
    }

    private void applyRarePreset() {
        config.enableOres = true;
        config.findDiamond = true;
        config.findEmerald = true;
        config.findAncientDebris = true;
        config.findGold = false;
        config.findIron = false;
        config.findLapis = false;
        config.findRedstone = false;
        config.findCopper = false;
        config.findCoal = false;
        config.findNetherQuartz = false;
        onConfigChanged();
    }

    private void reopenSelf() {
        if (this.client != null) {
            this.client.setScreen(new ConfigScreen(parent));
        }
    }

    private ButtonWidget makeToggle(int x, int y, int width, String label, boolean initialValue,
                                     java.util.function.Consumer<Boolean> setter) {
        final boolean[] value = {initialValue};
        return ButtonWidget.builder(
                Text.literal((value[0] ? "\u00A7a" : "\u00A7c") + label),
                btn -> {
                    value[0] = !value[0];
                    setter.accept(value[0]);
                    btn.setMessage(Text.literal((value[0] ? "\u00A7a" : "\u00A7c") + label));
                }
        ).dimensions(x, y, width, BTN_H).build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);

        // Show custom block list
        if (!config.customBlockIds.isEmpty()) {
            int listY = 0;
            for (var child : this.children()) {
                // find the custom block field y position
            }
            String customList = "Custom: " + String.join(", ", config.customBlockIds);
            context.drawTextWithShadow(this.textRenderer, customList, COL_LEFT, this.height - 20, 0xAAAAFF);
        }
    }

    @Override
    public void close() {
        config.save();
        BlockFinderClient.scanner.invalidateCache();
        this.client.setScreen(parent);
    }

    private void onConfigChanged() {
        BlockFinderClient.scanner.invalidateCache();
    }

    // Inner class for radius slider
    private class RadiusSlider extends SliderWidget {
        public RadiusSlider(int x, int y, int width, int height, int initialRadius) {
            super(x, y, width, height, Text.literal("Scan Radius: " + initialRadius), (initialRadius - 8) / 56.0);
        }

        @Override
        protected void updateMessage() {
            int radius = 8 + (int) (this.value * 56);
            this.setMessage(Text.literal("Scan Radius: " + radius));
        }

        @Override
        protected void applyValue() {
            config.scanRadius = 8 + (int) (this.value * 56);
            onConfigChanged();
        }
    }
}
