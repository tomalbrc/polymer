package eu.pb4.polymer.other;

import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.polymer.PolymerMod;
import eu.pb4.polymer.other.polymc.PolyMcHelpers;
import eu.pb4.polymer.resourcepack.ResourcePackUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

import static net.minecraft.server.command.CommandManager.literal;

@ApiStatus.Internal
public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("polymer")
                .requires((source) -> source.hasPermissionLevel(3))
                .then(literal("generate")
                        .executes((context -> {
                            context.getSource().sendFeedback(new LiteralText("Starting resource pack generation..."), true);

                            Path path = FabricLoader.getInstance().getGameDir().resolve("polymer-resourcepack");
                            path.toFile().mkdirs();

                            try {
                                if (PolymerMod.POLYMC_COMPAT) {
                                    PolyMcHelpers.createResources(path);
                                }
                            } catch (Exception e) {}

                            boolean success = ResourcePackUtils.build(FabricLoader.getInstance().getGameDir().resolve("polymer-resourcepack"));

                            if (success) {
                                context.getSource().sendFeedback(new LiteralText("Resource pack created successfully! You can find it in: " + path), true);
                            } else {
                                context.getSource().sendError(new LiteralText("Found issues while creating resource pack! See logs above for more detail!"));
                            }

                            return 0;
                        })))
        );
    }
}
