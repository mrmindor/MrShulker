package org.mrmindor.mrshulker.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({ShulkerBoxBlock.class})
public abstract class MixinShulkerBoxBlock
{
    @Inject( method = {"getDroppedStacks"}, at={@At("RETURN")})
    public void getDroppedStacks(BlockState state, LootWorldContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir){
        BlockEntity blockEntity = (BlockEntity)builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof ShulkerBoxBlockEntity shulker) {
            if (blockEntity.getWorld() != null) {
                for(ItemStack stack : cir.getReturnValue()) {
                    NbtElement lidItem = shulker.createNbt(blockEntity.getWorld().getRegistryManager()).get("lidItem");
                    if (lidItem != null) {
                        stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, (stack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT))
                                .apply(nbt -> nbt.put("lidItem", lidItem)));
                    }
                }
            }
        }
    }
}
