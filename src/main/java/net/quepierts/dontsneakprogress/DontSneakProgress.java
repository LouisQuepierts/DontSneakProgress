package net.quepierts.dontsneakprogress;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * @author Louis_Quepierts
 */
@SuppressWarnings("all")
@Mod(DontSneakProgress.MODID)
public class DontSneakProgress {
    public static final String MODID = "dontsneakprogress";

    private static final Map<UUID, GameType> PLAYER_GAME_MODE_MAP = new Object2ObjectOpenHashMap<>();

    public DontSneakProgress() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer) {
            PLAYER_GAME_MODE_MAP.put(serverPlayer.getUUID(), serverPlayer.gameMode.getGameModeForPlayer());
        }

        MinecraftServer server = player.getServer();
        if (server != null) {
            this.updatePlayerMode(server);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        this.PLAYER_GAME_MODE_MAP.remove(player.getUUID());

        MinecraftServer server = player.getServer();
        if (server != null) {
            this.updatePlayerMode(server);
        }
    }

    @SubscribeEvent
    public void onPlayerSwitchGameMode(final PlayerEvent.PlayerChangeGameModeEvent event) {
        Player player = event.getEntity();
        MinecraftServer server = player.getServer();

        if (server != null) {
            if (event.getNewGameMode() != GameType.ADVENTURE && server.getPlayerCount() < Config.minOnlinePlayer) {
                player.sendSystemMessage(Component.literal("Sneak progress? NO WAY!!").withStyle(ChatFormatting.RED));
                event.setNewGameMode(GameType.ADVENTURE);
            }
        }
    }

    public static void updatePlayerMode(@NotNull MinecraftServer server) {
        PlayerList playerList = server.getPlayerList();

        boolean hasEnoughOnlinePlayer = playerList.getPlayerCount() < Config.minOnlinePlayer;

        if (hasEnoughOnlinePlayer) {
            for (ServerPlayer player : playerList.getPlayers()) {
                player.setGameMode(PLAYER_GAME_MODE_MAP.getOrDefault(player.getUUID(), GameType.SURVIVAL));
            }
        } else {
            String message = "There are not enough player online [%d/%d]".formatted(playerList.getPlayerCount(), Config.minOnlinePlayer);
            for (ServerPlayer player : playerList.getPlayers()) {
                PLAYER_GAME_MODE_MAP.put(player.getUUID(), player.gameMode.getGameModeForPlayer());
                player.setGameMode(GameType.ADVENTURE);
                player.sendSystemMessage(Component.literal(message));
            }
        }
    }
}
