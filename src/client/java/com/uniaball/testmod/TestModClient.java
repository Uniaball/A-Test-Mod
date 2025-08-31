package com.uniaball.testmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;

public class TestModClient implements ClientModInitializer {
    private static final DecimalFormat FPS_FORMAT = new DecimalFormat("0");
    private static long lastUpdateTime;
    private static float currentFPS;

    @Override
    public void onInitializeClient() {
        // 注册HUD渲染回调
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) return;

            // 计算实时FPS
            updateFPS();
            renderFPS(matrixStack, client);
        });
    }

    private void updateFPS() {
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            return;
        }

        long elapsed = currentTime - lastUpdateTime;
        if (elapsed > 0) {
            currentFPS = 1000f / elapsed;
            lastUpdateTime = currentTime;
        }
    }

    private void renderFPS(MatrixStack matrices, MinecraftClient client) {
        String fpsText = Formatting.GREEN + "FPS: " + FPS_FORMAT.format(currentFPS);
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // 在屏幕中央绘制
        int textWidth = client.textRenderer.getWidth(fpsText);
        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight / 2 - 4; // 垂直居中

        client.textRenderer.drawWithShadow(
            matrices,
            fpsText,
            x,
            y,
            0xFFFFFF
        );
    }
}
