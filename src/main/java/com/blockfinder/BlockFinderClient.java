package com.blockfinder;

import com.blockfinder.config.BlockFinderConfig;
import com.blockfinder.config.ConfigScreen;
import com.blockfinder.render.ESPRenderer;
import com.blockfinder.render.HudRenderer;
import com.blockfinder.render.ParticleHighlighter;
import com.blockfinder.scanner.BlockScanner;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockFinderClient implements ClientModInitializer {

    public static final String MOD_ID = "blockfinder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static BlockFinderConfig config;
    public static BlockScanner scanner;
    public static boolean enabled = false;

    private static KeyBinding toggleKey;
    private static KeyBinding configKey;
    private static boolean rawToggleHeld = false;
    private static final KeyBinding.Category BLOCKFINDER_CATEGORY =
            KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

    @Override
    public void onInitializeClient() {
        LOGGER.info("BlockFinder initializing...");

        config = BlockFinderConfig.load();
        scanner = new BlockScanner();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blockfinder.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                BLOCKFINDER_CATEGORY
        ));

        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blockfinder.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                BLOCKFINDER_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ESPRenderer::render);
        WorldRenderEvents.END_MAIN.register(ESPRenderer::render);
        HudRenderCallback.EVENT.register(HudRenderer::render);

        LOGGER.info("BlockFinder initialized!");
    }

    private void onClientTick(MinecraftClient client) {
        boolean toggled = false;
        while (toggleKey.wasPressed()) {
            toggleEnabled(client);
            toggled = true;
        }

        // Fallback for profiles where another mod conflicts with G keybinding processing.
        if (!toggled && client.currentScreen == null) {
            boolean rawPressed = InputUtil.isKeyPressed(client.getWindow(), GLFW.GLFW_KEY_G);
            if (rawPressed && !rawToggleHeld) {
                toggleEnabled(client);
            }
            rawToggleHeld = rawPressed;
        } else if (client.currentScreen != null) {
            rawToggleHeld = false;
        }

        if (configKey.wasPressed()) {
            client.setScreen(new ConfigScreen(null));
        }

        if (enabled && client.world != null && client.player != null) {
            scanner.tick(client);
            ParticleHighlighter.tick(client);
        }
    }

    private static void toggleEnabled(MinecraftClient client) {
        enabled = !enabled;
        if (enabled) {
            scanner.invalidateCache();
        }
        if (client.player != null) {
            client.player.sendMessage(Text.literal("BlockFinder: " + (enabled ? "ON" : "OFF")), true);
        }
    }
}
