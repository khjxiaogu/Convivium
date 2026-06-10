package com.khjxiaogu.convivium.datagen;

import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CVModelProvider extends ModelProvider {
	ResourceManager resource;
	public CVModelProvider(PackOutput output, String modId,ResourceManager resource) {
		super(output, modId);
		this.resource=resource;
	}
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        new CVStatesProvider(resource, blockModels.blockStateOutput,blockModels.itemModelOutput,blockModels.modelOutput,modId).run();
        new CVItemModelProvider(blockModels.itemModelOutput,blockModels.modelOutput).run();
    
    }
    protected java.util.stream.Stream<? extends net.minecraft.core.Holder<Block>> getKnownBlocks() {
        return CVBlocks.BLOCKS.getEntries().stream().filter(t->!resource.getResource(t.getId().withPrefix("blockstates/").withSuffix(".json")).isPresent());
    }

    protected java.util.stream.Stream<? extends net.minecraft.core.Holder<Item>> getKnownItems() {
    	return CVItems.ITEMS.getEntries().stream();
    }
}
