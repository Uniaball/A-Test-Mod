package com.uniaball.ophnium.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.uniaball.ophnium.OptimizationManager;
import com.uniaball.ophnium.OptimizationManager.OptimizationLevel;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow private ChunkBuilder chunkBuilder;
    
    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk;setLevel(I)V"
        )
    )
    private void adjustChunkLightDetail(ChunkBuilder.BuiltChunk builtChunk, int level) {
        OptimizationLevel optLevel = OptimizationManager.getCurrentLevel();
        
        // 无优化时使用原始级别
        if (optLevel == OptimizationManager.OptimizationLevel.NONE) {
            builtChunk.setLevel(level);
            return;
        }
        
        // 根据优化级别调整细节
        int adjustedLevel = level;
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        ChunkPos chunkPos = builtChunk.getChunkPos();
        double distance = cameraPos.distanceTo(new Vec3d(chunkPos.getStartX(), cameraPos.y, chunkPos.getStartZ()));
        
        // 根据距离和优化级别调整细节
        if (distance > 32) {
            switch (optLevel) {
                case LOW:
                    adjustedLevel = Math.max(level - 1, 0);
                    break;
                case MEDIUM:
                    adjustedLevel = Math.max(level - 2, 0);
                    break;
                case HIGH:
                    adjustedLevel = Math.max(level - 3, 0);
                    break;
            }
        }
        
        builtChunk.setLevel(adjustedLevel);
    }
    
    @Inject(
        method = "renderSky",
        at = @At("HEAD"),
        cancellable = true
    )
    private void simplifySkyLight(DrawContext context, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        OptimizationLevel level = OptimizationManager.getCurrentLevel();
        
        // 根据优化级别调整天空渲染
        switch (level) {
            case MEDIUM:
                // 中度优化：简化天空效果
                context.fillGradient(0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), 0xFF6B8CE6, 0xFF87CEEB);
                ci.cancel();
                break;
                
            case HIGH:
                // 重度优化：仅渲染基本天空颜色
                context.fill(0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), 0xFF2A4F6B);
                ci.cancel();
                break;
                
            default:
                // 无优化或轻度优化：完整渲染
                break;
        }
    }
}
