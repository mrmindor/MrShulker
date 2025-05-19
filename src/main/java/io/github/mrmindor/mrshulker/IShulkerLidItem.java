package io.github.mrmindor.mrshulker;



import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IShulkerLidItem {
    Optional<ItemStack> getLidItem();
    static IShulkerLidItem from(Object object) {return (IShulkerLidItem) object;}
}
