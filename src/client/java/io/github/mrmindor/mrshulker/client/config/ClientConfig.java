package io.github.mrmindor.mrshulker.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.mrmindor.mrshulker.MrShulker;
import net.minecraft.world.item.ItemDisplayContext;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;

public class ClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config", "mrshulker_client.json");
    public static final float DEFAULT_DEFAULT_SCALE = 0.5f;
    public static final float DEFAULT_GUI_SCALE = 0.75f;
    public static final boolean DEFAULT_SHOW_CUSTOM_SCALES = true;
    private final Hashtable<String,Float> lidItemScales;
    private Boolean showCustomScales = true;

    public ClientConfig() {
        this.lidItemScales = new Hashtable<>();
        this.lidItemScales.putIfAbsent("default", DEFAULT_DEFAULT_SCALE);
        this.lidItemScales.putIfAbsent(ItemDisplayContext.GUI.getSerializedName(), DEFAULT_GUI_SCALE);
    }

    public Float getLidItemScale(String displayContext){
        if(lidItemScales.containsKey(displayContext)){
            return lidItemScales.get(displayContext);
        }
        return lidItemScales.get("default");
    }
    public void setLidItemScale(String displayContext, Float scale){
        lidItemScales.put(displayContext, scale);
        save();
    }
    public Hashtable<String,Float> getLidItemDisplayContextScales(){
        return lidItemScales;
    }

    public void setShowCustomScales(Boolean show){
        showCustomScales = show;
        save();
    }
    public boolean getShowCustomScales(){
        return showCustomScales;
    }
    public void resetLidItemScales() {
        lidItemScales.clear();
        lidItemScales.put("default", DEFAULT_DEFAULT_SCALE);
        lidItemScales.put(ItemDisplayContext.GUI.getSerializedName(), DEFAULT_GUI_SCALE);
        save();
    }
    public void resetLidItemScale(String displayContext){
        lidItemScales.remove(displayContext);
        if(displayContext.equals("default")){
            lidItemScales.put("default", DEFAULT_DEFAULT_SCALE);
        }
        if(displayContext.equals(ItemDisplayContext.GUI.getSerializedName())){
            lidItemScales.put(ItemDisplayContext.GUI.getSerializedName(), DEFAULT_GUI_SCALE);
        }
        save();
    }
    public void resetShowCustomScales(){
        setShowCustomScales(DEFAULT_SHOW_CUSTOM_SCALES);
    }

    public void save(){
        save(this);
    }

    public static ClientConfig load() {
        var config = new ClientConfig();
        try {
            if (Files.exists(CONFIG_PATH)) {
                Reader reader = Files.newBufferedReader(CONFIG_PATH);
                config = GSON.fromJson(reader, ClientConfig.class);
                reader.close();

            } else {
                save(config); // Save default if it doesn't exist
            }
        } catch (IOException e) {
            MrShulker.LOGGER.error("Failed to load MrShulker Client config: ",e);
        }
        return config;
    }
    public static void save(ClientConfig config) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Writer writer = Files.newBufferedWriter(CONFIG_PATH);
            GSON.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            MrShulker.LOGGER.error("Failed to save MrShulker Client config: ", e);
        }
    }


}
