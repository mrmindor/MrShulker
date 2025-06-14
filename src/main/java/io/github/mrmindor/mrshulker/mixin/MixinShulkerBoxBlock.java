package io.github.mrmindor.mrshulker.mixin;


import com.mojang.serialization.Codec;
import io.github.mrmindor.mrshulker.IShulkerLidItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import io.github.mrmindor.mrshulker.component.ModComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({ShulkerBoxBlock.class})
public abstract class MixinShulkerBoxBlock
{
    @Inject( method = {"getDrops"}, at={@At("RETURN")})
    public void getDroppedStacks(BlockState blockState, LootParams.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir){
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof ShulkerBoxBlockEntity shulker) {
            if (blockEntity.getLevel() != null) {
                for(ItemStack stack : cir.getReturnValue()) {
                    var iLidItem = IShulkerLidItem.from(shulker);
                    var customScale = iLidItem.getLidItemCustomScale();
                    var maybeLidItem = iLidItem.getLidItem();
                    maybeLidItem.ifPresent(lidStack -> stack.set(DataComponents.BLOCK_ENTITY_DATA, stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).update(
                            nbt -> {
                                ProblemReporter.Collector problemReporter = new ProblemReporter.Collector();
                                var output = TagValueOutput.createWithContext(problemReporter, blockEntity.getLevel().registryAccess());
                                output.putString("id", "minecraft:shulker_box");
                                output.store(ModComponents.LID_ITEM, ItemStack.CODEC, lidStack);
                                customScale.ifPresent(scale -> output.store(ModComponents.LID_ITEM_CUSTOM_SCALE, Codec.FLOAT, scale));
                                nbt.merge(output.buildResult());
                            }
                    )));
                }
            }
        }
    }
}
