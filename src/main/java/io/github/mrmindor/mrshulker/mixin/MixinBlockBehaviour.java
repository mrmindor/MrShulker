package io.github.mrmindor.mrshulker.mixin;

import io.github.mrmindor.mrshulker.IShulkerLidItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class MixinBlockBehaviour {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (state.getBlock() instanceof ShulkerBoxBlock && level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulker && stack.getItem() instanceof DyeItem dye) {
            Block block = Blocks.SHULKER_BOX;
            switch (dye.getDyeColor()) {
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
            level.setBlock(pos, block.defaultBlockState().setValue(ShulkerBoxBlock.FACING, state.getValue(ShulkerBoxBlock.FACING)), 2);
            if (level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity newShulker) {
                level.playLocalSound(player, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1, 0.95f);
                ((AccessorShulkerBoxBlockEntity) newShulker).setInventory(((AccessorShulkerBoxBlockEntity) shulker).getInventory());
                if (newShulker instanceof IShulkerLidItem lid) {
                    IShulkerLidItem.from(shulker).getLidItem().ifPresent(lid::setLidItem);
                }
                level.blockEntityChanged(pos);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

}
