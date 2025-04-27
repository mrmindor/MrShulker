package org.mrmindor.mrshulker.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.Nullable;
import org.mrmindor.mrshulker.component.ModComponents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class MixinAnvilScreenHandler extends ForgingScreenHandler {
    @Shadow @Nullable private String newItemName;

    @Shadow @Final private Property levelCost;

    @Shadow private int repairItemUsage;

    public MixinAnvilScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @Inject(
            method={"updateResult"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void updateResult(CallbackInfo ci){
        ItemStack input = this.input.getStack(0);
        ItemStack lidStack = this.input.getStack(1);
        if(this.isFirstItemAShulker()){
            ItemStack copy = input.copy();
            NbtComponent blockComponent = copy.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT );
            if(lidStack.isEmpty()){
                copy.set(DataComponentTypes.BLOCK_ENTITY_DATA, blockComponent.apply(
                        nbt-> {
                            nbt.putString("id", Identifier.of("shulker_box").toString());
                            nbt.remove(ModComponents.LidItem);
                        }

                ));
            }
            else{
                this.context.run( (world, blockPosition)->
                        copy.set(DataComponentTypes.BLOCK_ENTITY_DATA, blockComponent.apply(
                                nbt-> {
                                    nbt.putString("id", Identifier.of("shulker_box").toString());
                                    nbt.put(ModComponents.LidItem, lidStack.toNbt(world.getRegistryManager()));
                                }
                        ))

                );
            }
            if(this.newItemName != null && !StringHelper.isBlank(this.newItemName)){
                copy.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
            }
            else if(copy.contains(DataComponentTypes.CUSTOM_NAME)){
                copy.remove(DataComponentTypes.CUSTOM_NAME);
            }

            this.output.setStack(0,copy);
            this.levelCost.set(1);
            this.repairItemUsage = 1;
            this.sendContentUpdates();
            ci.cancel();

        }
    }

    @Inject(
            method = {"onTakeOutput"},
            at = {@At("HEAD")}
    )
    public void onTakeOutput(CallbackInfo ci) {
        if (this.isFirstItemAShulker()) {
            this.input.getStack(1).increment(1);
        }

    }

    @Unique
    private boolean isFirstItemAShulker(){
        ItemStack firstItemStack = this.input.getStack(0);
        Item firstItem = firstItemStack.getItem();
        if(firstItem instanceof BlockItem itemBlock){
            return itemBlock.getBlock() instanceof ShulkerBoxBlock;
        }
        return false;
    }
}
