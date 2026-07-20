package com.khjxiaogu.convivium.client.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

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
		public static class FruitPlatterRenderingPart{
			int type;
			int modelIndex;
			FruitModel model;
			ItemStack stack;
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
			public FruitPlatterRenderingPart(ItemStack stack,boolean isGrided) {
				super();
				type=isGrided?4:3;
				this.stack=stack;
			}
			@Override
			public String toString() {
				return "FruitPlatterRenderingPart [type=" + type + ", model=" + model + ", modelIndex=" + modelIndex + ", stack=" + stack + "]";
			}
		}
		public void setPart(int position,FruitModel model,boolean isGrided) {
			parts[position-1]=new FruitPlatterRenderingPart(model,isGrided);
		}
		public void setPart(int position,int modelIndex, FruitModel model) {
			parts[position-1]=new FruitPlatterRenderingPart(modelIndex,model);
		}
		public void setPart(int position,ItemStack stack,boolean isGrided) {
			parts[position-1]=new FruitPlatterRenderingPart(stack,isGrided);
		}
		public void extractRenderState(BlockEntityRendererProvider.Context context,BlockEntity be) {
			for(int i=0;i<4;i++) {

				FruitPlatterRenderingPart cpart=parts[i];
				if(cpart!=null) {
					if(cpart.stack!=null) {
						items[i]=new ItemStackRenderState();
						context.itemModelResolver()
						.appendItemLayers(items[i], cpart.stack,ItemDisplayContext.GROUND,be.getLevel(), null, 7+i);
						continue;
					}
				}
				items[i]=null;
			}
		}
		FruitPlatterRenderingPart[] parts=new FruitPlatterRenderingPart[4];
		ItemStackRenderState[] items=new ItemStackRenderState[4];
		QuadInstance[] quads=new QuadInstance[4];
		
		public FruitPlatterRenderingContext() {
			super();
			for(int i=0;i<4;i++) 
				quads[i]=new QuadInstance();
		}
		public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, BlockEntityRenderState state) {
			
			for(int i=1;i<=4;i++) {

				quads[i-1].setLightCoords(state.lightCoords);
				FruitPlatterRenderingPart cpart=parts[i-1];
				if(cpart!=null) {
					switch(cpart.type) {
					case 1:renderPartPiledAllFruit(i,cpart.model,poseStack,submitNodeCollector,camera,quads[i-1]);break;
					case 2:renderPartPiledSingleFruit(i,cpart.modelIndex,cpart.model,poseStack,submitNodeCollector,camera,quads[i-1]);break;
					case 3:renderPartPiledItem(i,items[i-1],poseStack,submitNodeCollector,camera,state.lightCoords);break;
					case 4:renderPartGridedItem(i,items[i-1],poseStack,submitNodeCollector,camera,state.lightCoords);break;
					case 5:renderGridedFruit(i,cpart.model,poseStack,submitNodeCollector,camera,quads[i-1]);break;
					}
				}
				
			}
			
		}
		public static void renderPartPiledAllFruit(int position,FruitModel rss,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {
			DynamicBlockModelReference dmr=rss.getPiled(position-1);
			if(dmr!=null) {
				drawModel(dmr,submitNodeCollector,poseStack,rss.getRenderType(),instance,rss.hasFoil());
			}else {
				for(int i=1;i<=position;i++) {
					drawModel(rss.getPiled(position-1, i-1),submitNodeCollector,poseStack,rss.getRenderType(),instance,rss.hasFoil());
				}
			}
		}
		public static void renderPartPiledSingleFruit(int position,int modelIndex,FruitModel rss,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {
			drawModel(rss.getPiled(modelIndex,position-1),submitNodeCollector,poseStack,rss.getRenderType(),instance,rss.hasFoil());
		}
		public static void renderGridedFruit(int position,FruitModel rss,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, QuadInstance instance) {
			drawModel(rss.getGrid(position-1),submitNodeCollector,poseStack,rss.getRenderType(),instance,rss.hasFoil());
			
		}
		public static void drawModel(DynamicBlockModelReference model,SubmitNodeCollector submitNodeCollector,PoseStack poseStack,RenderType renderType,QuadInstance quadInstance,boolean hasFoil) {
			model.submit(submitNodeCollector, poseStack, renderType, quadInstance);
			if(hasFoil)
				model.submit(submitNodeCollector, poseStack, RenderTypes.glintTranslucent(), quadInstance);
		}
		public static void renderPartPiledItem(int position,ItemStackRenderState is,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, int lightCoords) {
			poseStack.pushPose();
			poseStack.translate(0.375, 3/16f, 0.5f);
			poseStack.scale(1.5f,1, 1.5f);
			poseStack.mulPose(piled_rotations[position-1]);
			poseStack.mulPose(FluidRenderHelper.rotate90);
			poseStack.translate(0,0,-(position-1)/32f);
			is.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
			poseStack.popPose();
		}
		public static void renderPartGridedItem(int position,ItemStackRenderState is,PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, int lightCoords) {
			poseStack.pushPose();
			poseStack.translate((((position&1)==0)?11/16f:5/16f),3/16f,(position<=2?4/16f:11/16f));
			poseStack.mulPose(grided_rotations[position-1]);
			//matrixStack.scale(.85f, .85f, .85f);
			is.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
			poseStack.popPose();
		}
		
		

	}
	FruitPlatterRenderingContext ctx;
}
