package org.mrmindor.mrshulker.client.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.mrmindor.mrshulker.IShulkerLidItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static net.minecraft.block.ShulkerBoxBlock.FACING;

@Mixin({ShulkerBoxBlockEntityRenderer.class})
public abstract class MixinShulkerBoxBlockEntityRenderer {
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
            method = {"render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Vec3d;)V"},
            at = {@At(
                    value="INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/ShulkerBoxBlockEntityRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Direction;FLnet/minecraft/client/util/SpriteIdentifier;)V",
                    shift = At.Shift.AFTER
            )}
    )
    private void postRender(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float tickProgress, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int j, Vec3d vec3d, CallbackInfo ci){
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Optional<ItemStack> lidItem= IShulkerLidItem.from(shulkerBoxBlockEntity).getLidItem();
        if(lidItem.isPresent()){
            BlockState shulkerState = shulkerBoxBlockEntity.getCachedState();
            Direction shulkerFacing = shulkerState.get(FACING);
            Direction lidFrameFacing = shulkerFacing;
            float lidPosition = shulkerBoxBlockEntity.getAnimationProgress(tickProgress)/2.0F;
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
            ItemFrameEntity lidFrame = new ItemFrameEntity(minecraftClient.world, shulkerBoxBlockEntity.getPos(), lidFrameFacing);
            lidFrame.setHeldItemStack(lidItem.get(), false);
            lidFrame.setInvisible(true);
            Vector3fc up = new Vector3f(0.0F, 1.0F, 0.0F);


            switch (lidFrameFacing){
            }
            Quaternionf rotation = (new Quaternionf()).rotateTo(up, target);

            //Vector3fc translation = new Vector3f(0.5F, lidPosition, 0.5F);
            //translation.
            matrixStack.push();
            matrixStack.translate(target.x(),target.y(),target.z());
            //matrixStack.multiply(rotation);


            minecraftClient.getEntityRenderDispatcher().render(lidFrame, 0.0F, 0.0F, 0.0F, tickProgress, matrixStack, vertexConsumerProvider,light );
            matrixStack.pop();

        }
    }

}
