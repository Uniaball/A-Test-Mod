package com.uniaball.testmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class TestModClient implements ClientModInitializer {
    private static long lastRefreshTime;
    private static String displayText = "FPS: 0";
    private static int frameCount = 0;
    private static long lastFrameTime = 0;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) return;

            // 计算帧率
            updateFPS();
            
            // 渲染文本到屏幕顶部中央
            renderFPS(drawContext, client);
        });
    }

    private void updateFPS() {
        long currentTime = System.currentTimeMillis();
        frameCount++;
        
        // 每1000毫秒（1秒）更新一次显示
        if (currentTime - lastRefreshTime >= 1000) {
            // 计算实际FPS
            int fps = frameCount;
            
            // 重置计数器
            frameCount = 0;
            lastRefreshTime = currentTime;
            
            // 更新显示文本
            displayText = "FPS: " + fps;
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
