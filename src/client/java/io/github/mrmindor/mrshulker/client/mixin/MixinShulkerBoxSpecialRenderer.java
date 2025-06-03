package io.github.mrmindor.mrshulker.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mrmindor.mrshulker.MrShulker;
import io.github.mrmindor.mrshulker.client.MrShulkerClient;
import io.github.mrmindor.mrshulker.client.map.mapUtil;
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

        var mapData = mapUtil.tryGetMapData(lidItem, minecraftClient);
        if(mapData.isPresent()){
            poseStack.rotateAround(Direction.NORTH.getRotation(), 0.0F, 1.0F, 0.0F);
            var mapId = lidItem.get(DataComponents.MAP_ID);
            poseStack.translate(-1.0F, 0.00F, 0.0F);
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);

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

                var scale = MrShulkerClient.Config.getLidItemScale(displayContext.getSerializedName());
                if(MrShulker.Config.isPerShulkerScalingAllowed() && MrShulkerClient.Config.getShowCustomScales()){
                    var customScale = getLidItemCustomScale(minecraftClient);
                    scale = customScale.orElse(scale);
                }
                poseStack.scale(scale, scale, scale);
            }
            minecraftClient.getItemRenderer().renderStatic(lidItem, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, minecraftClient.level, 0);
        }
        poseStack.popPose();
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
    @Unique
    private Optional<Float> getLidItemCustomScale(Minecraft client) {
        if(client == null || client.level == null){
            return Optional.empty();
        }
        if(this.stack != null ){
            var item = this.stack.getItem();
            if(item instanceof BlockItem){
                var block = ((BlockItem) item).getBlock();
                if(block instanceof ShulkerBoxBlock){
                    var blockComponent = this.stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
                    CompoundTag tag = blockComponent.copyTag();
                    return tag.getFloat(ModComponents.LID_ITEM_CUSTOM_SCALE);
                }
            }
        }
        return Optional.empty();
    }


}
