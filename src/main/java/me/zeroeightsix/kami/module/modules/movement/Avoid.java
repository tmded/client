package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Coordinate;
import me.zeroeightsix.kami.util.MathsUtils;
import me.zeroeightsix.kami.util.MathsUtils.CardinalMain;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import static me.zeroeightsix.kami.util.MathsUtils.getPlayerMainCardinal;
import static me.zeroeightsix.kami.util.MathsUtils.isBetween;
import static me.zeroeightsix.kami.util.MessageSendHelper.sendChatMessage;
import static org.lwjgl.opengl.GL11.*;

@Module.Info(
        name = "Avoid",
        description = "Prevents going into unloaded chunks",
        category = Module.Category.MOVEMENT
)
public class Avoid extends Module {
//    private Setting<Integer> yOffset = register(Settings.i("Y Offset", 0));
//    private Setting<Boolean> relative = register(Settings.b("Relative", true));

    private static ArrayList<Chunk> surroundingChunks = new ArrayList<>();
    private CardinalMain unloaded = CardinalMain.POS_X;

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

//        getSurroundingChunks();
//        for (int i = 0 ; i < surroundingChunks.size() ; i++) {
//            if (surroundingChunks.get(i).isLoaded()) { // TODO: remember to invert
//                switch (i) {
//                    case 0: // +X
//                        unloaded = CardinalMain.POS_X;
//                        break;
//                    case 1: // -X
//                        unloaded = CardinalMain.NEG_X;
//                        break;
//                    case 2: // +Z
//                        unloaded = CardinalMain.POS_Z;
//                        break;
//                    case 3: // -Z`
//                        unloaded = CardinalMain.NEG_Z;
//                        break;
//                }
//            }
//        }
    }
    private void getSurroundingChunks() {
        surroundingChunks.clear();
        surroundingChunks.add(mc.world.getChunk(new BlockPos(mc.player.posX + 16, mc.player.posY, mc.player.posZ)));
        surroundingChunks.add(mc.world.getChunk(new BlockPos(mc.player.posX - 16, mc.player.posY, mc.player.posZ)));
        surroundingChunks.add(mc.world.getChunk(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ + 16)));
        surroundingChunks.add(mc.world.getChunk(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ - 16)));
    }

}
