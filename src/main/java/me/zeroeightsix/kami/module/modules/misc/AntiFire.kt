package me.zeroeightsix.kami.module.modules.misc

import me.zeroeightsix.kami.KamiMod
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.module.modules.combat.CrystalAura
import me.zeroeightsix.kami.module.modules.combat.Surround.Companion.faceVectorPacketInstant
import me.zeroeightsix.kami.setting.Settings
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import kotlin.math.atan2
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
    private val range = 5.0f

    override fun onUpdate() {
        // get sphere of all blocks of radius $range
        val range = floor(range).toInt()
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
        // some servers anti cheat doesnt let you hit things you aren't facing
        faceVectorPacketInstant(hitVec)
        mc.playerController.clickBlock(blockpos, facing)
    }
}