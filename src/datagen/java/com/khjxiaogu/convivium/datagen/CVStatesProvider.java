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

package com.khjxiaogu.convivium.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import com.khjxiaogu.convivium.CVBlocks;
import com.khjxiaogu.convivium.CVFluids;
import com.khjxiaogu.convivium.CVItems;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductBlock;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductConnection;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductControllerBlock;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductMainConnection;
import com.khjxiaogu.convivium.blocks.camellia.CamelliaFlowerBlock;
import com.khjxiaogu.convivium.blocks.kinetics.CogCageBlock;
import com.khjxiaogu.convivium.blocks.kinetics.KineticBasedBlock;
import com.khjxiaogu.convivium.blocks.vending.BeverageVendingBlock;
import com.mojang.math.Quadrant;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator.Empty;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CVStatesProvider extends BlockModelGenerators {
	protected static final List<Vec3i> COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(),
		BlockPos.ZERO.above(2));
	protected static final Map<Identifier, String> generatedParticleTextures = new HashMap<>();
	String modid;
	ResourceManager input;

	public CVStatesProvider(ResourceManager input, Consumer<BlockModelDefinitionGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput,
		String modid) {
		super(blockStateOutput, itemModelOutput, modelOutput);
		this.modid = modid;
		this.input = input;
	}
	public void horizontalBlock(Block block,MultiVariant model) {
		this.blockStateOutput.accept(
			this.getVariantBuilder(block,model).with(ROTATION_HORIZONTAL_FACING)
			);
	}
	public void horizontalBlock(Block block,PropertyDispatch<MultiVariant> model) {
		this.blockStateOutput.accept(
			this.getVariantBuilder(block)
			.with(model)
			.with(ROTATION_HORIZONTAL_FACING)
			);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		kineticBlockModel("cog");
		kineticBlockModel("cage_wheel");
		kineticDirectionalBlockModel("aeolipile", "aeolipile_stator");
		kineticMixedBlockModel("whisk", "whisk_stator", "whisk_rotor");
		kineticMixedBlockModel("pestle_and_mortar", "pestle_and_mortar_stator", "pestle_and_mortar_rotor");
		horizontalBlock(CVBlocks.basin.get(), bmf("earthen_basin"));
		blockItemModel("basin");
		blockItemModel("lead_basin");
		horizontalBlock(CVBlocks.lead_basin.get(), bmf("lead_basin"));
		blockItemModel("fruit_platter");
		simpleBlock(cvblock("beverage"), bmf("beverage"));
		simpleBlock(cvblock("fruit_platter"), bmf(CPMain.rl("block/dish")));
		simpleBlockItem(cvblock("camellia_plant"), CVMain.rl("camellia_plant"));
		blockItemModel(CVBlocks.CAMELLIA_FLOWER.get(), CVMain.rl("camellia_product_stage_c"));
		this.horizontalBlock(CVBlocks.wolf_fountain.get(), PropertyDispatch.initial(KineticBasedBlock.ACTIVE).generate(bs->bs?bmf("wolf_fountain_2"):bmf("wolf_fountain_1")));
		blockItemModel("wolf_fountain","_1");
		this.blockStateOutput.accept(this.getVariantBuilder(CVBlocks.CAMELLIA_FLOWER.get())
		.with(PropertyDispatch.initial(CamelliaFlowerBlock.AGE)
			.generate(t->switch(t) {
			case 0->createRotatedVariants(bmfs("camellia_product_stage_1"));
			case 1->createRotatedVariants(bmfs("camellia_product_stage_2b"));
			case 2->createRotatedVariants(bmfs("camellia_product_stage_2a"));
			default->createRotatedVariants(bmfs("camellia_product_stage_c"));
			}
			)));
		blockItemModel("beverage_vending_machine");
		this.horizontalBlock(CVBlocks.BEVERAGE_VENDING_MACHINE.get(), PropertyDispatch.initial(BeverageVendingBlock.ACTIVE).generate(bs->bs? bmf("beverage_vending_machine_active") : bmf("beverage_vending_machine")));
		for (String s : new String[] { "felsic_tuff", "stone", "sandstone" }) {
			this.blockStateOutput.accept(
			this.getVariantBuilder(cvblock(s + "_aqueduct")).with(PropertyDispatch.initial(AqueductBlock.CONN)
				.select(AqueductConnection.X,bmf(s + "_aqueduct_straight"))
				.select(AqueductConnection.Z,bmf(s + "_aqueduct_straight").with(Y_ROT_90))
				.select(AqueductConnection.N,bmf(s + "_aqueduct_end").with(Y_ROT_90))
				.select(AqueductConnection.E,bmf(s + "_aqueduct_end").with(Y_ROT_180))
				.select(AqueductConnection.S,bmf(s + "_aqueduct_end").with(Y_ROT_270))
				.select(AqueductConnection.W,bmf(s + "_aqueduct_end"))
				.select(AqueductConnection.NE,bmf(s + "_aqueduct_corner").with(Y_ROT_90))
				.select(AqueductConnection.NW,bmf(s + "_aqueduct_corner"))//
				.select(AqueductConnection.SW,bmf(s + "_aqueduct_corner").with(Y_ROT_270))
				.select(AqueductConnection.SE,bmf(s + "_aqueduct_corner").with(Y_ROT_180))
				.select(AqueductConnection.A,bmf(s + "_aqueduct_isolated"))
				));
			this.blockStateOutput.accept(
				this.getVariantBuilder(cvblock(s + "_aqueduct_wavemaker"))
				.with(PropertyDispatch.initial(AqueductControllerBlock.CONN,BlockStateProperties.HORIZONTAL_FACING)
				.generate((conn,dir)->{
					
					MultiVariant model=switch (conn) {
					case N->bmf(s + "_aqueduct_wavemaker_stator_isolated");
					case L->bmf(s + "_aqueduct_wavemaker_stator_end");
					case R->bmf(s + "_aqueduct_wavemaker_stator_end");
					case A->bmf(s + "_aqueduct_wavemaker_stator");
					};
					boolean rev = conn==AqueductMainConnection.L;
					return model.with(VariantMutator.Y_ROT.withValue(Quadrant.parseJson((int) dir.toYRot() + (rev ? 0 : 180))));

				})));

			this.blockItemModel(cvblock(s + "_aqueduct"), CVMain.rl(s + "_aqueduct_straight"));

			this.blockItemModel(cvblock(s + "_aqueduct_wavemaker"), CVMain.rl(s + "_aqueduct_wavemaker_stator"));
		}
		empty(CVBlocks.FLAT_BREAD.get());
		for(String s:CVFluids.sorbets) {
			empty(s+"_sorbet");
		}
		for(String bottleType:CVItems.bottles) {
			empty("beverage_"+bottleType);
		}
	}

	private Block cvblock(String name) {
		return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(this.modid, name));
	}

	protected void kineticDirectionalBlockModel(String name, String stator) {
		this.blockStateOutput.accept(horizontalMultipart(this.getMultipartBuilder(cvblock(name)), bmf(stator), c -> c));
		blockItemModel(name);

	}

	protected void kineticMixedBlockModel(String name, String stator, String rotor) {
		/*
		 * this.getVariantBuilder(cvblock(name)).partialState().modelForState().
		 * modelFile(bmf(stator)).addModel(); blockItemModel(name);
		 */
		this.blockStateOutput.accept(
		this.getMultipartBuilder(cvblock(name))
		.with(condition(KineticBasedBlock.ACTIVE, false), bmf("dynamic/" + rotor))
		.with(bmf(stator))
		);
		blockItemModel(name);

	}
	protected void empty(Block name) {
		this.blockStateOutput.accept(
		this.getVariantBuilder(name,bmf(Identifier.withDefaultNamespace("block/block")))
		);

	}
	protected void empty(String name) {
		empty(cvblock(name));

	}
	protected void kineticBlockModel(String name) {
		this.blockStateOutput.accept(
		this.getMultipartBuilder(cvblock(name))
		.with(condition(CogCageBlock.ACTIVE, false), bmf("dynamic/" + name))
		);

		blockItemModel(name);
	}

	protected Empty getVariantBuilder(Block blk) {
		return MultiVariantGenerator.dispatch(blk);
	}

	protected MultiVariantGenerator getVariantBuilder(Block blk,MultiVariant model) {
		return MultiVariantGenerator.dispatch(blk,model);
	}

	private Block cpblock(String name) {
		return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(this.modid, name));
	}

	protected void blockItemModel(String n) {
		blockItemModel(n, "");
	}
	public void simpleTexture(String name, String par) {
		this.itemModelOutput.accept(BuiltInRegistries.ITEM.getValue(CVMain.rl(name)),
		ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(CVMain.rl("item/" + name),new TextureMapping().put(TextureSlot.LAYER0, new Material(CVMain.rl("item/" + par + name),false)), this.modelOutput))
		);

	}
	public void simpleTexture(Item item) {
		this.itemModelOutput.accept(item,
		ItemModelUtils.plainModel(this.createFlatItemModel(item))
		);
	}
	public void texture(String name) {
		texture(name, name);
	}
	public void texture(Item name, String par) {
		this.itemModelOutput.accept(name,
			ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(name), TextureMapping.layer0(new Material(Identifier.fromNamespaceAndPath(CVMain.MODID, "item/"+par))), this.modelOutput)
				));
	}
	public void texture(String name, String par) {
		texture(BuiltInRegistries.ITEM.getValue(CVMain.rl(name)),par);
	}

	protected void blockItemModel(String n, String p) {
		if (input.getResource(Identifier.fromNamespaceAndPath(CVMain.MODID, "textures/item/" + n + p + ".png")).isPresent()) {

			texture(n, n + p);
		} else {
			blockItemModel(cpblock(n), CVMain.rl(n + p));
		}
	}

	protected void blockItemModel(Block n, Identifier p) {
		Identifier blockModelId=p.withPrefix("block/");
		String name=p.getPath();
		if(existsModel(blockModelId)) {

			this.itemModelOutput.accept(n.asItem(), ItemModelUtils.plainModel(blockModelId));
		}else {
			List<String> rn = Arrays.asList(name.split("_"));
			for (int i = rn.size(); i >= 0; i--) {
				List<String> rrn = new ArrayList<>(rn);
				rrn.add(i, "0");
				blockModelId = Identifier.fromNamespaceAndPath(this.modid, "block/" + String.join("_", rrn));
				if (existsModel(blockModelId)) {
					this.itemModelOutput.accept(n.asItem(), ItemModelUtils.plainModel(blockModelId));
					return;
				}
			}
			

			throw new IllegalArgumentException("model does not exists: "+p);
		}
	}

	public void stove(Block block) {
		this.blockStateOutput.accept(
		
			horizontalMultipart(this.getMultipartBuilder(block),
				bmf(Utils.getRegistryName(block).getPath())));
		blockItemModel(block, Utils.getRegistryName(block));

	}

	public boolean existsModel(Identifier id) {
		return input.getResource(id.withPrefix("models/").withSuffix(".json")).isPresent();

	}

	public MultiVariant bmf(String name) {
		return super.variant(bmfs(name));
	}
	
	public Variant bmfs(String name) {
		Identifier orl = Identifier.fromNamespaceAndPath(this.modid, "block/" + name);
		Identifier rl = orl;

		if (!existsModel(rl)) {// not exists, let's guess
			List<String> rn = Arrays.asList(name.split("_"));
			for (int i = rn.size(); i >= 0; i--) {
				List<String> rrn = new ArrayList<>(rn);
				rrn.add(i, "0");
				rl = Identifier.fromNamespaceAndPath(this.modid, "block/" + String.join("_", rrn));
				if (existsModel(rl))
					return super.plainModel(rl);
			}
			CVMain.logger.warn("Model file " + orl + " not exists, using unchecked");
		}
		
		return super.plainModel(rl);
	}

	public MultiVariant bmf(Identifier name) {
		return super.variant(bmfs(name));
	}

	public Variant bmfs(Identifier orl) {
		return super.plainModel(orl);
	}

	protected void simpleBlockItem(Block b, Identifier model) {
		this.blockStateOutput.accept(createSimpleBlock(b, bmf(model.withPrefix("block/"))));
		blockItemModel(b, model);
	}
	protected void simpleBlock(Block b, MultiVariant model) {
		this.blockStateOutput.accept(createSimpleBlock(b, model));
	}
	public void horizontalAxisBlock(Block block, MultiVariant mf) {

		this.blockStateOutput
			.accept(getVariantBuilder(block).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_AXIS)
				.select(Axis.Z, mf)
				.select(Axis.X, mf.with(Y_ROT_90))));

	}

	public MultiPartGenerator horizontalMultipart(MultiPartGenerator generator, MultiVariant variant) {
		forEachHorizontalDirection((direction, rotation) -> generator.with(condition(BlockStateProperties.HORIZONTAL_FACING, direction), variant.with(rotation)));
		return generator;
	}

	public MultiPartGenerator horizontalMultipart(MultiPartGenerator generator, MultiVariant variant,
		UnaryOperator<ConditionBuilder> act) {
		forEachHorizontalDirection((direction, rotation) -> generator.with(act.apply(condition(BlockStateProperties.HORIZONTAL_FACING, direction)), variant.with(rotation)));

		return generator;
	}

	protected MultiPartGenerator getMultipartBuilder(Block block) {
		return MultiPartGenerator.multiPart(block);
	}
}
