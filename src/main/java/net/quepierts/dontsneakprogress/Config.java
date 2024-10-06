package net.quepierts.dontsneakprogress;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * @author Louis_Quepierts
 */
@Mod.EventBusSubscriber(modid = DontSneakProgress.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER;
    private static final ForgeConfigSpec.IntValue MIN_ONLINE_PLAYER;

    static final ForgeConfigSpec SPEC;

    public static int minOnlinePlayer;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        minOnlinePlayer = MIN_ONLINE_PLAYER.get();
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {
        minOnlinePlayer = MIN_ONLINE_PLAYER.get();
    }

    @SubscribeEvent
    static void onUnload(final ModConfigEvent.Unloading event) {
        MIN_ONLINE_PLAYER.set(minOnlinePlayer);
    }

    static {
        BUILDER = new ForgeConfigSpec.Builder();
        MIN_ONLINE_PLAYER = BUILDER.comment(
                "The minimum number of online players you expect",
                "If the number of online players is less than this, all online players will be switched to Adventure Mode"
        ).defineInRange("minOnlinePlayer", 3, 0, Integer.MAX_VALUE);
        SPEC = BUILDER.build();
    }
}
