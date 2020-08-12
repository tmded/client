package me.zeroeightsix.kami.module.modules.misc

import me.zeroeightsix.kami.KamiMod
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.module.modules.combat.CrystalAura
import me.zeroeightsix.kami.module.modules.combat.Surround
import me.zeroeightsix.kami.setting.Settings
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Created by 086 on 12/12/2017.
 * Updated by hub on 31 October 2019
 * Updated by bot-debug on 10/04/20
 * Baritone compat added by dominikaaaa on 18/05/20
 * Updated by Xiaro on 02/08/20
 */
@Module.Info(
        name = "AntiFire",
        category = Module.Category.MISC,
        description = "Automatically snuff out fire"
)
class AntiFire : Module() {
    private val range = register(Settings.f("Range", 5.5f))

    override fun onUpdate() {
        // get sphere of all blocks of radius $range
        val range = floor(range.value).toInt()
        val crystalAura = KamiMod.MODULE_MANAGER.getModuleT(CrystalAura::class.java)
        val blockPosList = crystalAura.getSphere(CrystalAura.getPlayerPos(), range.toFloat(), range, false, true, 0)
        // increment over the blocks until one of them is a bed block then right click it
        for (pos in blockPosList) {
            if (mc.world.getBlockState(pos).block != Blocks.FIRE) continue
            // this direction seems to work for all fire location however there may be somewhere it breaks
            leftClickBlock(pos, EnumFacing.DOWN)
        }
    }

    private fun leftClickBlock(blockpos: BlockPos, facing:EnumFacing) {
        // we un sneak so that the right click registers as an explosion
        val hitVec = Vec3d(blockpos).add(0.5, 0.5, 0.5).add(Vec3d(EnumFacing.DOWN.directionVec).scale(0.5))
        faceVectorPacketInstant(hitVec)
        mc.playerController.clickBlock(blockpos, facing)
    }

    private fun faceVectorPacketInstant(vec: Vec3d) {
        val rotations = getLegitRotations(vec)
        mc.player.connection.sendPacket(CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround))
    }

    private fun getLegitRotations(vec: Vec3d): FloatArray {
        val eyesPos = eyesPos
        val diffX = vec.x - eyesPos.x
        val diffY = vec.y - eyesPos.y
        val diffZ = vec.z - eyesPos.z

        val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)
        val yaw = Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90.0f
        val pitch = (-Math.toDegrees(atan2(diffY, diffXZ))).toFloat()

        return floatArrayOf(mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch))
    }

    private val eyesPos: Vec3d
        get() = Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight().toDouble(), mc.player.posZ)
}