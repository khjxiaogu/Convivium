/*
 * Copyright (c) 2024 IEEM Trivium Society/khjxiaogu
 *
 * This file is part of Convivium.
 *
 * Convivium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 * the Free Software Foundation, version 3.
 *
 * Convivium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 * along with Convivium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.khjxiaogu.convivium.item;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.khjxiaogu.convivium.CVComponents;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.foods.BeverageBlockEntity;
import com.khjxiaogu.convivium.util.BeverageInfo;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class JugItem extends Item  implements ICreativeModeTabItem{
    public JugItem(Properties props) {
        super(props);
    }


	@Override
	public InteractionResult use(Level worldIn, Player playerIn, InteractionHand pUsedHand) {
		BlockHitResult ray = Item.getPlayerPOVHitResult(worldIn, playerIn, Fluid.SOURCE_ONLY);
		ItemStack cur=playerIn.getItemInHand(pUsedHand);
		ItemAccess ia=ItemAccess.forPlayerInteraction(playerIn, pUsedHand);
		if (ray.getType() == Type.BLOCK) {
			BlockPos blockpos = ray.getBlockPos();
			FluidState state = worldIn.getFluidState(blockpos);
			BlockState blk=worldIn.getBlockState(blockpos);
			ResourceHandler<FluidResource> handler=cur.getCapability(Capabilities.Fluid.ITEM,ia);
			if(blk.getBlock() instanceof BucketPickup bucket) {
				try(Transaction trans=Transaction.openRoot()){
					if(handler!=null) {
						int amt=handler.insert(FluidResource.of(state.getType()),FluidType.BUCKET_VOLUME,trans);
						if(amt>0) {
							bucket.pickupBlock(playerIn,worldIn, blockpos, blk);
							trans.commit();
							return InteractionResult.SUCCESS;
						}
						
					}
				}
			}
			if(worldIn.getBlockEntity(blockpos) instanceof BeverageBlockEntity be) {
				ItemResource ir=be.getInternal().getResource(0);
				if(ir.is(Items.GLASS_BOTTLE)&&handler!=null) {
					FluidResource rs=handler.getResource(0);
					if(!rs.isEmpty()) {
						if(!worldIn.isClientSide()) {
							try(Transaction ctx=Transaction.openRoot()){
								int amt = handler.extract(rs,250, ctx);
								ContanerContainFoodEvent ev=Utils.contain(ir,rs,amt);
								if (ev.isAllowed()) {
									if(be.exchangeInternal(ev.getOutput(),ctx).is(Items.GLASS_BOTTLE)) {
										ctx.commit();
										be.syncData();
									}
								}
							}
						}

						return InteractionResult.SUCCESS_SERVER;
					}
				}
			}
			try(Transaction trans=Transaction.openRoot()){
				if(handler!=null) {
					FluidStack res=FluidUtil.tryPickupFluid(handler, playerIn, worldIn, blockpos,ray.getDirection());
					if(!res.isEmpty()) {
						trans.commit();
						return InteractionResult.SUCCESS;
					}
					
				}
			}
		}else if(ray.getType() == Type.MISS) {
			if(playerIn.isShiftKeyDown()) {
				ResourceHandler<FluidResource> handler=cur.getCapability(Capabilities.Fluid.ITEM,ia);
				if(handler!=null) {
					try(Transaction trans=Transaction.openRoot()){
						if(handler.extract(handler.getResource(0), handler.getAmountAsInt(0), trans)>0)
							trans.commit();
					}
				}
			}
		}
		return super.use(worldIn, playerIn, pUsedHand);
	}

	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(helper.isType(CVMain.MAIN_TAB))
			helper.accept(this,1);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return false;
	}


	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
		@Nullable @org.jspecify.annotations.Nullable ResourceHandler<FluidResource> e=itemStack.getCapability(Capabilities.Fluid.ITEM,ItemAccess.forStack(itemStack));
		if(e!=null){
			FluidResource f=e.getResource(0);
			if(!f.isEmpty()) {
				builder.accept(f.getHoverName());
				BeverageInfo info = f.get(CVComponents.BEVERAGE_INFO);
				if(info!=null){
					info.addToTooltip(context, builder, tooltipFlag, f);
				}
				builder.accept(Utils.string(e.getAmountAsInt(0)+"/1250 mB"));
				
			}
		}
	}

}
