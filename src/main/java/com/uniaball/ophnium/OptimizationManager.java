package com.uniaball.ophnium;

public class OptimizationManager {
    // 优化级别枚举
    public enum OptimizationLevel {
        NONE,       // 无优化
        LOW,        // 轻度优化 (FPS 45-60)
        MEDIUM,     // 中度优化 (FPS 30-45)
        HIGH        // 重度优化 (FPS <30)
    }
    
    // 当前优化级别
    private static volatile OptimizationLevel currentLevel = OptimizationLevel.NONE;
    
    // 最后检测时间
    private static volatile long lastCheckTime = 0;
    
    // 更新优化级别（返回新级别）
    public static OptimizationLevel updateOptimizationLevel(int currentFPS) {
        // 根据帧率确定优化级别
        OptimizationLevel newLevel;
        if (currentFPS >= 60) {
            newLevel = OptimizationLevel.NONE;
        } else if (currentFPS >= 45) {
            newLevel = OptimizationLevel.LOW;
        } else if (currentFPS >= 30) {
            newLevel = OptimizationLevel.MEDIUM;
        } else {
            newLevel = OptimizationLevel.HIGH;
        }
        
        // 更新当前级别
        currentLevel = newLevel;
        return newLevel;
    }
    
    // 应用优化级别
    public static void applyOptimizationLevel(OptimizationLevel level) {
        // 这里可以添加任何需要全局调整的设置
        // 例如：调整渲染距离、粒子效果等
        
        // 记录优化级别变化
        Ophnium.LOGGER.info("Optimization level changed to: " + level);
    }
    
    // 获取当前优化级别
    public static OptimizationLevel getCurrentLevel() {
        return currentLevel;
    }
}
