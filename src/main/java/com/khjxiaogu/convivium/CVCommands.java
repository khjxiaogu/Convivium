package com.khjxiaogu.convivium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.khjxiaogu.convivium.data.recipes.numbers.Expression;
import com.khjxiaogu.convivium.data.recipes.numbers.INumber;
import com.khjxiaogu.convivium.util.evaluator.ConstantEnvironment;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber
public class CVCommands {
	private static LazyOptional<BlockInput[]> def_palette=LazyOptional.of(()->
		new BlockInput[] {
			      new BlockInput(Blocks.WHITE_WOOL.defaultBlockState(),Set.of(), null),
			      new BlockInput(Blocks.ORANGE_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.MAGENTA_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.LIGHT_BLUE_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.YELLOW_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.LIME_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.PINK_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.GRAY_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.LIGHT_GRAY_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.CYAN_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.PURPLE_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.BLUE_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.BROWN_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.GREEN_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.RED_WOOL.defaultBlockState(), Set.of(), null),
			      new BlockInput(Blocks.BLACK_WOOL.defaultBlockState(), Set.of(), null)
		}
	);
	private static BlockInput[] def_nopalette=new BlockInput[] {
			new BlockInput(Blocks.STONE.defaultBlockState(),Set.of(), null)
	};
	public static LiteralArgumentBuilder<CommandSourceStack> plot(CommandBuildContext ctx) {
		return Commands.literal("plot").then(
				Commands.argument("expression", StringArgumentType.string()).then(
					Commands.argument("pos1", BlockPosArgument.blockPos()).then(
							createSub(0,false,ctx,Commands.argument("pos2", BlockPosArgument.blockPos())
									.requires(e->e.hasPermission(3))
									.executes(e->{
										INumber expr=Expression.of(StringArgumentType.getString(e, "expression"));
										BoundingBox bb=BoundingBox.fromCorners(BlockPosArgument.getBlockPos(e, "pos1"), BlockPosArgument.getBlockPos(e, "pos2"));
										ServerLevel level=e.getSource().getLevel();
										
										BlockInput[] ips=def_palette.orElse(def_nopalette);
										CVCommands.place(bb, level, expr, ips,false);
										e.getSource().sendSuccess(()->Utils.string("Succeed!"), false);
										return Command.SINGLE_SUCCESS;
									}))
							)
						)
				).then(
						Commands.literal("replace").then(
						Commands.argument("expression", StringArgumentType.string()).then(
								Commands.argument("pos1", BlockPosArgument.blockPos()).then(
										createSub(0,true,ctx,Commands.argument("pos2", BlockPosArgument.blockPos())
												.requires(e->e.hasPermission(3))
												.executes(e->{
													INumber expr=Expression.of(StringArgumentType.getString(e, "expression"));
													BoundingBox bb=BoundingBox.fromCorners(BlockPosArgument.getBlockPos(e, "pos1"), BlockPosArgument.getBlockPos(e, "pos2"));
													ServerLevel level=e.getSource().getLevel();
													
													BlockInput[] ips=def_palette.orElse(def_nopalette);
													CVCommands.place(bb, level, expr, ips,true);
													e.getSource().sendSuccess(()->Utils.string("Succeed!"), false);
													return Command.SINGLE_SUCCESS;
												}))
										)
									)
							)
						);
	}
	private static ArgumentBuilder<CommandSourceStack, ?> createSub(int last,boolean rep,CommandBuildContext ctx,ArgumentBuilder<CommandSourceStack, ?> par){
		if(last>=16)
			return par;
		RequiredArgumentBuilder<CommandSourceStack, ?> child=Commands.argument("block"+last,BlockStateArgument.block(ctx))
				.requires(e->e.hasPermission(3))
				.executes(createPlotter(last+1,rep));
		par.then(createSub(last+1,rep,ctx,child)); 
		return par;
	}
	private static void place(BoundingBox bb,ServerLevel level,INumber expr,BlockInput[] ips,boolean replace) {
		Map<String,Double> env=new HashMap<>();
		ConstantEnvironment venv=new ConstantEnvironment(env);
		BlockPos.MutableBlockPos posx=new MutableBlockPos();
		List<BlockPos> poss=new ArrayList<>();
		int lx=bb.minX();
		int hx=bb.maxX();
		int ly=bb.minY();
		int hy=bb.maxY();
		int lz=bb.minZ();
		int hz=bb.maxZ();
		for(int x=lx;x<=hx;x++)
			for(int y=ly;y<=hy;y++)
				for(int z=lz;z<hz;z++) {
					posx.set(x, y, z);
					env.put("x", (double) x-lx);
					env.put("y", (double) y-ly);
					env.put("z", (double) z-lz);
					double res=expr.applyAsDouble(venv);
					if(res>0) {
						for(int i=ips.length-1;i>=0;i--) {
							if(res>i) {
								
								BlockEntity blockentity = level.getBlockEntity(posx);
				                  Clearable.tryClear(blockentity);
				                  if (ips[i].place(level, posx, 2)) {
				                     poss.add(posx.immutable());
				                  }
								
								break;
							}
						}
					}else if(replace) {
						level.setBlock(posx,Blocks.AIR.defaultBlockState(), 2);
					}
				}
         for(BlockPos blockpos1 : poss) {
             Block block = level.getBlockState(blockpos1).getBlock();
             level.blockUpdated(blockpos1, block);
          }	
	}
	private static Command<CommandSourceStack> createPlotter(int nval,boolean replace){
		return e->{
			try {
				INumber expr=Expression.of(StringArgumentType.getString(e, "expression"));
				BoundingBox bb=BoundingBox.fromCorners(BlockPosArgument.getBlockPos(e, "pos1"), BlockPosArgument.getBlockPos(e, "pos2"));
				ServerLevel level=e.getSource().getLevel();
				
				BlockInput[] ips=new BlockInput[nval];
				for(int i=0;i<nval;i++) {
					ips[i]=BlockStateArgument.getBlock(e, "block"+i);
				}
				
				
				CVCommands.place(bb, level, expr, ips,replace);
			}catch(Throwable t) {
				t.printStackTrace();
				e.getSource().sendFailure(Utils.string(t.getMessage()));
			}
			e.getSource().sendSuccess(()->Utils.string("Succeed!"), false);
			return Command.SINGLE_SUCCESS;
		};
	}
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent ev) {
		ev.getDispatcher().register(Commands.literal(CVMain.MODID).then(plot(ev.getBuildContext())));
		
	}
}
