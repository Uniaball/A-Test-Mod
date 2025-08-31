package com.uniaball.testmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;

public class TestModClient implements ClientModInitializer {
    private static final DecimalFormat FPS_FORMAT = new DecimalFormat("0");
    private static long lastUpdateTime;
    private static long lastRefreshTime;
    private static float currentFPS;
    private static float displayFPS;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) return;

            // 计算实时FPS
            updateFPS();
            renderFPS(drawContext, client);
        });
    }

    private void updateFPS() {
        long currentTime = System.currentTimeMillis();
        
        // 初始化时间戳
        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            lastRefreshTime = currentTime;
            return;
        }
        
        // 每帧计算瞬时帧率
        long elapsed = currentTime - lastUpdateTime;
        if (elapsed > 0) {
            currentFPS = 1000f / elapsed;
            lastUpdateTime = currentTime;
        }
        
        // 每2秒更新一次显示值
        if (currentTime - lastRefreshTime > 2000) {
            displayFPS = currentFPS;
            lastRefreshTime = currentTime;
        }
    }

    private void renderFPS(DrawContext context, MinecraftClient client) {
        String fpsText = Formatting.GREEN + "FPS: " + FPS_FORMAT.format(displayFPS);
        int screenWidth = client.getWindow().getScaledWidth();
        
        // 在屏幕顶部居中显示
        int textWidth = client.textRenderer.getWidth(fpsText);
        int x = (screenWidth - textWidth) / 2;
        int y = 10; // 距离顶部10像素

        context.drawTextWithShadow(
            client.textRenderer,
            fpsText,
            x,
            y,
            0xFFFFFF
        );
    }
}
