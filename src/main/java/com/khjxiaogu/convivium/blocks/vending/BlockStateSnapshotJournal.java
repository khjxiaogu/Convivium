package com.khjxiaogu.convivium.blocks.vending;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;

public class BlockStateSnapshotJournal extends SnapshotJournal<BlockState> {
	public final BeverageVendingBlockEntity blockEntity;
	BlockState bs;

	public BlockStateSnapshotJournal(BeverageVendingBlockEntity blockEntity,BlockState bs) {
		super();
		this.blockEntity = blockEntity;
		this.bs=bs;
	}

	@Override
	protected BlockState createSnapshot() {
		return bs;
	}

	@Override
	protected void revertToSnapshot(BlockState snapshot) {
		bs=snapshot;
	}
	@Override
	protected void onRootCommit(BlockState originalState) {
		super.onRootCommit(originalState);
		blockEntity.getLevel().setBlockAndUpdate(blockEntity.getBlockPos(), originalState);
	}
}
