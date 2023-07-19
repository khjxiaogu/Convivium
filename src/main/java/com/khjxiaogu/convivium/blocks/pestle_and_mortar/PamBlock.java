package com.khjxiaogu.convivium.blocks.pestle_and_mortar;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

public class PamBlock extends KineticBasedBlock<PamBlockEntity> {

	public PamBlock(Properties p_54120_) {
		super(CVBlockEntityTypes.PAM, p_54120_);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		BlockEntity be=worldIn.getBlockEntity(pos);
		if (be instanceof PamBlockEntity pam) {
			ItemStack held = player.getItemInHand(handIn);
			FluidStack out=BowlContainingRecipe.extractFluid(held);
			if (!out.isEmpty()) {
				if(pam.tankin.fill(out, FluidAction.SIMULATE)==out.getAmount()) {
					pam.tankin.fill(out, FluidAction.EXECUTE);
					ItemStack ret = held.getCraftingRemainingItem();
					held.shrink(1);
					ItemHandlerHelper.giveItemToPlayer(player, ret);
					return InteractionResult.sidedSuccess(worldIn.isClientSide);
				}
			}
			if (FluidUtil.interactWithFluidHandler(player, handIn, pam.tanks))
				return InteractionResult.SUCCESS;
			if (handIn == InteractionHand.MAIN_HAND) {
				if(!worldIn.isClientSide)
					NetworkHooks.openScreen((ServerPlayer) player, pam, pam.getBlockPos());
				return InteractionResult.SUCCESS;
			}
		}
		
		return p;
	}

}
