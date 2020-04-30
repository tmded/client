package me.zeroeightsix.kami.module.modules.movement

import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.setting.Settings
import net.minecraft.util.EnumFacing

/**
 * @author dominikaaaa
 * TODO: Implement keeping acceleration and 'stacking' it, creating exponential momentum
 */
@Module.Info(
        name = "WallSpeed",
        description = "Gives you a boost when running beside a wall!",
        category = Module.Category.MOVEMENT
)
class WallSpeed : Module() {
    private val speed = register(Settings.integerBuilder("Speed").withMinimum(1).withMaximum(100).withValue(60).build())

    override fun onUpdate() {
        if (mc.player.collidedHorizontally
                && mc.player.onGround
                && !mc.player.isOnLadder
                && !mc.player.isInWater
                && !mc.player.isInLava) {
            mc.player.collidedHorizontally = false
            when (mc.player.horizontalFacing) {
                EnumFacing.NORTH -> mc.player.motionZ = -(speed.value / 100.0)
                EnumFacing.SOUTH -> mc.player.motionZ = speed.value / 100.0
                EnumFacing.EAST -> mc.player.motionX = speed.value / 100.0
                EnumFacing.WEST -> mc.player.motionX = -(speed.value / 100.0)
                else -> return
            }
        }
    }
}