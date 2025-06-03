package io.github.mrmindor.mrshulker.client;

import com.mojang.brigadier.CommandDispatcher;
import io.github.mrmindor.mrshulker.MrShulker;
import io.github.mrmindor.mrshulker.client.command.ClientCommands;
import io.github.mrmindor.mrshulker.client.config.ClientConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;


public class MrShulkerClient implements ClientModInitializer {

    public static ClientConfig Config;// = new ClientConfig();
    @Override
    public void onInitializeClient() {
        MrShulker.LOGGER.info("MrShulkerClient.onInitializeClient() start");
        Config = ClientConfig.load();
        ClientCommandRegistrationCallback.EVENT.register(MrShulkerClient::registerClientCommands);
        MrShulker.LOGGER.info("MrShulkerClient.onInitializeClient() finish");
    }

    private static void registerClientCommands(CommandDispatcher<FabricClientCommandSource> fabricClientCommandSourceCommandDispatcher, CommandBuildContext commandBuildContext) {
        MrShulker.LOGGER.info("registering client commands");
        ClientCommands.registerClientCommands(fabricClientCommandSourceCommandDispatcher, commandBuildContext);
        MrShulker.LOGGER.info("client command registration complete.");
    }






}
