package io.github.mrmindor.mrshulker;

import com.mojang.brigadier.CommandDispatcher;
import io.github.mrmindor.mrshulker.command.ServerCommands;
import io.github.mrmindor.mrshulker.config.ServerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MrShulker implements ModInitializer {

    public static final String MOD_ID = "mrshulker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ServerConfig Config;


    @Override
    public void onInitialize() {
        MrShulker.LOGGER.info("MrShulker.onInitialize() start");
        Config = ServerConfig.load();
        CommandRegistrationCallback.EVENT.register(MrShulker::registerServerCommands);
        MrShulker.LOGGER.info("MrShulker.onInitialize() finish");

    }

    private static void registerServerCommands(CommandDispatcher<CommandSourceStack> commandSourceStackCommandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        MrShulker.LOGGER.info("registering stuff.");
        ServerCommands.registerServerCommands(commandSourceStackCommandDispatcher,commandBuildContext,commandSelection);
    }


}
