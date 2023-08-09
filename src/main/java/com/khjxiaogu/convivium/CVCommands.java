package com.khjxiaogu.convivium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber
public class CVCommands {
	public static LiteralArgumentBuilder<CommandSourceStack> plot(CommandBuildContext ctx) {
		return Commands.literal("plot").then(
				Commands.argument("expression", StringArgumentType.string()).then(
					Commands.argument("pos1", BlockPosArgument.blockPos()).then(
							createSub(0,ctx,Commands.argument("pos2", BlockPosArgument.blockPos()))
							)
						)
				);
	}
	private static ArgumentBuilder<CommandSourceStack, ?> createSub(int last,CommandBuildContext ctx,ArgumentBuilder<CommandSourceStack, ?> par){
		if(last>=16)
			return par;
		RequiredArgumentBuilder<CommandSourceStack, ?> child=Commands.argument("block"+last,BlockStateArgument.block(ctx))
				.requires(e->e.hasPermission(3))
				.executes(createPlotter(last+1));
		par.then(createSub(last+1,ctx,child)); 
		return par;
	}
	private static Command<CommandSourceStack> createPlotter(int nval){
		return e->{
			try {
				INumber expr=Expression.of(StringArgumentType.getString(e, "expression"));
				BlockPos p1=BlockPosArgument.getBlockPos(e, "pos1");
				BlockPos p2=BlockPosArgument.getBlockPos(e, "pos2");
				int hx=Math.max(p1.getX(), p2.getX());
				int lx=Math.min(p1.getX(), p2.getX());
				int hy=Math.max(p1.getY(), p2.getY());
				int ly=Math.min(p1.getY(), p2.getY());
				int hz=Math.max(p1.getZ(), p2.getZ());
				int lz=Math.min(p1.getZ(), p2.getZ());
				Map<String,Double> env=new HashMap<>();
				ConstantEnvironment venv=new ConstantEnvironment(env);
				BlockPos.MutableBlockPos posx=new MutableBlockPos();
				BlockInput[] ips=new BlockInput[nval];
				for(int i=0;i<nval;i++) {
					ips[i]=BlockStateArgument.getBlock(e, "block"+i);
				}
				List<BlockPos> poss=new ArrayList<>();
				ServerLevel level=e.getSource().getLevel();
				for(int x=lx;x<=hx;x++)
					for(int y=ly;y<=hy;y++)
						for(int z=lz;z<hz;z++) {
							posx.set(x, y, z);
							env.put("x", (double) x-lx);
							env.put("y", (double) y-ly);
							env.put("z", (double) z-lz);
							double res=expr.applyAsDouble(venv);
							if(res>0) {
								for(int i=nval-1;i>=0;i--) {
									if(res>i) {
										
										BlockEntity blockentity = level.getBlockEntity(posx);
						                  Clearable.tryClear(blockentity);
						                  if (ips[i].place(level, posx, 2)) {
						                     poss.add(posx.immutable());
						                  }
										
										break;
									}
								}
							}
						}
		         for(BlockPos blockpos1 : poss) {
		             Block block = level.getBlockState(blockpos1).getBlock();
		             level.blockUpdated(blockpos1, block);
		          }	
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
