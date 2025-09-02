package com.uniaball.ophnium.mixin;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.uniaball.ophnium.OptimizationManager;
import com.uniaball.ophnium.OptimizationManager.OptimizationLevel;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    @Shadow
    protected ClientWorld world;
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        OptimizationLevel level = OptimizationManager.getCurrentLevel();
        
        // 无优化时不做处理
        if (level == OptimizationManager.OptimizationLevel.NONE) return;
        
        // 根据优化级别设置最大粒子距离
        double maxDistance = 0;
        switch (level) {
            case LOW:
                maxDistance = 96; // 96格以外移除
                break;
            case MEDIUM:
                maxDistance = 64; // 64格以外移除
                break;
            case HIGH:
                maxDistance = 32; // 32格以外移除
                break;
        }
        
        // 移除远处的粒子
        if (world != null && world.getCameraEntity() != null) {
            ParticleManager self = (ParticleManager) (Object) this;
            self.getParticles().removeIf(particle -> 
                particle.getDistanceTo(world.getCameraEntity()) > maxDistance
            );
        }
    }
}
