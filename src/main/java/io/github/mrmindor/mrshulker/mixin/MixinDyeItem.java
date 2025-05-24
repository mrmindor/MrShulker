package io.github.mrmindor.mrshulker.mixin;

import io.github.mrmindor.mrshulker.IShulkerLidItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinDyeItem  {


    @Shadow public abstract Item asItem();

    @Inject(method="useOn",
            at=@At("RETURN"),
            cancellable = true)
    public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        if(this.asItem() instanceof DyeItem dyeItem) {
            var level = context.getLevel();
            var pos = context.getClickedPos();
            var player = context.getPlayer();
            var state = level.getBlockState(pos);
            var targetBlock = level.getBlockState(pos).getBlock();
            if(targetBlock instanceof ShulkerBoxBlock
               && level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity targetShulker){
                if(targetShulker.getColor() == dyeItem.getDyeColor()){
                    cir.setReturnValue(InteractionResult.PASS);
                    return;
                }
                Block block = getDyedShulker(dyeItem.getDyeColor());
                level.setBlock(pos, block.defaultBlockState().setValue(ShulkerBoxBlock.FACING, state.getValue(ShulkerBoxBlock.FACING)), 2);
                if(level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity newShulker){
                    level.playLocalSound(player, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1, 0.95f);
                    ((AccessorShulkerBoxBlockEntity) newShulker).setInventory(((AccessorShulkerBoxBlockEntity) targetShulker).getInventory());

                    if (newShulker instanceof IShulkerLidItem lid) {
                        IShulkerLidItem.from(targetShulker).getLidItem().ifPresent(lid::setLidItem);
                    }
                    level.blockEntityChanged(pos);
                }
                context.getItemInHand().shrink(1);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }

        }




    }

    @Unique
    private static @NotNull Block getDyedShulker(DyeColor color) {
        Block block = Blocks.SHULKER_BOX;
        switch (color) {
            case WHITE -> block = Blocks.WHITE_SHULKER_BOX;
            case ORANGE -> block = Blocks.ORANGE_SHULKER_BOX;
            case MAGENTA -> block = Blocks.MAGENTA_SHULKER_BOX;
            case LIGHT_BLUE -> block = Blocks.LIGHT_BLUE_SHULKER_BOX;
            case YELLOW -> block = Blocks.YELLOW_SHULKER_BOX;
            case LIME -> block = Blocks.LIME_SHULKER_BOX;
            case PINK -> block = Blocks.PINK_SHULKER_BOX;
            case GRAY -> block = Blocks.GRAY_SHULKER_BOX;
            case LIGHT_GRAY -> block = Blocks.LIGHT_GRAY_SHULKER_BOX;
            case CYAN -> block = Blocks.CYAN_SHULKER_BOX;
            case PURPLE -> block = Blocks.PURPLE_SHULKER_BOX;
            case BLUE -> block = Blocks.BLUE_SHULKER_BOX;
            case BROWN -> block = Blocks.BROWN_SHULKER_BOX;
            case GREEN -> block = Blocks.GREEN_SHULKER_BOX;
            case RED -> block = Blocks.RED_SHULKER_BOX;
            case BLACK -> block = Blocks.BLACK_SHULKER_BOX;
        }
        return block;
    }
}
