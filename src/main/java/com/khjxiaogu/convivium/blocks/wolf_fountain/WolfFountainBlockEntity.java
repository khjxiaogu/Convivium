package com.khjxiaogu.convivium.blocks.wolf_fountain;

import java.util.Optional;

import org.joml.Vector2i;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.kinetics.Cog;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.client.CVParticles;
import com.teammoeg.caupona.blocks.foods.IFoodContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;

public class WolfFountainBlockEntity extends KineticTransferBlockEntity implements Cog{
	FluidTank fluid=new FluidTank(1000) {

		@Override
		protected void onContentsChanged() {
			syncData();
			super.onContentsChanged();
		}

	};
	ItemStack item;
	public static final Vector2i[] spd1pos=new Vector2i[] {
		new Vector2i(1,0),
		new Vector2i(1,-1),
		new Vector2i(2,-1),
		new Vector2i(1,-2),
		new Vector2i(2,-2),
		new Vector2i(2,-3),
		new Vector2i(2,-4),
		new Vector2i(2,-5),
		new Vector2i(2,-6),
		new Vector2i(2,-7),
		
	};
	public static final Vector2i[] spd2pos=new Vector2i[] {
		new Vector2i(1,0),
		new Vector2i(2,0),
		new Vector2i(2,-1),
		new Vector2i(2,-2),
		new Vector2i(3,-2),
		new Vector2i(3,-3),
		new Vector2i(3,-4),
		new Vector2i(3,-5),
		new Vector2i(4,-5),
		new Vector2i(4,-6),
		new Vector2i(4,-7),
	};
	public WolfFountainBlockEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CVBlockEntityTypes.WOLF_FOUNTAIN.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
		
	}



	@Override
	public void readCustomNBT(CompoundTag tag, boolean arg1, Provider arg2) {
		super.readCustomNBT(tag, arg1, arg2);
		fluid.readFromNBT(arg2, tag.getCompound("fluid"));
		if(tag.contains("item"))
			item=ItemStack.parse(arg2, tag.getCompound("item")).orElse(null);
	}

	@Override
	public void writeCustomNBT(CompoundTag tag, boolean arg1, Provider arg2) {
		super.writeCustomNBT(tag, arg1, arg2);
		tag.put("fluid", fluid.writeToNBT(arg2, new CompoundTag()));
		if(item!=null)
			tag.put("item", item.save(arg2));
	}

	@Override
	public void tick() {
		super.tick();
		if(this.level.isClientSide) {
			if(!fluid.isEmpty()) {
			//if(this.level.getGameTime()%20==0) {
				Vec3i vec=this.getBlockState().getValue(WolfFountainBlock.FACING).getNormal();
				Vec3 center=this.getBlockPos().getCenter().add(vec.getX()*0.75,0.1815,vec.getZ()*0.75);
				
				this.level.addParticle(CVParticles.SPLASH.get().with(fluid.getFluid()),center.x,center.y,center.z,vec.getX()*0.1*getSpeed(), 0,vec.getZ()*0.1*getSpeed());
			}else if(item!=null) {
				Vec3i vec=this.getBlockState().getValue(WolfFountainBlock.FACING).getNormal();
				
				Vec3 center=this.getBlockPos().getCenter().add(vec.getX()*0.75,0.1815,vec.getZ()*0.75);
				this.level.addParticle(CVParticles.SPLASH.get().with(item),center.x,center.y,center.z,vec.getX()*0.1*getSpeed(), 0,vec.getZ()*0.1*getSpeed());
			}
			//}
			return;
		}
		if(getSpeed()>0) {
			if(fluid.isEmpty()&&item==null) {
				Direction face=this.getBlockState().getValue(WolfFountainBlock.FACING);
				Direction backFace=face.getOpposite();
				BlockPos back=this.getBlockPos().relative(backFace);
				Block backBlock=this.getLevel().getBlockState(back).getBlock();

				Optional<IFluidHandler> blockSource = FluidUtil.getFluidHandler(this.getLevel(), back,face);
				if (blockSource.isPresent()) {
					FluidUtil.tryFluidTransfer(fluid, blockSource.orElse(null), 250, true);
					if(!fluid.isEmpty())
						this.syncData();
				} else if (backBlock instanceof BucketPickup bpu) {
					FluidUtil.tryFluidTransfer(fluid,
							new BucketPickupHandlerWrapper(null,bpu,this.getLevel(),back), FluidType.BUCKET_VOLUME,
							true);
					if(!fluid.isEmpty())
						this.syncData();
				}else if(this.getLevel().getBlockEntity(back) instanceof IFoodContainer cont) {
					for(int i=0;i<cont.getSlots();i++) {
						ItemStack its=cont.getInternal(i);
						IFluidHandlerItem ifhi=FluidHandler.ITEM.getCapability(its, null);
						if(ifhi!=null) {
							FluidStack fs=ifhi.getFluidInTank(0);
							if(!fs.isEmpty()) {
								if(fluid.fill(fs, FluidAction.SIMULATE)==fs.getAmount()) {
									fluid.fill(fs, FluidAction.EXECUTE);
									cont.setInternal(i,its.getCraftingRemainingItem());
									this.syncData();
									break;
								}
							}

						}else if(its.is(Items.POTION)) {
							item=its;
							cont.setInternal(i,its.getCraftingRemainingItem());
							this.syncData();
						}

					}
					
				}
			}
		}
	}

	@Override
	public boolean isReceiver() {
		return true;
	}

	@Override
	public boolean isCogTowards(Direction facing) {
		return false;
	}

	@Override
	public boolean isCageTowards(Direction facing) {
		return facing.getAxis()==this.getBlockState().getValue(WolfFountainBlock.FACING).getClockWise().getAxis();
	}

}
