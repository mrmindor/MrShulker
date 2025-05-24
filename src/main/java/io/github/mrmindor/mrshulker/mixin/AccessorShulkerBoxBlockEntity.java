package io.github.mrmindor.mrshulker.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShulkerBoxBlockEntity.class)
public interface AccessorShulkerBoxBlockEntity {

    @Invoker("getItems")
    NonNullList<ItemStack> getInventory();

    @Invoker("setItems")
    void setInventory(NonNullList<ItemStack> items);

}
