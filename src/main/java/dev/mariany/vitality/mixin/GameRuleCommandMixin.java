package dev.mariany.vitality.mixin;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.mariany.vitality.gamerule.VitalityGamerules;
import dev.mariany.vitality.packet.clientbound.BooleanGameruleUpdatePacket;
import dev.mariany.vitality.packet.clientbound.IntGameruleUpdatePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRuleCommand.class)
public class GameRuleCommandMixin {
    @Inject(method = "executeSet", at = @At(value = "TAIL"))
    private static <T extends GameRules.Rule<T>> void executeSet(CommandContext<ServerCommandSource> context,
                                                                 GameRules.Key<T> key,
                                                                 CallbackInfoReturnable<Integer> cir) {
        ServerCommandSource serverCommandSource = context.getSource();
        MinecraftServer server = serverCommandSource.getServer();

        VitalityGamerules.BOOLEAN_RULES.stream().filter(intKey -> intKey.equals(key)).findFirst().ifPresent(
                gamerule -> updateBooleanGamerule(gamerule, BoolArgumentType.getBool(context, "value"), server));

        VitalityGamerules.INT_RULES.stream().filter(intKey -> intKey.equals(key)).findFirst().ifPresent(
                gamerule -> updateIntGamerule(gamerule, IntegerArgumentType.getInteger(context, "value"), server));
    }

    @Unique
    private static void updateBooleanGamerule(GameRules.Key<GameRules.BooleanRule> gamerule, boolean value,
                                              MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new BooleanGameruleUpdatePacket(gamerule.getName(), value));
        }
    }

    @Unique
    private static void updateIntGamerule(GameRules.Key<GameRules.IntRule> gamerule, int value,
                                          MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new IntGameruleUpdatePacket(gamerule.getName(), value));
        }
    }
}
