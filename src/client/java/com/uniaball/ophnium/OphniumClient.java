package com.uniaball.ophnium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class OphniumClient implements ClientModInitializer {
    // 使用原子变量确保线程安全
    private static volatile long lastFrameTime = 0;
    private static volatile int frameCount = 0;
    private static volatile int currentFPS = 0;
    private static volatile long lastUpdateTime = 0;
    
    // 优化级别缓存
    private static volatile OptimizationManager.OptimizationLevel lastLevel = OptimizationManager.OptimizationLevel.NONE;

    @Override
    public void onInitializeClient() {
        // 初始化帧率计数器
        lastFrameTime = System.currentTimeMillis();
        
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) return;

            // 极简帧率计数
            updateFPS();
            
            // 高效渲染文本
            renderFPS(drawContext, client);
        });
    }

    private void updateFPS() {
        // 每帧只增加计数器
        frameCount++;
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastUpdateTime;
        
        // 动态调整检测频率
        int targetInterval = (currentFPS > 60) ? 2000 : 1000; // 高帧率时每2秒检测一次
        
        if (elapsed >= targetInterval) {
            // 计算实际FPS
            currentFPS = (int) (frameCount * 1000 / elapsed);
            
            // 重置计数器
            frameCount = 0;
            lastUpdateTime = currentTime;
            
            // 优化级别管理
            OptimizationManager.OptimizationLevel newLevel = OptimizationManager.updateOptimizationLevel(currentFPS);
            
            // 如果级别变化，应用新设置
            if (newLevel != lastLevel) {
                OptimizationManager.applyOptimizationLevel(newLevel);
                lastLevel = newLevel;
            }
        }
    }

    private void renderFPS(DrawContext context, MinecraftClient client) {
        // 仅当有更新时才重新计算文本
        String displayText = "FPS: " + currentFPS;
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
    
    // 获取当前FPS值（供其他类使用）
    public static int getCurrentFPS() {
        return currentFPS;
    }
}
