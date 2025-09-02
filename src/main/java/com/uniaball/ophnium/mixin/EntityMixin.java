package com.uniaball.ophnium.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.uniaball.ophnium.OptimizationManager;
import com.uniaball.ophnium.OptimizationManager.OptimizationLevel;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        OptimizationLevel level = OptimizationManager.getCurrentLevel();
        
        // 无优化时不做处理
        if (level == OptimizationManager.OptimizationLevel.NONE) return;
        
        Entity self = (Entity) (Object) this;
        
        // 根据优化级别设置跳过距离
        double skipDistance = 0;
        switch (level) {
            case LOW:
                skipDistance = 64; // 64格以外跳过
                break;
            case MEDIUM:
                skipDistance = 48; // 48格以外跳过
                break;
            case HIGH:
                skipDistance = 32; // 32格以外跳过
                break;
        }
        
        // 获取玩家位置
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        // 计算距离
        double distanceSq = self.squaredDistanceTo(client.player);
        double skipDistanceSq = skipDistance * skipDistance;
        
        // 如果实体在视野外且距离玩家很远，跳过碰撞检测
        if (!self.isPlayer() && distanceSq > skipDistanceSq) {
            ci.cancel();
        }
    }
}
