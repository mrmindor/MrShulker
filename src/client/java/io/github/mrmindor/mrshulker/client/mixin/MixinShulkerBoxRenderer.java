package io.github.mrmindor.mrshulker.client.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mrmindor.mrshulker.MrShulker;
import io.github.mrmindor.mrshulker.client.MrShulkerClient;
import io.github.mrmindor.mrshulker.client.map.mapUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import io.github.mrmindor.mrshulker.IShulkerLidItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;


@Mixin({ShulkerBoxRenderer.class})
public abstract class MixinShulkerBoxRenderer {


    @Inject(
            method = "render(Lnet/minecraft/world/level/block/entity/ShulkerBoxBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/phys/Vec3;)V",
            at = {@At(
                    value="INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/ShulkerBoxRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/core/Direction;FLnet/minecraft/client/resources/model/Material;)V",
                    shift = At.Shift.AFTER
            )}
    )
    private void postRender(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float tickProgress, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Vec3 vec3, CallbackInfo ci){
        Minecraft minecraftClient = Minecraft.getInstance();
        var iShulker = IShulkerLidItem.from(shulkerBoxBlockEntity);
        Optional<ItemStack> lidItem= iShulker.getLidItem();

        if(lidItem.isPresent()){
            poseStack.pushPose();
            BlockState shulkerState = shulkerBoxBlockEntity.getBlockState();
            Direction shulkerFacing = shulkerState.getValue(ShulkerBoxBlock.FACING);
            var lidProgress = shulkerBoxBlockEntity.getProgress(tickProgress);
            poseStack.translate(0.5F, 0.0F, 0.5F);
            float f,g;
            if(shulkerFacing.getAxis().isHorizontal()){
                f= 0.0F;
                g=180.0F - shulkerFacing.toYRot();
            } else {
                f = (float) (-90 * shulkerFacing.getAxisDirection().getStep());
                g = 180F;
            }
            poseStack.translate((0.5F+lidProgress/2.0F)*shulkerFacing.getStepX(), 0.5F+((0.5F+lidProgress/2.0F)*shulkerFacing.getStepY()), (0.5F+lidProgress/2.0F)*(float)shulkerFacing.getStepZ());

            poseStack.mulPose(Axis.XP.rotationDegrees(f));
            poseStack.mulPose(Axis.YP.rotationDegrees(g));
            poseStack.mulPose(Axis.ZP.rotationDegrees(lidProgress*270.0F));

            var mapData = mapUtil.tryGetMapData(lidItem.get(), minecraftClient);
            if(mapData.isPresent()){
                poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
                poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
                poseStack.translate(-64.0F, -64.0F, 0.0F);
                poseStack.translate(0.0F, 0.0F, -1.0F);

                var renderer = minecraftClient.getMapRenderer();
                var mapRendererState = new MapRenderState();
                var mapId = lidItem.get().get(DataComponents.MAP_ID);

                renderer.extractRenderState(mapId, mapData.get(), mapRendererState);
                renderer.render(mapRendererState, poseStack, multiBufferSource, true, i);

            } else {
                var scale = MrShulkerClient.Config.getLidItemScale("block");
                if(MrShulker.Config.isPerShulkerScalingAllowed() && MrShulkerClient.Config.getShowCustomScales()){
                    var customScale = iShulker.getLidItemCustomScale();
                    scale = customScale.orElse(scale);
                }
                poseStack.scale(scale, scale, scale);
                minecraftClient.getItemRenderer().renderStatic(lidItem.get(), ItemDisplayContext.FIXED, i, j, poseStack, multiBufferSource, minecraftClient.level, 0);
            }
            poseStack.popPose();




        }
    }

}
