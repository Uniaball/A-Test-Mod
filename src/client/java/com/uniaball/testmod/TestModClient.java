package com.uniaball.testmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.fps.FpsCounter;

public class TestModClient implements ClientModInitializer {
    private static long lastRefreshTime;
    private static String displayText = "FPS: 0";

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) return;

            // 更新显示文本
            updateDisplayText(client);
            
            // 渲染文本到屏幕顶部中央
            renderFPS(drawContext, client);
        });
    }

    private void updateDisplayText(MinecraftClient client) {
        long currentTime = System.currentTimeMillis();
        
        // 每1000毫秒（1秒）更新一次显示
        if (currentTime - lastRefreshTime >= 1000) {
            FpsCounter fpsCounter = client.getCurrentFpsCounter();
            if (fpsCounter != null) {
                // 直接获取游戏内计算的精确FPS值
                int fps = fpsCounter.getFps();
                
                // 创建纯文本格式（无颜色）
                displayText = "FPS: " + fps;
            }
            lastRefreshTime = currentTime;
        }
    }

    private void renderFPS(DrawContext context, MinecraftClient client) {
        int screenWidth = client.getWindow().getScaledWidth();
        
        // 在屏幕顶部居中显示
        int textWidth = client.textRenderer.getWidth(displayText);
        int x = (screenWidth - textWidth) / 2;
        int y = 10; // 距离顶部10像素

        context.drawTextWithShadow(
            client.textRenderer,
            displayText,
            x,
            y,
            0xFFFFFF // 白色文本
        );
    }
}
