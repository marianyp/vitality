package dev.mariany.vitality.packet.serverbound;

import dev.mariany.vitality.Vitality;
import dev.mariany.vitality.logic.DoubleJumpLogic;
import dev.mariany.vitality.packet.clientbound.DoubleJumpedPacket;
import dev.mariany.vitality.util.VitalityUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record DoubleJumpPacket() implements CustomPayload {
    public static final CustomPayload.Id<DoubleJumpPacket> ID = new CustomPayload.Id<>(Vitality.id("double_jump"));
    public static final PacketCodec<RegistryByteBuf, DoubleJumpPacket> CODEC = PacketCodec.unit(new DoubleJumpPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(DoubleJumpPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        ServerWorld world = player.getServerWorld();

        if (VitalityUtils.canDoubleJump(player)) {
            Vec3d movement = DoubleJumpLogic.doubleJump(player);

            for (int i = 0; i < 8; ++i) {
                double motionX = player.getRandom().nextGaussian() * 0.02;
                double motionY = player.getRandom().nextGaussian() * 0.02 + 0.20;
                double motionZ = player.getRandom().nextGaussian() * 0.02;

                ParticleEffect particleType = player.isSubmergedInWater() ? ParticleTypes.BUBBLE : ParticleTypes.POOF;
                world.spawnParticles(particleType, player.getX(), player.getY(), player.getZ(), 1, motionX, motionY,
                        motionZ, 0.15);
            }

            Packet<ClientCommonPacketListener> doubleJumpedPacket = ServerPlayNetworking.createS2CPacket(
                    new DoubleJumpedPacket(player.getId(), movement.x, movement.y, movement.z));

            world.getChunkManager().sendToOtherNearbyPlayers(player, doubleJumpedPacket);
        }
    }
}
