package io.github.mrmindor.mrshulker.client.mixin;

import io.github.mrmindor.mrshulker.client.IShulkerRendererLidItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelWrapper.class)
public abstract class MixinSpecialModelWrapper<T> implements ItemModel {
    @Final
    @Shadow
    private SpecialModelRenderer<T> specialRenderer;

    @Inject(method = "update", at=@At("TAIL"))
    public void update(ItemStackRenderState itemStackRenderState, ItemStack itemStack, ItemModelResolver itemModelResolver, ItemDisplayContext itemDisplayContext, ClientLevel clientLevel, LivingEntity livingEntity, int i, CallbackInfo ci){
        if(this.specialRenderer instanceof IShulkerRendererLidItem shulkerRenderer){
            if(itemDisplayContext == ItemDisplayContext.GUI) {
                var data = shulkerRenderer.getCustomData();
                if(data != null){
                    itemStackRenderState.appendModelIdentityElement(data);
                }
            }
        }
    }

}
