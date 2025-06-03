package io.github.mrmindor.mrshulker;



import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IShulkerLidItem {
    Optional<ItemStack> getLidItem();
    void setLidItem(ItemStack stack);
    Optional<Float> getLidItemCustomScale();
    void setLidItemCustomScale(Float customScale);
    static IShulkerLidItem from(Object object) {return (IShulkerLidItem) object;}
}
