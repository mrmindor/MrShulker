package io.github.mrmindor.mrshulker.client.map;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.Optional;

public class mapUtil {
    public static Optional<MapItemSavedData> tryGetMapData(ItemStack lidItem, Minecraft minecraftClient){
        if(lidItem.has(DataComponents.MAP_ID)){
            var mapId = lidItem.get(DataComponents.MAP_ID);
            if(minecraftClient.level != null) {
                return Optional.ofNullable(minecraftClient.level.getMapData(mapId));
            }
        }
        return Optional.empty();
    }
}
