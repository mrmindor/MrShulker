package io.github.mrmindor.mrshulker.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mrmindor.mrshulker.component.ModComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ShulkerBoxSpecialRenderer.class)
public abstract class MixinShulkerBoxSpecialRenderer implements NoDataSpecialModelRenderer {

    @Unique @Nullable
    private ItemStack stack;


    @Nullable
    public Void extractArgument(ItemStack arg){
        this.stack = arg;
        return NoDataSpecialModelRenderer.super.extractArgument(arg);
    }


    @Inject(
            method="render",
            at=@At("RETURN")
    )
    public void render(ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType, CallbackInfo ci){

        Minecraft minecraftClient = Minecraft.getInstance();
        Optional<ItemStack> maybeItem= getLidItem(minecraftClient);
        if(maybeItem.isEmpty()){
            return;
        }
        var lidItem = maybeItem.get().copy();

        poseStack.pushPose();

        var mapData = tryGetMapData(lidItem, minecraftClient);
        if(mapData.isPresent()){
            poseStack.rotateAround(Direction.NORTH.getRotation(), 0.0F, 1.0F, 0.0F);
            var mapId = lidItem.get(DataComponents.MAP_ID);
            poseStack.translate(-.99F, 0.01F, 0.0F);
            poseStack.scale(0.0077F, 0.0077F, 0.077F);
            var renderer = minecraftClient.getMapRenderer();
            var mapRendererState = new MapRenderState();
            renderer.extractRenderState(mapId, mapData.get(), mapRendererState);
            renderer.render(mapRendererState, poseStack, bufferSource, true, packedLight);
        }
        else {
            if(lidItem.getItem() instanceof BlockItem && ((BlockItem)(lidItem.getItem())).getBlock() instanceof ShulkerBoxBlock){
                poseStack.translate(0.5F, 1.0F, 0.5F);
            }
            else {
                poseStack.rotateAround(Direction.NORTH.getRotation(), 0.0F, 1.0F, 0.0F);
                poseStack.translate(-0.5F, 0.5F, 0.0F);
            }
            minecraftClient.getItemRenderer().renderStatic(lidItem, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, minecraftClient.level, 0);
        }
        poseStack.popPose();
    }
    @Unique
    private Optional<MapItemSavedData> tryGetMapData(ItemStack lidItem, Minecraft minecraftClient){
        Optional<MapItemSavedData> result = Optional.empty();
        if(lidItem.has(DataComponents.MAP_ID)){
            var mapId = lidItem.get(DataComponents.MAP_ID);
            if(minecraftClient.level != null) {
                result = Optional.ofNullable(minecraftClient.level.getMapData(mapId));
            }
        }
        return result;

    }
    @Unique
    private Optional<ItemStack> getLidItem(Minecraft client) {
        Optional<ItemStack> lidItem = Optional.empty();
        if(client == null || client.level == null){
            return lidItem;
        }
        RegistryAccess registryAccess = client.level.registryAccess();
        if(this.stack != null ){
            var item = this.stack.getItem();
            if(item instanceof BlockItem){
                var block = ((BlockItem) item).getBlock();
                if(block instanceof ShulkerBoxBlock){
                    var blockComponent = this.stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
                    CompoundTag tag = blockComponent.copyTag();
                    var lidItemNbt = tag.getCompound(ModComponents.LID_ITEM);
                    if(lidItemNbt.isEmpty()){
                        lidItemNbt = tag.getCompound(ModComponents.COMPAT_DISPLAY);
                    }
                    if(lidItemNbt.isPresent()){

                        lidItem = ItemStack.parse(registryAccess, lidItemNbt.get());
                    }
                }
            }
        }
        return lidItem;
    }


}
