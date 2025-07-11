package io.github.mrmindor.mrshulker.client;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public interface IShulkerRendererLidItem {
    CustomData getCustomData();
    ItemStack getStack();
    void setStack(ItemStack stack);
}
