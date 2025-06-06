package io.github.mrmindor.mrshulker.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.github.mrmindor.mrshulker.IShulkerLidItem;
import io.github.mrmindor.mrshulker.component.ModComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;


@Mixin({ShulkerBoxBlockEntity.class})
public abstract class MixinShulkerBoxBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, IShulkerLidItem {
    protected MixinShulkerBoxBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    @Unique
    private ItemStack lidItem ;
    @Unique
    public Optional<ItemStack> getLidItem() {
        return Optional.ofNullable(lidItem);
    }
    @Unique
    public void setLidItem(ItemStack stack) {
        lidItem = stack;
    }
    @Unique
    private float lidItemCustomScale;
    @Unique
    public Optional<Float> getLidItemCustomScale() {return lidItemCustomScale > 0.0F? Optional.of(lidItemCustomScale):Optional.empty();}

    @Unique
    public void setLidItemCustomScale(Float customScale){
        lidItemCustomScale = customScale;
    }

    @Inject( method ={"saveAdditional"}, at= {@At("RETURN")})
    public void mixinSaveAdditional(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        getLidItem().ifPresent(itemStack -> tag.put(ModComponents.LID_ITEM, itemStack.save(registries)));
        getLidItemCustomScale().ifPresent(scale -> tag.putFloat(ModComponents.LID_ITEM_CUSTOM_SCALE, scale));
    }
    @Inject( method={"loadAdditional"}, at={@At("RETURN")})
    public void mixinLoadAdditional(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci){
        Optional<CompoundTag> lidItemNbt = tag.getCompound(ModComponents.LID_ITEM);
        if(lidItemNbt.isEmpty())
            lidItemNbt = tag.getCompound(ModComponents.COMPAT_DISPLAY);
        if(lidItemNbt.isPresent()){
            Optional<ItemStack> itemStack = ItemStack.parse(registries, lidItemNbt.get());
            this.lidItem = itemStack.orElse(null);
        }
        else{
            this.lidItem = null;
        }
        var customScale = tag.getFloat(ModComponents.LID_ITEM_CUSTOM_SCALE);
        customScale.ifPresent(this::setLidItemCustomScale);
    }

    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries){
        CompoundTag nbt = super.getUpdateTag(registries);
        if(this.lidItem != null) {
            nbt.put(ModComponents.LID_ITEM, lidItem.save(registries));
        }
        getLidItemCustomScale().ifPresent(scale ->nbt.putFloat(ModComponents.LID_ITEM_CUSTOM_SCALE, scale));
        return nbt;
    }
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    public void setChanged(){
        super.setChanged();
        if(this.level != null){
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 0);
        }

    }

}
