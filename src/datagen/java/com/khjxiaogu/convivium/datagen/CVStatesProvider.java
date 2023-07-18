/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
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
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import com.khjxiaogu.convivium.CVMain;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductBlock;
import com.khjxiaogu.convivium.blocks.aqueduct.AqueductConnection;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import net.minecraftforge.registries.ForgeRegistries;

public class CVStatesProvider extends BlockStateProvider {
	protected static final List<Vec3i> COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(),
			BlockPos.ZERO.above(2));
	protected static final ResourceType MODEL = new ResourceType(PackType.CLIENT_RESOURCES, ".json", "models");
	protected static final Map<ResourceLocation, String> generatedParticleTextures = new HashMap<>();
	protected final ExistingFileHelper existingFileHelper;
	String modid;

	public CVStatesProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
		super(gen.getPackOutput(), modid, exFileHelper);
		this.modid = modid;
		this.existingFileHelper = exFileHelper;
	}

	@Override
	protected void registerStatesAndModels() {
		kineticBlockModel("cog");
		kineticBlockModel("cage_wheel");
		kineticDirectionalBlockModel("aeolipile","aeolipile_stator");
		kineticMixedBlockModel("whisk","whisk_stator");
		kineticMixedBlockModel("pestle_and_mortar","pestle_and_mortar_stator");
		blockItemModel("fruit_platter");
		simpleBlock(cvblock("fruit_platter"),obmf(CPMain.MODID,"dish"));
		for(String s:new String[] {"felsic_tuff","stone","sandstone"}) {
			
			this.getVariantBuilder(cvblock(s+"_aqueduct"))
			.partialState().with(AqueductBlock.CONN,AqueductConnection.X).modelForState().modelFile(bmf(s+"_aqueduct_straight")).addModel()
			.partialState().with(AqueductBlock.CONN,AqueductConnection.Z).modelForState().modelFile(bmf(s+"_aqueduct_straight")).rotationY(90).addModel()
			.partialState().with(AqueductBlock.CONN,AqueductConnection.N).modelForState().modelFile(bmf(s+"_aqueduct_end")).rotationY(90).addModel()
			.partialState().with(AqueductBlock.CONN,AqueductConnection.E).modelForState().modelFile(bmf(s+"_aqueduct_end")).rotationY(180).addModel()
			.partialState().with(AqueductBlock.CONN,AqueductConnection.S).modelForState().modelFile(bmf(s+"_aqueduct_end")).rotationY(270).addModel()
			.partialState().with(AqueductBlock.CONN,AqueductConnection.W).modelForState().modelFile(bmf(s+"_aqueduct_end")).rotationY(0).addModel()
			.partialState().with(AqueductBlock.CONN,AqueductConnection.NE).modelForState().modelFile(bmf(s+"_aqueduct_corner")).rotationY(90).addModel()
			.partialState().with(AqueductBlock.CONN,AqueductConnection.NW).modelForState().modelFile(bmf(s+"_aqueduct_corner")).rotationY(0).addModel()//
			.partialState().with(AqueductBlock.CONN,AqueductConnection.SW).modelForState().modelFile(bmf(s+"_aqueduct_corner")).rotationY(270).addModel()
			.partialState().with(AqueductBlock.CONN,AqueductConnection.SE).modelForState().modelFile(bmf(s+"_aqueduct_corner")).rotationY(180).addModel();
			
			this.horizontalBlock(cvblock(s+"_aqueduct_wavemaker"), bmf(s+"_aqueduct_wavemaker_stator"));
			this.itemModel(cvblock(s+"_aqueduct"), bmf(s+"_aqueduct_straight"));
			
			this.itemModel(cvblock(s+"_aqueduct_wavemaker"), bmf(s+"_aqueduct_wavemaker_stator"));
		}
	}

	private Block cvblock(String name) {
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(this.modid, name));
	}
	protected void kineticDirectionalBlockModel(String name,String stator) {
		horizontalMultipart(this.getMultipartBuilder(cvblock(name)),bmf(stator),c->c);
		blockItemModel(name);
		
	} 
	protected void kineticMixedBlockModel(String name,String stator) {
		this.getVariantBuilder(cvblock(name)).partialState().modelForState().modelFile(bmf(stator)).addModel();
		blockItemModel(name);
		
	} 
	protected void kineticBlockModel(String name) {
		
		this.getMultipartBuilder(cvblock(name)).part().modelFile(bmf("dynamic/"+name)).addModel().end();
		blockItemModel(name);
	} 
	protected void blockItemModel(String n) {
		blockItemModel(n, "");
	}

	protected void blockItemModel(String n, String p) {
		if (this.existingFileHelper.exists(new ResourceLocation(CVMain.MODID, "textures/item/" + n + p + ".png"),
				PackType.CLIENT_RESOURCES)) {
			itemModels().basicItem(new ResourceLocation(CVMain.MODID, n));
		} else {
			itemModels().getBuilder(n).parent(bmf(n + p));
		}
	}
	public ModelFile obmf(String modid,String name) {
		ResourceLocation rl = new ResourceLocation(modid, "block/" + name);
		return new ModelFile.UncheckedModelFile(rl);
	}
	
	public ModelFile bmf(String modid,String name) {
		ResourceLocation rl = new ResourceLocation(modid, "block/" + name);
		if (!existingFileHelper.exists(rl, MODEL)) {// not exists, let's guess
			List<String> rn = Arrays.asList(name.split("_"));
			for (int i = rn.size(); i >= 0; i--) {
				List<String> rrn = new ArrayList<>(rn);
				rrn.add(i, "0");
				rl = new ResourceLocation(modid, "block/" + String.join("_", rrn));
				if (existingFileHelper.exists(rl, MODEL))
					break;
			}

		}
		return new ModelFile.ExistingModelFile(rl, existingFileHelper);
	}
	public ModelFile bmf(String name) {
		return bmf(this.modid,name);
	}

	public void simpleBlockItem(Block b, ModelFile model) {
		simpleBlockItem(b, new ConfiguredModel(model));
	}

	protected void simpleBlockItem(Block b, ConfiguredModel model) {
		simpleBlock(b, model);
		itemModel(b, model.model);
	}

	public void horizontalAxisBlock(Block block, ModelFile mf) {
		getVariantBuilder(block).partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Axis.Z).modelForState()
				.modelFile(mf).addModel().partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Axis.X)
				.modelForState().modelFile(mf).rotationY(90).addModel();
	}

	public MultiPartBlockStateBuilder horizontalMultipart(MultiPartBlockStateBuilder block, ModelFile mf) {
		return horizontalMultipart(block, mf, UnaryOperator.identity());
	}

	public MultiPartBlockStateBuilder horizontalMultipart(MultiPartBlockStateBuilder block, ModelFile mf,
			UnaryOperator<PartBuilder> act) {
		for (Direction d : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues())
			block = act.apply(block.part().modelFile(mf).rotationY(((int) d.toYRot()) % 360).addModel()
					.condition(BlockStateProperties.HORIZONTAL_FACING, d)).end();
		return block;
	}

	protected void itemModel(Block block, ModelFile model) {
		itemModels().getBuilder(Utils.getRegistryName(block).getPath()).parent(model);
	}
}
