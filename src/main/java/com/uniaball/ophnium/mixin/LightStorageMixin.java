package com.uniaball.ophnium.mixin;

import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.uniaball.ophnium.OptimizationManager;
import com.uniaball.ophnium.OptimizationManager.OptimizationLevel;

@Mixin(LightStorage.class)
public abstract class LightStorageMixin {
    @Inject(
        method = "updateLight",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onUpdateLight(CallbackInfoReturnable<Boolean> cir) {
        OptimizationLevel level = OptimizationManager.getCurrentLevel();
        
        // 根据优化级别动态调整
        switch (level) {
            case LOW:
                // 轻度优化：跳过20%的光照更新
                if (Math.random() < 0.2) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
                break;
                
            case MEDIUM:
                // 中度优化：跳过40%的光照更新
                if (Math.random() < 0.4) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
                break;
                
            case HIGH:
                // 重度优化：跳过60%的光照更新
                if (Math.random() < 0.6) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
                break;
                
            default:
                // 无优化：完全更新
                break;
        }
    }
    
    @Inject(
        method = "propagateLight",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onPropagateLight(CallbackInfoReturnable<Boolean> cir) {
        OptimizationLevel level = OptimizationManager.getCurrentLevel();
        
        // 根据优化级别动态调整
        switch (level) {
            case LOW:
                // 轻度优化：跳过10%的光照传播
                if (Math.random() < 0.1) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
                break;
                
            case MEDIUM:
                // 中度优化：跳过25%的光照传播
                if (Math.random() < 0.25) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
                break;
                
            case HIGH:
                // 重度优化：跳过40%的光照传播
                if (Math.random() < 0.4) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
                break;
                
            default:
                // 无优化：完全传播
                break;
        }
    }
}
