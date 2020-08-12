package me.zeroeightsix.kami.module.modules.combat

import me.zeroeightsix.kami.KamiMod
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.setting.Settings
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.ceil

/**
 * Created by 086 on 12/12/2017.
 * Updated by hub on 31 October 2019
 * Updated by bot-debug on 10/04/20
 * Baritone compat added by dominikaaaa on 18/05/20
 * Updated by Xiaro on 02/08/20
 */
@Module.Info(
        name = "BedAura",
        category = Module.Category.COMBAT,
        description = "automatically blows up beds in the nether"
)
class BedAura : Module() {
    private val range = register(Settings.f("Range", 5.5f))

    override fun onUpdate() {
        // check if player is in nether
        if (mc.player.dimension == -1) {
            // get sphere of all blocks of radius $range
            val range = ceil(range.value).toInt()
            val crystalAura = KamiMod.MODULE_MANAGER.getModuleT(CrystalAura::class.java)
            val blockPosList = crystalAura.getSphere(CrystalAura.getPlayerPos(), range.toFloat(), range, false, true, 0)
            // increment over the blocks until one of them is a bed block then right click it
            for (pos in blockPosList) {
                if (mc.world.getBlockState(pos).block != Blocks.BED) continue
                rightClickBlock(pos)
                //break
            }
        }
    }

    private fun rightClickBlock(blockpos: BlockPos) {
        // we use EnumFacing.DOWN as otherwise it can start placing beds on the beds however if you use down there is not space to place.
        val hitVec = Vec3d(blockpos).add(0.5, 0.5, 0.5).add(Vec3d(EnumFacing.DOWN.directionVec).scale(0.5))
        // we un sneak so that the right click registers as an explosion
        mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING))
        // tell out player to right click the block
        mc.playerController.processRightClickBlock(mc.player, mc.world, blockpos, EnumFacing.DOWN,hitVec,EnumHand.MAIN_HAND)
    }
}