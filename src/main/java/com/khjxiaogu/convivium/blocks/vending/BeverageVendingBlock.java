package com.khjxiaogu.convivium.blocks.vending;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVTags;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class BeverageVendingBlock extends CPHorizontalEntityBlock<BeverageVendingBlockEntity> {
	public static final BooleanProperty ACTIVE=BooleanProperty.create("active");
	public BeverageVendingBlock(Properties p_54120_) {
		super(CVBlockEntityTypes.BEVERAGE_VENDING_MACHINE, p_54120_);
		this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, true));
	}
	static final VoxelShape shape = Block.box(1, 0, 1, 15, 15, 15);
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		// TODO Auto-generated method stub
		super.createBlockStateDefinition(builder);
		builder.add(ACTIVE);
	}
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}
	@SuppressWarnings("deprecation")
	@Override
	public float getDestroyProgress(BlockState pState, Player player, BlockGetter worldIn, BlockPos pos) {
		if (worldIn.getBlockEntity(pos) instanceof BeverageVendingBlockEntity blockEntity) {
			if(player.getAbilities().instabuild||player.getUUID().equals(blockEntity.owner))
				return super.getDestroyProgress(pState, player, worldIn, pos);
			return 0;
		}
		return super.getDestroyProgress(pState, player, worldIn, pos);
	}
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		if (pLevel.getBlockEntity(pPos) instanceof BeverageVendingBlockEntity dish) {
			dish.owner=pPlacer.getUUID();
		}
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!(newState.getBlock() instanceof BeverageVendingBlock)) {
			if (worldIn.getBlockEntity(pos) instanceof BeverageVendingBlockEntity dish) {
				for(int i=0;i<dish.storage.getSlots();i++) {
					super.popResource(worldIn, pos, dish.storage.getStackInSlot(i));
				}
			}
			worldIn.removeBlockEntity(pos);
		}
	}


	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());

	}
	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		if (pLevel.getBlockEntity(pPos) instanceof BeverageVendingBlockEntity dish) {
			int sign=dish.tank.getFluidAmount()/250;
			return Math.min(sign,15);
		}
		
		return 0;
	}
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		InteractionResult p = super.useWithoutItem(state, level, pos, player, hitResult);
		if (p.consumesAction())
			return p;
		if (level.getBlockEntity(pos) instanceof BeverageVendingBlockEntity blockEntity) {
			if(player.getUUID().equals(blockEntity.owner)) {
					if (!level.isClientSide)	
						player.openMenu(blockEntity, blockEntity.getBlockPos());
					return InteractionResult.sidedSuccess(level.isClientSide);
			}			
			return InteractionResult.FAIL;
		}
		return p;
	}
	@Override
	protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		ItemInteractionResult p = super.useItemOn(held, state, level, pos, player, hand, hitResult);
		if (p.consumesAction())
			return p;
		if (level.getBlockEntity(pos) instanceof BeverageVendingBlockEntity blockEntity) {
			if(player.getUUID().equals(blockEntity.owner)) {
				FluidStack out=Utils.extractFluid(held);
				if (!out.isEmpty()) {
					if(blockEntity.tank.fill(out, FluidAction.SIMULATE)==out.getAmount()) {
						blockEntity.tank.fill(out, FluidAction.EXECUTE);
						ItemStack ret = held.getCraftingRemainingItem();
						held.shrink(1);
						ItemHandlerHelper.giveItemToPlayer(player, ret);
						return ItemInteractionResult.sidedSuccess(level.isClientSide);
					}
				}
				if (FluidUtil.interactWithFluidHandler(player, hand, blockEntity.tank))
					return ItemInteractionResult.SUCCESS;
			}			
			if(state.getValue(ACTIVE)) {
				if (FluidUtil.interactWithFluidHandler(player, hand, blockEntity.handler))
					return ItemInteractionResult.SUCCESS;
			}else {
				if(held.is(CVTags.Items.ASSES)&&held.getCount()>=blockEntity.amt&&blockEntity.tank.getFluidAmount()>=250) {
					if(blockEntity.isInfinite||ItemHandlerHelper.insertItem(blockEntity.storage,held.copyWithCount(blockEntity.amt), true).isEmpty()) {
						if(!level.isClientSide) {
							ItemStack it=held.split(blockEntity.amt);
							if(!blockEntity.isInfinite)
								ItemHandlerHelper.insertItem(blockEntity.storage,it, false);
							if(held.isEmpty())
								player.setItemInHand(hand, ItemStack.EMPTY);
							level.setBlockAndUpdate(pos,state.setValue(ACTIVE, true));
						}
					}
				}
			}
		}
		return p;
	}
}
