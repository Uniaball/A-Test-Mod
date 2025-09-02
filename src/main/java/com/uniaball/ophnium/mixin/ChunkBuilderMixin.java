package com.uniaball.ophnium.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.uniaball.ophnium.OptimizationManager;
import com.uniaball.ophnium.OptimizationManager.OptimizationLevel;

@Mixin(ChunkBuilder.class)
public abstract class ChunkBuilderMixin {
    @Inject(
        method = "scheduleRebuild",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onScheduleRebuild(int x, int y, int z, boolean important, CallbackInfo ci) {
        OptimizationLevel level = OptimizationManager.getCurrentLevel();
        
        // 无优化时不做处理
        if (level == OptimizationManager.OptimizationLevel.NONE) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            Vec3d playerPos = client.player.getPos();
            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
            Vec3d chunkCenter = new Vec3d(
                chunkPos.getStartX() + 8,
                y,
                chunkPos.getStartZ() + 8
            );
            
            double distance = playerPos.distanceTo(chunkCenter);
            double maxDistance = 0;
            
            // 根据优化级别设置最大距离
            switch (level) {
                case LOW:
                    maxDistance = 64; // 64格以外不重建
                    break;
                case MEDIUM:
                    maxDistance = 48; // 48格以外不重建
                    break;
                case HIGH:
                    maxDistance = 32; // 32格以外不重建
                    break;
            }
            
            // 超出距离的区块跳过重建
            if (distance > maxDistance) {
                ci.cancel();
            }
        }
    }
}
