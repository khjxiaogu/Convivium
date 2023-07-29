/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.item;

import java.util.List;
import java.util.Optional;

import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.foods.BeverageBlock;
import com.khjxiaogu.convivium.blocks.foods.BeverageBlockEntity;
import com.khjxiaogu.convivium.data.recipes.ContainingRecipe;
import com.khjxiaogu.convivium.fluid.BeverageFluid;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.StewInfo;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.registries.ForgeRegistries;

public class JugItem extends ItemFluidContainer  implements ICreativeModeTabItem{
    public JugItem(Properties props) {
        super(props, 1250);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }


	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(e->{
			FluidStack f=e.getFluidInTank(0);
			if(!f.isEmpty()) {
				tooltip.add(f.getDisplayName());
				Optional<BeverageInfo> oinfo = BeverageFluid.getInfo(f);
				oinfo.ifPresent(info->{
				Utils.addPotionTooltip(info.effects, tooltip, 1);
				info.appendTooltip(tooltip);
				});
				tooltip.add(Utils.string(f.getAmount()+"/1250 mB"));
				
			}
		});
		
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand pUsedHand) {
		BlockHitResult ray = Item.getPlayerPOVHitResult(worldIn, playerIn, Fluid.SOURCE_ONLY);
		ItemStack cur=playerIn.getItemInHand(pUsedHand);
		if (ray.getType() == Type.BLOCK) {
			BlockPos blockpos = ray.getBlockPos();
			FluidState state = worldIn.getFluidState(blockpos);
			BlockState blk=worldIn.getBlockState(blockpos);
			
			if(blk.getBlock() instanceof BucketPickup bucket) {
				IFluidHandlerItem handler=cur.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
				if(handler!=null) {
					FluidStack fluid=handler.getFluidInTank(0);
					if(!fluid.isEmpty()&&fluid.getAmount()<handler.getTankCapacity(0)&&fluid.getFluid().isSame(state.getType())) {
						int amt=handler.fill(new FluidStack(state.getType(),FluidType.BUCKET_VOLUME),FluidAction.EXECUTE);
						if(amt>0) {
							bucket.pickupBlock(worldIn, blockpos, blk);
							return InteractionResultHolder.sidedSuccess(cur,worldIn.isClientSide);
						}
					}
				}
			}
			if(worldIn.getBlockEntity(blockpos) instanceof BeverageBlockEntity be&&be.internal.is(Items.GLASS_BOTTLE)) {
				IFluidHandlerItem handler=cur.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
				if(handler!=null&handler.getFluidInTank(0).getAmount()>=250) {
					ContainingRecipe cr=ContainingRecipe.recipes.get(handler.getFluidInTank(0).getFluid());
					if(cr!=null) {
						if(!worldIn.isClientSide) {
							be.internal=cr.handle(handler.drain(250, FluidAction.EXECUTE));
							be.syncData();
						}
						return InteractionResultHolder.sidedSuccess(cur,worldIn.isClientSide);
					}
				}
			}
			
			FluidActionResult res=FluidUtil.tryPickUpFluid(cur, playerIn, worldIn, blockpos,ray.getDirection());
			if(res.isSuccess()) {
				
				return InteractionResultHolder.sidedSuccess(res.getResult(),worldIn.isClientSide);
			}
		}else if(ray.getType() == Type.MISS) {
			if(playerIn.isShiftKeyDown()) {
				IFluidHandlerItem handler=cur.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
				if(handler!=null) {
					FluidStack fluid=handler.getFluidInTank(0);
					if(!fluid.isEmpty()) {
						if(handler.drain(1250, FluidAction.EXECUTE).getAmount()>0) {
							return InteractionResultHolder.sidedSuccess(cur,worldIn.isClientSide);
						}
					}
				}
			}
		}
		return InteractionResultHolder.pass(cur);
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(helper.isType(CVMain.MAIN_TAB))
			helper.accept(this);
	}

}
