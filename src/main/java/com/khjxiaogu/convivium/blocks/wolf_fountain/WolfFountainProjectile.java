package com.khjxiaogu.convivium.blocks.wolf_fountain;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WolfFountainProjectile extends Projectile {

	public WolfFountainProjectile(EntityType<? extends Projectile> entityType, Level level) {
		super(entityType, level);
		// TODO Auto-generated constructor stub
	}

	int age;
	int verid;
	BlockPos source;
	final int lifetime=20;
    @Override
    public void tick() {
        super.tick();
       
        Vec3 vec33 = this.getDeltaMovement();
        if (this.age++ >= this.lifetime) {
            this.remove(RemovalReason.DISCARDED);
        } else {
        	vec33=vec33.add(0,-0.04,0);
        	this.move(MoverType.SELF, vec33);
        	vec33=vec33.scale(0.98);
        }
        this.setDeltaMovement(vec33);
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (!this.noPhysics) {
            this.hitTargetOrDeflectSelf(hitresult);
            this.hasImpulse = true;
        }

        this.updateRotation();
    }
	@Override
	protected void defineSynchedData(Builder builder) {

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		age = compound.getInt("age");
		verid = compound.getInt("version");
		ListTag listtag = compound.getList("SourceBE", Tag.TAG_INT);
		source = new BlockPos(listtag.getInt(0),listtag.getInt(1),listtag.getInt(2));
		
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("age", age);
		compound.putInt("version",verid);
		ListTag tag=new ListTag();
		tag.add(IntTag.valueOf(source.getX()));
		tag.add(IntTag.valueOf(source.getY()));
		tag.add(IntTag.valueOf(source.getZ()));
		compound.put("SourceBE", tag);
		
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		//System.out.println("hit entity");
		if(source!=null)
			if(result.getEntity() instanceof LivingEntity&&this.level().getBlockEntity(source) instanceof WolfFountainBlockEntity wf) {
				wf.applyEffectTo(verid, (LivingEntity)result.getEntity());
			}
		this.remove(RemovalReason.DISCARDED);
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		//System.out.println("hit block");
		if(source!=null)
			if(this.level().getBlockEntity(source) instanceof WolfFountainBlockEntity wf) {
				wf.applyEffectTo(verid, result.getBlockPos(),result.getDirection());
			}
		this.remove(RemovalReason.DISCARDED);
	}

}
