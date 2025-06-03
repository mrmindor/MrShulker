package io.github.mrmindor.mrshulker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.mrmindor.mrshulker.MrShulker;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config", "mrshulker_server.json");
    private static final boolean DEFAULT_ALLOW_DYEING = true;
    private static final boolean DEFAULT_ALLOW_PER_SHULKER_SCALING = true;
    public boolean allowDyeing = true;
    public boolean allowPerShulkerScaling = true;

    public void setAllowDyeing(boolean allowance){
        allowDyeing = allowance;
        save();
    }
    public boolean isDyeingAllowed(){
        return allowDyeing;
    }
    public void resetAllowDyeing(){
        setAllowDyeing(DEFAULT_ALLOW_DYEING);
    }
    public void setAllowPerShulkerScaling(boolean allowance){
        allowPerShulkerScaling = allowance;
        save();
    }
    public boolean isPerShulkerScalingAllowed(){
        return allowPerShulkerScaling;
    }
    public void resetAllowPerShulkerScaling() {
        setAllowPerShulkerScaling(DEFAULT_ALLOW_PER_SHULKER_SCALING);
    }

    public void save(){
        save(this);
    }
    public static ServerConfig load() {
        var config = new ServerConfig();
        try {
            if (Files.exists(CONFIG_PATH)) {
                Reader reader = Files.newBufferedReader(CONFIG_PATH);
                config = GSON.fromJson(reader, ServerConfig.class);
                reader.close();

            } else {
                save(config); // Save default if it doesn't exist
            }
        } catch (IOException e) {
            MrShulker.LOGGER.error("Failed to load MrShulker Server config: ",e);
        }
        return config;
    }
    public static void save(ServerConfig config) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Writer writer = Files.newBufferedWriter(CONFIG_PATH);
            GSON.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            MrShulker.LOGGER.error("Failed to save MrShulker Server config: ", e);
        }
    }



}
