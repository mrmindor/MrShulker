package io.github.mrmindor.mrshulker.component;



import io.github.mrmindor.mrshulker.MrShulker;

public class ModComponents {

    protected static void initialize() {
        MrShulker.LOGGER.info("Registering {} components", MrShulker.MOD_ID);
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }

    public static final String LID_ITEM = "lidItem";
    public static final String LID_ITEM_CUSTOM_SCALE = "lid_item_custom_scale";
    public static final String COMPAT_DISPLAY = "Display";

}
