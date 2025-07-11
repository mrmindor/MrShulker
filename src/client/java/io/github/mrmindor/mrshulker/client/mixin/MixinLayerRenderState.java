package io.github.mrmindor.mrshulker.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mrmindor.mrshulker.client.IShulkerRendererLidItem;
import net.fabricmc.fabric.api.renderer.v1.render.FabricLayerRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public class MixinLayerRenderState implements FabricLayerRenderState {
    @Shadow @Nullable private Object argumentForSpecialRendering;

    @Shadow @Nullable private SpecialModelRenderer<Object> specialRenderer;

    @Inject(method= "setupSpecialModel",
    at=@At("RETURN"))
    public <T> void setupShulkerBoxSpecialRenderer(SpecialModelRenderer<T> specialModelRenderer, T object, CallbackInfo ci) {
        if(specialModelRenderer instanceof IShulkerRendererLidItem iShulkerRendererLidItem){
            this.argumentForSpecialRendering =  iShulkerRendererLidItem.getStack();
        }
    }

    @Inject(method="render", at=@At("HEAD"))
    void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci){
        if(this.specialRenderer != null && (this.specialRenderer instanceof IShulkerRendererLidItem iShulkerRendererLidItem)){
            iShulkerRendererLidItem.setStack((ItemStack) this.argumentForSpecialRendering);
            this.argumentForSpecialRendering = null;
        }
    }

}
