package net.sirplop.aetherworks.block.forge;

import net.minecraft.world.ItemInteractionResult;

import com.rekindled.embers.util.CapabilityCompat;

import net.minecraft.world.level.block.BaseEntityBlock;

import com.mojang.serialization.MapCodec;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.Misc;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.rekindled.embers.compat.legacy.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.api.block.HorizontalWaterloggableEntityBlock;
import net.sirplop.aetherworks.blockentity.AetheriumAnvilBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AetheriumAnvilBlock extends HorizontalWaterloggableEntityBlock {

    //1.20.5 made BaseEntityBlock require a codec so blocks can round-trip through data.
    public static final MapCodec<AetheriumAnvilBlock> CODEC = simpleCodec(AetheriumAnvilBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    public AetheriumAnvilBlock(Properties pProperties) {
        super(pProperties);
    }
    public static final VoxelShape EAST_WEST_AABB = Block.box(4, 0, 2, 12, 3, 14);
    public static final VoxelShape NORTH_SOUTH_AABB = Block.box(3, 0, 4, 14, 3, 12);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(HorizontalDirectionalBlock.FACING)) {
            case EAST, WEST -> EAST_WEST_AABB;
            default -> NORTH_SOUTH_AABB;
        };
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return AWRegistry.AETHERIUM_ANVIL_BLOCK_ENTITY.get().create(blockPos, blockState);
    }
    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        Direction dir = context.getHorizontalDirection().getOpposite();
        return state == null ? null : state.setValue(FACING, dir).setValue(BlockStateProperties.WATERLOGGED,
                context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    //Block.use split into useItemOn/useWithoutItem in 1.21; these all consult the held item.
    protected ItemInteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof AetheriumAnvilBlockEntity anvil) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (!heldItem.isEmpty()) {
                if (heldItem.getItem() == RegistryManager.TINKER_HAMMER.get()) {
                    anvil.onHit();
                    return ItemInteractionResult.SUCCESS;
                }
                ItemStack leftover = anvil.inventory.insertItem(0, heldItem, false);
                if (!leftover.equals(heldItem)) {
                    player.setItemInHand(hand, leftover);
                    return ItemInteractionResult.SUCCESS;
                }
            }
            if (!anvil.inventory.getStackInSlot(0).isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, player.position().x, player.position().y, player.position().z, anvil.inventory.getStackInSlot(0)));
                anvil.inventory.setStackInSlot(0, ItemStack.EMPTY);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                IItemHandler handler = CapabilityCompat.getCapability(blockEntity, ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
                if (handler != null) {
                    Misc.spawnInventoryInWorld(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, handler);
                    level.updateNeighbourForOutputSignal(pos, this);
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
