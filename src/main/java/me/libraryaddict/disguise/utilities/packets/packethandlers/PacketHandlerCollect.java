package me.libraryaddict.disguise.utilities.packets.packethandlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import me.libraryaddict.disguise.DisguiseConfig;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PlayerWatcher;
import me.libraryaddict.disguise.utilities.packets.IPacketHandler;
import me.libraryaddict.disguise.utilities.packets.LibsPackets;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by libraryaddict on 3/01/2019.
 */
public class PacketHandlerCollect implements IPacketHandler {
    @Override
    public PacketType[] getHandledPackets() {
        return new PacketType[]{PacketType.Play.Server.COLLECT};
    }

    @Override
    public void handle(Disguise disguise, PacketContainer sentPacket, LibsPackets packets, Player observer,
            Entity entity) {
        if (disguise.getType().isMisc()) {
            packets.clear();
        } else if (DisguiseConfig.isBedPacketsEnabled() && disguise.getType().isPlayer() &&
                ((PlayerWatcher) disguise.getWatcher()).isSleeping()) {
            PacketContainer newPacket = new PacketContainer(PacketType.Play.Server.ANIMATION);

            StructureModifier<Integer> mods = newPacket.getIntegers();
            mods.write(0, disguise.getEntity().getEntityId());
            mods.write(1, 3);

            packets.clear();

            packets.addPacket(newPacket);
            packets.addPacket(sentPacket);
        }
    }
}
