package io.github.mrmindor.mrshulker.client.command;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.mrmindor.mrshulker.MrShulker;
import io.github.mrmindor.mrshulker.client.MrShulkerClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ClientCommands {
    public static final String COMMAND_MRSHULKER = "mrshulker_display";
    public static final String SUB_COMMAND_SET = "set";
    public static final String SUB_COMMAND_SCALE = "scale";
    public static final String SUB_COMMAND_QUERY = "query";
    public static final String SUB_COMMAND_RESET = "reset";
    public static final String SUB_COMMAND_SHOW_CUSTOM_SCALES = "show_custom_scales";

    public static final String ARGUMENT_DISPLAY_CONTEXT = "display_context";
    public static final String ARGUMENT_SCALE = "scale";
    public static final String ARGUMENT_SHOW_CUSTOM_SCALES = "show_custom_scales";
    public static final List<String> validDisplayContexts = List.of(
            ItemDisplayContext.NONE.getSerializedName(),
            ItemDisplayContext.THIRD_PERSON_LEFT_HAND.getSerializedName(),
            ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.getSerializedName(),
            ItemDisplayContext.FIRST_PERSON_LEFT_HAND.getSerializedName(),
            ItemDisplayContext.FIRST_PERSON_RIGHT_HAND.getSerializedName(),
            ItemDisplayContext.HEAD.getSerializedName(),
            ItemDisplayContext.GUI.getSerializedName(),
            ItemDisplayContext.GROUND.getSerializedName(),
            ItemDisplayContext.FIXED.getSerializedName(),
            "block",
            "default");

    public static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext ignoredCommandBuildContext) {

        MrShulker.LOGGER.info("logging client side commands");
        var builder = ClientCommandManager.literal(COMMAND_MRSHULKER)
                .then(ClientCommandManager .literal(SUB_COMMAND_SET)
                    .then(ClientCommandManager .literal(SUB_COMMAND_SCALE)
                        .then(ClientCommandManager .argument(ARGUMENT_DISPLAY_CONTEXT, StringArgumentType.word())
                            .suggests(ItemDisplayContextSuggestions)
                            .then(ClientCommandManager .argument(ARGUMENT_SCALE, FloatArgumentType.floatArg(0.25f, 2.0f))
                                .executes(ClientCommands::setLidItemScale)
                            )
                        )
                    ).then(ClientCommandManager .literal(SUB_COMMAND_SHOW_CUSTOM_SCALES)
                        .then(ClientCommandManager .argument(ARGUMENT_SHOW_CUSTOM_SCALES, BoolArgumentType.bool())
                                .executes(ClientCommands::setShowCustomScales)
                        )
                    )
                )
                .then(ClientCommandManager .literal(SUB_COMMAND_QUERY)
                    .then(ClientCommandManager .literal(SUB_COMMAND_SCALE)
                        .executes(ClientCommands::queryDisplayContextScales)
                        .then(ClientCommandManager .argument(ARGUMENT_DISPLAY_CONTEXT, StringArgumentType.word())
                            .suggests(ItemDisplayContextSuggestions)
                            .executes(ClientCommands::queryDisplayContextScale)
                        )
                    )
                    .then(ClientCommandManager .literal(SUB_COMMAND_SHOW_CUSTOM_SCALES)
                        .executes(ClientCommands::queryShowCustomScale)
                    )
                )
                .then(ClientCommandManager .literal(SUB_COMMAND_RESET)
                    .then(ClientCommandManager .literal(SUB_COMMAND_SCALE)
                        .executes(ClientCommands::ResetDisplayContextScales)
                        .then(ClientCommandManager .argument(ARGUMENT_DISPLAY_CONTEXT, StringArgumentType.word())
                            .suggests(ItemDisplayContextSuggestions)
                            .executes(ClientCommands::ResetDisplayContextScale)
                        )
                    )
                    .then(ClientCommandManager .literal(SUB_COMMAND_SHOW_CUSTOM_SCALES)
                        .executes(ClientCommands::ResetShowCustomScales)
                    )
                );



        dispatcher.register(builder);
    }


    private static int ResetShowCustomScales(CommandContext<FabricClientCommandSource> commandContext) {
        MrShulkerClient.Config.resetShowCustomScales();
        return 1;

    }

    private static int ResetDisplayContextScale(CommandContext<FabricClientCommandSource> commandContext) {
        var itemDisplayContext = StringArgumentType.getString(commandContext, ARGUMENT_DISPLAY_CONTEXT);
        if(validDisplayContexts.contains(itemDisplayContext)) {
            MrShulkerClient.Config.resetLidItemScale(itemDisplayContext);
            return 1;
        }
        commandContext.getSource().sendError(Component.literal("%s is not a valid displayContext".formatted(itemDisplayContext)));
        return -1;
    }

    private static int ResetDisplayContextScales(CommandContext<FabricClientCommandSource> commandContext) {

        MrShulkerClient.Config.resetLidItemScales();
        return 1;
    }

    private static int queryShowCustomScale(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Component.literal("show_custom_scales: %b".formatted(MrShulkerClient.Config.getShowCustomScales())));
        return 1;
    }

    private static int setShowCustomScales(CommandContext<FabricClientCommandSource> context) {
        var showCustomScales = BoolArgumentType.getBool(context, "show_custom_scales");
        MrShulkerClient.Config.setShowCustomScales(showCustomScales);
        context.getSource().sendFeedback(Component.literal("show_custom_scales: %b".formatted(MrShulkerClient.Config.getShowCustomScales())));

        return 1;
    }

    private static int queryDisplayContextScale(CommandContext<FabricClientCommandSource> commandContext) {
        var itemDisplayContext = StringArgumentType.getString(commandContext, ARGUMENT_DISPLAY_CONTEXT);
        if(validDisplayContexts.contains(itemDisplayContext)) {
            var scale = MrShulkerClient.Config.getLidItemScale(itemDisplayContext);
            sendScaleMessage(commandContext, itemDisplayContext, scale);
            return 1;
        }
        commandContext.getSource().sendError(Component.literal("%s is not a valid displayContext".formatted(itemDisplayContext)));
        return -1;
    }

    private static int queryDisplayContextScales(CommandContext<FabricClientCommandSource> context) {
        MrShulkerClient.Config.getLidItemDisplayContextScales().forEach(
                (key, value) -> sendScaleMessage(context,key, value));
        return 1;
    }
    private static void sendScaleMessage(CommandContext<FabricClientCommandSource> context, String key, Float value){
        context.getSource().sendFeedback(Component.literal("%s : %f".formatted(key, value)));
    }



    private static final @NotNull SuggestionProvider<FabricClientCommandSource> ItemDisplayContextSuggestions = (context, builder) -> {

        var displayContext = builder.getRemaining();
        if(displayContext.isEmpty()) {
            validDisplayContexts.forEach(builder::suggest);
        } else {
            validDisplayContexts.stream().filter(s-> s.startsWith(displayContext)).forEach(builder::suggest);
        }
        return builder.buildFuture();
    };

    private static int setLidItemScale(CommandContext<FabricClientCommandSource> commandContext) {
        var itemDisplayContext = StringArgumentType.getString(commandContext, ARGUMENT_DISPLAY_CONTEXT);
        if(validDisplayContexts.contains(itemDisplayContext)) {
            var scale = FloatArgumentType.getFloat(commandContext, ARGUMENT_SCALE);
            MrShulkerClient.Config.setLidItemScale(itemDisplayContext, scale);
            return 1;
        }
        commandContext.getSource().sendError(Component.literal("%s is not a valid displayContext".formatted(itemDisplayContext)));
        return -1;

    }




}
