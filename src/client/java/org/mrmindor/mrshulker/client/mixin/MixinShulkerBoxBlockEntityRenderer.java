package org.mrmindor.mrshulker.client.mixin;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.mrmindor.mrshulker.IShulkerLidItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

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
            ItemFrameEntity lidFrame = new ItemFrameEntity(minecraftClient.world, shulkerBoxBlockEntity.getPos(), Direction.DOWN);
            lidFrame.setHeldItemStack(lidItem.get(), false);
            lidFrame.setInvisible(true);
            matrixStack.push();
            float lidPosition = shulkerBoxBlockEntity.getAnimationProgress(tickProgress);
            minecraftClient.getEntityRenderDispatcher().render(lidFrame, 0.0F, 0.0F, 0.0F, tickProgress, matrixStack, vertexConsumerProvider,light );
        }
    }

}
