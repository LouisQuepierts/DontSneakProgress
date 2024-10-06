package net.quepierts.dontsneakprogress;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author Louis_Quepierts
 */
@Mod.EventBusSubscriber(modid = DontSneakProgress.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Command {

    @SubscribeEvent
    public static void onRegisterCommand(final RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("dsp")
                        .requires(stack -> stack.hasPermission(2))
                        .then(Commands.literal("set").then(Commands.argument("min_amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(Command::set)))
                        .then(Commands.literal("get").executes(Command::get))
        );
    }

    private static int get(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("The minimum player requirement is " + Config.minOnlinePlayer));
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> context) {
        Config.minOnlinePlayer = context.getArgument("min_amount", Integer.TYPE);
        context.getSource().sendSystemMessage(Component.literal("The minimum player requirement has been set to " + Config.minOnlinePlayer));
        DontSneakProgress.updatePlayerMode(context.getSource().getServer());
        return 1;
    }
}
