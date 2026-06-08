package com.khjxiaogu.convivium.client.renderer;

import org.joml.Quaternionf;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FruitPlatterRenderState extends BlockEntityRenderState {
	public static class FruitPlatterRenderingContext{
		private static final Quaternionf[] piled_rotations=new Quaternionf[] {
			new Quaternionf().rotateY((float) (Math.PI/2*2/4)),
			new Quaternionf().rotateY((float) (Math.PI/2*3/4)),
			new Quaternionf().rotateY((float) (Math.PI/2*4/4)),
			new Quaternionf().rotateY((float) (Math.PI/2*5/4))
		};
		private static final Quaternionf[] grided_rotations=new Quaternionf[] {
			new Quaternionf().rotateXYZ((float) ((90+10)/180f*Math.PI),-(float) (10/180f*Math.PI),-(float) (15/180f*Math.PI)),
			new Quaternionf().rotateXYZ((float) ((90+10)/180f*Math.PI),+(float) (15/180f*Math.PI),-(float) (15/180f*Math.PI)),
			new Quaternionf().rotateXYZ((float) ((90-15)/180f*Math.PI),-(float) (15/180f*Math.PI),+(float) (15/180f*Math.PI)),
			new Quaternionf().rotateXYZ((float) ((90-15)/180f*Math.PI),+(float) (15/180f*Math.PI),+(float) (15/180f*Math.PI))
		};
		@SuppressWarnings("unchecked")
		private static final ImmutableSet<String>[] model_names=new ImmutableSet[] {
			ImmutableSet.of("FruitUnit1"),
			ImmutableSet.of("FruitUnit2"),
			ImmutableSet.of("FruitUnit3"),
			ImmutableSet.of("FruitUnit4")
		}; 
		public static class FruitPlatterRenderingPart{
			int type;
			int modelIndex;
			FruitModel model;
			ItemStackRenderState stack=new ItemStackRenderState();
			public FruitPlatterRenderingPart(FruitModel model,boolean isGrided) {
				super();
				type=isGrided?5:1;
				this.model = model;
			}
			public FruitPlatterRenderingPart(int modelIndex, FruitModel model) {
				super();
				type=2;
				this.modelIndex = modelIndex;
				this.model = model;
			}
			public FruitPlatterRenderingPart(ItemModelResolver ir,Level level,ItemStack stack,boolean isGrided) {
				super();
				type=isGrided?4:3;
				ir.appendItemLayers(this.stack, stack,ItemDisplayContext.GROUND,level, null, 7+type);

			}
		}
		public void setPart(int position,FruitModel model,boolean isGrided) {
			parts[position-1]=new FruitPlatterRenderingPart(model,isGrided);
		}
		public void setPart(int position,int modelIndex, FruitModel model) {
			parts[position-1]=new FruitPlatterRenderingPart(modelIndex,model);
		}
		public void setPart(int position,ItemModelResolver ir,Level level,ItemStack stack,boolean isGrided) {
			parts[position-1]=new FruitPlatterRenderingPart(ir, level, stack,isGrided);
		}
		FruitPlatterRenderingPart[] parts=new FruitPlatterRenderingPart[4];
		public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {
			
			for(int i=1;i<=4;i++) {
				FruitPlatterRenderingPart cpart=parts[i-1];
				if(cpart!=null) {
					switch(cpart.type) {
					case 1:renderPartPiledAllFruit(i,cpart.model,poseStack,submitNodeCollector,camera,instance);break;
					case 2:renderPartPiledSingleFruit(i,cpart.modelIndex,cpart.model,poseStack,submitNodeCollector,camera,instance);break;
					case 3:renderPartPiledItem(i,cpart.stack,poseStack,submitNodeCollector,camera,instance);break;
					case 4:renderPartGridedItem(i,cpart.stack,poseStack,submitNodeCollector,camera,instance);break;
					case 5:renderGridedFruit(i,cpart.model,poseStack,submitNodeCollector,camera,instance);break;
					}
				}
				
			}
			
		}
		public static void renderPartPiledAllFruit(int position,FruitModel rss,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {
			submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose,buffer)->{
				for(BakedQuad quad:rss.getPiled(position-1,position-1).get().getAll()) {
					buffer.putBakedQuad(pose, quad, instance);
				}
			});		}
		public static void renderPartPiledSingleFruit(int position,int modelIndex,FruitModel rss,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {
			submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose,buffer)->{
				for(BakedQuad quad:rss.getPiled(modelIndex,position-1).get().getAll()) {
					buffer.putBakedQuad(pose, quad, instance);
				}
			});
		}
		public static void renderGridedFruit(int position,FruitModel rss,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {
			submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose,buffer)->{
				for(BakedQuad quad:rss.getGrid(position-1).get().getAll()) {
					buffer.putBakedQuad(pose, quad, instance);
				}
			});
		}
		public static void renderPartPiledItem(int position,ItemStackRenderState is,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {
			poseStack.pushPose();
			poseStack.translate(0.375, 3/16f, 0.5f);
			poseStack.scale(1.5f,1, 1.5f);
			poseStack.mulPose(piled_rotations[position-1]);
			poseStack.mulPose(FluidRenderHelper.rotate90);
			poseStack.translate(0,0,-(position-1)/32f);
			is.submit(poseStack, submitNodeCollector, instance.getLightCoords(0), OverlayTexture.NO_OVERLAY, 0xff000000);
			poseStack.popPose();
		}
		public static void renderPartGridedItem(int position,ItemStackRenderState is,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {

			
			poseStack.pushPose();
			poseStack.translate((((position&1)==0)?11/16f:5/16f),3/16f,(position<=2?4/16f:11/16f));
			
			poseStack.mulPose(grided_rotations[position-1]);
			//matrixStack.scale(.85f, .85f, .85f);
			is.submit(poseStack, submitNodeCollector, instance.getLightCoords(0), OverlayTexture.NO_OVERLAY, 0xff000000);
			poseStack.popPose();
		}
		
		

	}
	FruitPlatterRenderingContext ctx;
}
