package org.mrmindor.mrshulker;

import net.minecraft.item.ItemStack;

import java.util.Optional;

public interface IShulkerLidItem {
    Optional<ItemStack> getLidItem();
    void setLidItem(ItemStack stack);
    static IShulkerLidItem from(Object object) {return (IShulkerLidItem) object;}
}
