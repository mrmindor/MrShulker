package org.mrmindor.mrshulker.component;


import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.mrmindor.mrshulker.MrShulker;

public class ModComponents {
    protected static void initialize() {
        MrShulker.LOGGER.info("Registering {} components", org.mrmindor.mrshulker.MrShulker.MOD_ID);
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }
    public static final ComponentType<String> SHULKER_LID_MODEL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(org.mrmindor.mrshulker.MrShulker.MOD_ID, "shulker_lid_model"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

}
