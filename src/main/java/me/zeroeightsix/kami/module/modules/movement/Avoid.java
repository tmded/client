package me.zeroeightsix.kami.module.modules.movement;

import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Coordinate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import static me.zeroeightsix.kami.util.MessageSendHelper.sendChatMessage;
import static org.lwjgl.opengl.GL11.*;

@Module.Info(
        name = "Avoid",
        description = "Prevents going into unloaded chunks",
        category = Module.Category.MOVEMENT
)
public class Avoid extends Module {
    private Setting<Integer> yOffset = register(Settings.i("Y Offset", 0));
    private Setting<Boolean> relative = register(Settings.b("Relative", true));

//    private static ArrayList<Chunk> notLoadedChunks = new ArrayList<>();

    private static ArrayList<Chunk> surroundingChunks = new ArrayList<>();
    private static ArrayList<Coordinate> surroundingChunksCoords = new ArrayList<>();
    private static ArrayList<Coordinate> notLoadedChunks = new ArrayList<>();
    private static boolean dirty = true;
    private int list = GL11.glGenLists(1);

//    @EventHandler // really hacky solution in order to get if the chunk is loaded or not. forge doesn't fire proper events for finished loading, it only fires an event when starting loading
//    public Listener<ChunkEvent> listener = new Listener<>(event -> {
//        if (mc.world.getChunk(event.getPacket().getChunkX(), event.getPacket().getChunkZ()).isTerrainPopulated()) {
//            notLoadedChunks.add(event.getChunk());
//            dirty = true;
//            sendChatMessage("Found unloaded chunk at " + event.getPacket().getChunkX() + " " + event.getPacket().getChunkZ());
//        } else if (!mc.world.getChunk(event.getPacket().getChunkX(), event.getPacket().getChunkZ()).isTerrainPopulated()) {
//            notLoadedChunks.remove(event.getChunk());
//            dirty = false;
//            sendChatMessage("Removed loaded chunk at " + event.getPacket().getChunkX() + " " + event.getPacket().getChunkZ());
//        }
//    });

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        getSurroundingChunks();
        for (int i = 0 ; i < surroundingChunks.size() ; i++) {
            if (surroundingChunks.get(i).isLoaded()) {
                notLoadedChunks.remove(surroundingChunksCoords.get(i));
                sendChatMessage("Chunk loaded: " + surroundingChunksCoords.get(i).x + " " + surroundingChunksCoords.get(i).z);
            } else {
                notLoadedChunks.add(surroundingChunksCoords.get(i));
                sendChatMessage("Chunk not loaded: " + surroundingChunksCoords.get(i).x + " " + surroundingChunksCoords.get(i).z);
            }
        }
    }

    private void getSurroundingChunks() {
        surroundingChunks.clear();
        surroundingChunks.add(mc.world.getChunk(new BlockPos(mc.player.posX + 16, mc.player.posY, mc.player.posZ)));
        surroundingChunks.add(mc.world.getChunk(new BlockPos(mc.player.posX - 16, mc.player.posY, mc.player.posZ)));
        surroundingChunks.add(mc.world.getChunk(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ + 16)));
        surroundingChunks.add(mc.world.getChunk(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ - 16)));

        if (mc.getRenderViewEntity() == null) return;
        surroundingChunksCoords.clear();
        surroundingChunksCoords.add(new Coordinate(((int) mc.getRenderViewEntity().posX + 16) & 15, 0, ((int) mc.getRenderViewEntity().posZ) & 15));
        surroundingChunksCoords.add(new Coordinate(((int) mc.getRenderViewEntity().posX - 16) & 15, 0, ((int) mc.getRenderViewEntity().posZ) & 15));
        surroundingChunksCoords.add(new Coordinate(((int) mc.getRenderViewEntity().posX) & 15, 0, ((int) mc.getRenderViewEntity().posZ + 16) & 15));
        surroundingChunksCoords.add(new Coordinate(((int) mc.getRenderViewEntity().posX) & 15, 0, ((int) mc.getRenderViewEntity().posZ - 16) & 15));
//        sendChatMessage(String.format("Chunk: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15));
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (dirty) {
            GL11.glNewList(list, GL11.GL_COMPILE);

            glPushMatrix();
            glEnable(GL_LINE_SMOOTH);
            glLineWidth(1.0F);
            for (Coordinate coordinate : notLoadedChunks) {
                double posX = coordinate.x * 16;
                double posY = 0;
                double posZ = coordinate.z * 16;

                glColor3f(.2f, .1f, .6f);

                glBegin(GL_LINE_LOOP);
                glVertex3d(posX, posY, posZ);
                glVertex3d(posX + 16, posY, posZ);
                glVertex3d(posX + 16, posY, posZ + 16);
                glVertex3d(posX, posY, posZ + 16);
                glVertex3d(posX, posY, posZ);
                glEnd();
            }
            glDisable(GL_LINE_SMOOTH);
            glPopMatrix();
            glColor4f(1, 1, 1, 1);

            GL11.glEndList();
            dirty = false;
        }

        double x = mc.getRenderManager().renderPosX;
        double y = relative.getValue() ? 0 : -mc.getRenderManager().renderPosY;
        double z = mc.getRenderManager().renderPosZ;
        GL11.glTranslated(-x, y + yOffset.getValue(), -z);
        GL11.glCallList(list);
        GL11.glTranslated(x, -(y + yOffset.getValue()), z);
    }

    @Override
    public void destroy() {
        GL11.glDeleteLists(1, 1);
    }
}
