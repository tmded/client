package me.zeroeightsix.kami.module.modules.movement

import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.util.MathsUtils.CardinalMain
import me.zeroeightsix.kami.util.MathsUtils.getPlayerMainCardinal
import me.zeroeightsix.kami.util.MessageSendHelper.sendChatMessage
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk

@Module.Info(name = "Avoid", description = "Prevents going into unloaded chunks", category = Module.Category.MOVEMENT)
class Avoid() : Module() {
    //    private val render = register(Settings.b("Show Warning"))
    private var unLoaded = CardinalMain.POS_X
    private var surroundingChunks: ArrayList<Chunk>? = null

    override fun onUpdate() {
        if (mc.player == null) return

        surroundingChunks = updateSurrounding()
        surroundingChunks?.let {
            for (i in 1..surroundingChunks!!.size) {
                if (surroundingChunks!![i].isLoaded) { // TODO: remember to invert
                    when (i) {
                        1 -> unLoaded = CardinalMain.POS_X
                        2 -> unLoaded = CardinalMain.NEG_X
                        3 -> unLoaded = CardinalMain.POS_Z
                        4 -> unLoaded = CardinalMain.NEG_Z
                    }
                    continue
                }
            }
            sendChatMessage(getDirection())
        }
    }

    private fun getDirection(): String {
        val playerCardinal = getPlayerMainCardinal(mc)
        return when (playerCardinal.ordinal - 4 % 2) {
            1 -> "one"
            2 -> "two"
            3 -> "three"
            4 -> "four"
            else -> "error"
        }
    }

    private fun updateSurrounding(): ArrayList<Chunk>? {
        val chunks: ArrayList<Chunk>? = null
        chunks?.clear()
        chunks?.add(mc.world.getChunk(BlockPos(mc.player.posX + 16, mc.player.posY, mc.player.posZ)))
        chunks?.add(mc.world.getChunk(BlockPos(mc.player.posX - 16, mc.player.posY, mc.player.posZ)))
        chunks?.add(mc.world.getChunk(BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ + 16)))
        chunks?.add(mc.world.getChunk(BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ - 16)))
        return chunks
    }
}