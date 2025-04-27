package io.github.mrmindor.mrshulker.mixin;


import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.Nullable;
import io.github.mrmindor.mrshulker.component.ModComponents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class MixinAnvilMenu extends ItemCombinerMenu {
    @Shadow @Nullable private String itemName;

    @Shadow @Final private DataSlot cost;

    @Shadow private int repairItemCountCost;

    public MixinAnvilMenu(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context, ItemCombinerMenuSlotDefinition forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Inject(
            method={"createResult"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void createResult(CallbackInfo ci){
        ItemStack input = this.inputSlots.getItem(0);
        ItemStack lidStack = this.inputSlots.getItem(1);
        if(this.isFirstItemAShulker()){
            ItemStack copy = input.copy();
            var blockComponent = copy.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY );
            if(lidStack.isEmpty()){
                copy.set(DataComponents.BLOCK_ENTITY_DATA, blockComponent.update(
                        nbt-> {
                            nbt.putString("id", "minecraft:shulker_box");
                            nbt.remove(ModComponents.LID_ITEM);
                        }

                ));
            }
            else{
                this.access.execute( (world, blockPosition)->
                        copy.set(DataComponents.BLOCK_ENTITY_DATA, blockComponent.update(
                                nbt-> {
                                    nbt.putString("id", "minecraft:shulker_box");
                                    nbt.put(ModComponents.LID_ITEM, lidStack.save(world.registryAccess()));
                                }
                        ))

                );
            }
            if(this.itemName != null && !StringUtil.isBlank(this.itemName)){
                copy.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            }
            else if(copy.has(DataComponents.CUSTOM_NAME)){
                copy.remove(DataComponents.CUSTOM_NAME);
            }

            this.resultSlots.setItem(0,copy);
            this.cost.set(1);
            this.repairItemCountCost = 1;
            this.broadcastChanges();
            ci.cancel();

        }
    }

    @Inject(
            method = {"onTake"},
            at = {@At("HEAD")}
    )
    public void onTaket(CallbackInfo ci) {
        if (this.isFirstItemAShulker()) {
            this.inputSlots.getItem(1).grow(1);
        }

    }

    @Unique
    private boolean isFirstItemAShulker(){
        ItemStack firstItemStack = this.inputSlots.getItem(0);
        Item firstItem = firstItemStack.getItem();
        if(firstItem instanceof BlockItem itemBlock){
            return itemBlock.getBlock() instanceof ShulkerBoxBlock;
        }
        return false;
    }
}
