package eu.pb4.polymer.virtualentity.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.common.impl.CommonImplUtils;
import eu.pb4.polymer.common.impl.CompatStatus;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


@ApiStatus.Internal
public class VirtualEntityMod implements ModInitializer {
    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        CommonImplUtils.registerDevCommands(this::commands);
        ServerLifecycleEvents.SERVER_STARTING.register(x -> server = x);
    }

    public static void logAsyncAccess() {
        if (!VirtualEntityMod.server.isOnThread()) {
            System.out.println("Error: polymer async access!");

            var configDir = FabricLoader.getInstance().getGameDir().toFile();
            File logFile = new File(configDir, "async_access.log");

            try (FileWriter fw = new FileWriter(logFile, true); PrintWriter pw = new PrintWriter(fw)) {
                pw.println("Async access detected at " + LocalDateTime.now());
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stack) {
                    pw.println("\tat " + element);
                }
                pw.println();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void commands(LiteralArgumentBuilder<ServerCommandSource> builder, CommandRegistryAccess commandRegistryAccess) {
        builder.then(literal("ve_blockbound").then(argument("pos", BlockPosArgumentType.blockPos()).executes((ctx) -> {
            var b = BlockBoundAttachment.get(ctx.getSource().getWorld(), BlockPosArgumentType.getBlockPos(ctx, "pos"));

            if (b == null) {
                ctx.getSource().sendFeedback(() -> Text.literal("No block bound!"), false);
            } else {
                ctx.getSource().sendFeedback(() -> Text.literal("Found: " + b.holder()), false);
                for (var e : b.holder().getElements()) {
                    ctx.getSource().sendFeedback(() -> Text.literal("- " + e), false);
                }
            }


            return b != null ? b.holder().getElements().size() : -1;
        })));
    }
}
