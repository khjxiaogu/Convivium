package com.khjxiaogu.convivium.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;

public class TransformedBakedModel implements BakedModel {
	private static class ModelCache{

		List<BakedQuad> unculled;
		List<List<BakedQuad>> culled=new ArrayList<>(6);
		
		public ModelCache() {
			super();
			for(int i=0;i<6;i++)
				culled.add(null);
		}
		public List<BakedQuad> getQuads(BakedModel original, IQuadTransformer transformer,BlockState pState, Direction pDirection, RandomSource pRandom) {
			if(pDirection==null) {
				if(unculled==null)
					unculled=transformer.process(original.getQuads(pState, pDirection, pRandom));
				return unculled;
			}
			int idx=pDirection.ordinal();
			if(culled.get(idx)==null) {
				culled.set(idx, transformer.process(original.getQuads(pState, pDirection, pRandom)));
			}
			return culled.get(idx);
		}
		public @NotNull List<BakedQuad> getQuads(BakedModel original, IQuadTransformer transformer,@Nullable BlockState state, @Nullable Direction pDirection, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
			if(pDirection==null) {
				if(unculled==null)
					unculled=transformer.process(original.getQuads(state, pDirection, rand, data, renderType));
				return unculled;
			}
			int idx=pDirection.ordinal();
			if(culled.get(idx)==null) {
				culled.set(idx, transformer.process(original.getQuads(state, pDirection, rand, data, renderType)));
			}
			return culled.get(idx);
		}
	}
	BakedModel original;
	IQuadTransformer transformer;
	Function<ModelData,ModelCache> cache=Util.memoize(t->new ModelCache());
	public TransformedBakedModel(BakedModel original,PoseStack stack) {
		this.original=original;
		this.transformer=QuadTransformers.applying(new Transformation(stack.last().pose()));

	}

	@Override
	public List<BakedQuad> getQuads(BlockState pState, Direction pDirection, RandomSource pRandom) {
		
		return cache.apply(ModelData.EMPTY).getQuads(original, transformer, pState, pDirection, pRandom);
	}
	public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction pDirection, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
		return cache.apply(data).getQuads(original, transformer, state, pDirection, rand, data, renderType);
	}
	public boolean useAmbientOcclusion() {
		return original.useAmbientOcclusion();
	}

	public boolean isGui3d() {
		return original.isGui3d();
	}

	public boolean usesBlockLight() {
		return original.usesBlockLight();
	}

	public boolean isCustomRenderer() {
		return original.isCustomRenderer();
	}

	public TextureAtlasSprite getParticleIcon() {
		return original.getParticleIcon();
	}



	public ItemTransforms getTransforms() {
		return original.getTransforms();
	}

	public ItemOverrides getOverrides() {
		return original.getOverrides();
	}

	public boolean useAmbientOcclusion(BlockState state) {
		return original.useAmbientOcclusion(state);
	}

	public boolean useAmbientOcclusion(BlockState state, RenderType renderType) {
		return original.useAmbientOcclusion(state, renderType);
	}

	public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
		return original.applyTransform(transformType, poseStack, applyLeftHandTransform);
	}

	public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
		return original.getModelData(level, pos, state, modelData);
	}

	public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
		return original.getParticleIcon(data);
	}

	public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
		return original.getRenderTypes(state, rand, data);
	}

	public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
		return original.getRenderTypes(itemStack, fabulous);
	}

	public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
		return original.getRenderPasses(itemStack, fabulous);
	}



}
