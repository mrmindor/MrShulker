package org.mrmindor.mrshulker.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.mrmindor.mrshulker.IShulkerLidItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;


@Mixin({ShulkerBoxBlockEntity.class})
public abstract class MixinShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory, IShulkerLidItem {
    protected MixinShulkerBoxBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    @Unique
    private ItemStack lidItem ;
    @Unique
    public Optional<ItemStack> getLidItem() {
        return Optional.ofNullable(lidItem);
    }

    public void setLidItem(ItemStack stack){
        lidItem = stack;
        this.markDirty();
    }

    @Inject( method ={"writeNbt"}, at= {@At("RETURN")})
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
        getLidItem().ifPresent(itemStack -> nbt.put("lidItem", itemStack.toNbt(registries)));
    }
    @Inject( method={"readNbt"}, at={@At("RETURN")})
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, CallbackInfo ci){
        Optional<NbtCompound> lidItemNbt = nbt.getCompound("lidItem");
        if(lidItemNbt.isPresent()){
            Optional<ItemStack> itemStack = ItemStack.fromNbt(registries, lidItemNbt.get());
            this.lidItem = itemStack.orElse(null);
        }
        else{
            this.lidItem = null;
        }
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries){
        NbtCompound nbt = super.toInitialChunkDataNbt(registries);
        if(this.lidItem != null) {
            nbt.put("lidItem", lidItem.toNbt(registries));
        }
        return nbt;
    }
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    public void markDirty(){
        super.markDirty();
        if(this.world != null){
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 0);
        }

    }

}
