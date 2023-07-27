package com.khjxiaogu.convivium.blocks.vending;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.CVTags;
import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

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
	@Override
	public float getDestroyProgress(BlockState pState, Player player, BlockGetter worldIn, BlockPos pos) {
		if (worldIn.getBlockEntity(pos) instanceof BeverageVendingBlockEntity blockEntity) {
			if(player.getUUID().equals(blockEntity.owner))
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
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		if (worldIn.getBlockEntity(pos) instanceof BeverageVendingBlockEntity blockEntity) {
			if (!worldIn.isClientSide) {
				if(player.getUUID().equals(blockEntity.owner)) {
					ItemStack held = player.getItemInHand(handIn);
					FluidStack out=ContainingRecipe.extractFluid(held);
					if (!out.isEmpty()) {
						if(blockEntity.tank.fill(out, FluidAction.SIMULATE)==out.getAmount()) {
							blockEntity.tank.fill(out, FluidAction.EXECUTE);
							ItemStack ret = held.getCraftingRemainingItem();
							held.shrink(1);
							ItemHandlerHelper.giveItemToPlayer(player, ret);
							return InteractionResult.sidedSuccess(worldIn.isClientSide);
						}
					}
					if (FluidUtil.interactWithFluidHandler(player, handIn, blockEntity.tank))
						return InteractionResult.SUCCESS;
					NetworkHooks.openScreen((ServerPlayer) player, blockEntity, blockEntity.getBlockPos());
					return InteractionResult.SUCCESS;
				}
				if(state.getValue(ACTIVE)) {
					if (FluidUtil.interactWithFluidHandler(player, handIn, blockEntity.handler))
						return InteractionResult.SUCCESS;
				}else {
					ItemStack held = player.getItemInHand(handIn);
					if(held.is(CVTags.Items.ASSES)&&held.getCount()>=blockEntity.amt) {
						ItemHandlerHelper.insertItem(blockEntity.storage,held.split(blockEntity.amt), false);
						if(held.isEmpty())
							player.setItemInHand(handIn, ItemStack.EMPTY);
						worldIn.setBlockAndUpdate(pos,state.setValue(ACTIVE, true));
					}
				}
				
			}
			return InteractionResult.SUCCESS;
		}
		return p;
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
}
