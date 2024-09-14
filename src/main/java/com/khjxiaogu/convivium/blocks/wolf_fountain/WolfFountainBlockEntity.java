package com.khjxiaogu.convivium.blocks.wolf_fountain;

import org.joml.Vector2i;

import com.khjxiaogu.convivium.CVBlockEntityTypes;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductControllerBlock;
import com.khjxiaogu.convivium.blocks.kinetics.Cog;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.blocks.kinetics.KineticTransferBlockEntity;
import com.khjxiaogu.convivium.client.CVParticles;
import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WolfFountainBlockEntity extends KineticTransferBlockEntity implements Cog{
	int color;
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
	public void tick() {
		super.tick();
		if(this.level.isClientSide) {
			//if(this.level.getGameTime()%20==0) {
			Vec3i vec=this.getBlockState().getValue(WolfFountainBlock.FACING).getNormal();
		
			Vec3 center=this.getBlockPos().getCenter().add(vec.getX()*0.75,0.1815,vec.getZ()*0.75);
			this.level.addParticle(CVParticles.SPLASH.get().withColor(0xffffffff),center.x,center.y,center.z,vec.getX()*0.1*getSpeed(), 0,vec.getZ()*0.1*getSpeed());
			//}
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
