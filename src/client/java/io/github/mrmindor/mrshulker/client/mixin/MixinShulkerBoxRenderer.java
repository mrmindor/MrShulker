package io.github.mrmindor.mrshulker.client.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import io.github.mrmindor.mrshulker.IShulkerLidItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;


@Mixin({ShulkerBoxRenderer.class})
public abstract class MixinShulkerBoxRenderer {
//    @Inject(
//            method = {"render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Vec3d;)V"},
//            at = {@At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/render/block/entity/ShulkerBoxBlockEntityRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Direction;FLnet/minecraft/client/util/SpriteIdentifier;)V",
//                    shift = At.Shift.BEFORE
//            )}
//    )
//    private void preRender(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d, CallbackInfo ci){
//
//    }

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
        Optional<ItemStack> lidItem= IShulkerLidItem.from(shulkerBoxBlockEntity).getLidItem();
        if(lidItem.isPresent()){
            BlockState shulkerState = shulkerBoxBlockEntity.getBlockState();
            Direction shulkerFacing = shulkerState.getValue(ShulkerBoxBlock.FACING);
            Direction lidFrameFacing = shulkerFacing;
            float lidPosition = shulkerBoxBlockEntity.getProgress(tickProgress)/2.0F;
            Vector3fc target;
            switch(shulkerFacing){
                case UP -> {
                    lidFrameFacing = Direction.DOWN;
                    target = new Vector3f(0.5F, 1.0F+lidPosition, 0.5F);
                }
                case DOWN -> {
                    lidFrameFacing = Direction.UP;
                    target = new Vector3f(0.5F, -lidPosition, 0.5F);
                }
                case EAST -> {
                    lidFrameFacing = Direction.WEST;
                    target = new Vector3f(1.0F+lidPosition, 0.5F, 0.5F);
                }
                case WEST -> {
                    lidFrameFacing = Direction.EAST;
                    target = new Vector3f(-lidPosition, 0.5F, 0.5F);
                }
                case SOUTH -> {
                    lidFrameFacing = Direction.NORTH;
                    target = new Vector3f(0.5F, 0.5F, 1.0F +lidPosition);
                }
                case NORTH -> {
                    lidFrameFacing = Direction.SOUTH;
                    target = new Vector3f(0.5F, 0.5F, -lidPosition);
                }
                default -> target = new Vector3f(0.5F, lidPosition, 0.5F);

            }
            ItemFrame lidFrame = new ItemFrame(minecraftClient.level, shulkerBoxBlockEntity.getBlockPos(), lidFrameFacing);
            lidFrame.setItem(lidItem.get(), false);
            lidFrame.setInvisible(true);
            Vector3fc up = new Vector3f(0.0F, 1.0F, 0.0F);

            poseStack.pushPose();
            poseStack.translate(target.x(),target.y(),target.z());

            minecraftClient.getEntityRenderDispatcher().render(lidFrame, 0.0F, 0.0F, 0.0F, tickProgress, poseStack, multiBufferSource,i );
            poseStack.popPose();

        }
    }

}
