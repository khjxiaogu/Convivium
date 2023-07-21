package com.khjxiaogu.convivium.data.recipes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.numbers.Expression;
import com.khjxiaogu.convivium.data.recipes.numbers.INumber;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishCondition;
import com.khjxiaogu.convivium.data.recipes.relishcondition.RelishConditions;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.khjxiaogu.convivium.util.evaluator.IEnvironment;
import com.khjxiaogu.convivium.util.evaluator.VEnvironment;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.data.Writeable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public class SwayRecipe  extends IDataRecipe{


	public SwayRecipe(ResourceLocation id, List<RelishCondition> relish, int priority, Map<String, INumber> locals,
			List<SwayEffect> effects, ResourceLocation icon) {
		super(id);
		this.relish = relish;
		this.priority = priority;
		this.locals = locals;
		this.effects = effects;
		this.icon = icon;
	}

	List<RelishCondition> relish;
	int priority;
	Map<String,INumber> locals;
	List<SwayEffect> effects;
	ResourceLocation icon;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public static RegistryObject<RecipeType<Recipe<?>>> TYPE;
	public static List<SwayRecipe> recipes;
	@Override
	public RecipeSerializer<?> getSerializer() {
		// TODO Auto-generated method stub
		return SERIALIZER.get();
	}
	@Override
	public RecipeType<?> getType() {
		// TODO Auto-generated method stub
		return TYPE.get();
	}
	public SwayRecipe(ResourceLocation id,JsonObject jo) {
		super(id);
		relish=SerializeUtil.parseJsonList(jo.get("relish"), RelishConditions::of);
		priority=GsonHelper.getAsInt(jo, "priority",0);
		locals=new LinkedHashMap<>();
		for(Entry<String, JsonElement> s:jo.getAsJsonObject().entrySet()) {
			locals.put(s.getKey(), Expression.of(s.getValue()));
		}
		effects=SerializeUtil.parseJsonList(jo.get("effects"),SwayEffect::new);
		icon=new ResourceLocation(jo.get("icon").getAsString());
		
	}
	public SwayRecipe(ResourceLocation id,FriendlyByteBuf jo) {
		super(id);
		relish=SerializeUtil.readList(jo, RelishConditions::of);
		priority=jo.readVarInt();
		locals=new LinkedHashMap<>();
		SerializeUtil.readList(jo,b->Pair.of(b.readUtf(),Expression.of(b))).forEach(d->locals.put(d.getFirst(), d.getSecond()));
		effects=SerializeUtil.readList(jo,SwayEffect::new);
		icon=jo.readResourceLocation();
		
	}
	@Override
	public void serializeRecipeData(JsonObject json) {
		// TODO Auto-generated method stub
		json.add("relish", SerializeUtil.toJsonList(relish, RelishCondition::serialize));
		json.addProperty("priority", priority);
		JsonObject jo=new JsonObject();
		for(Entry<String, INumber> p:locals.entrySet()) {
			jo.add(p.getKey(), p.getValue().serialize());
		}
		json.add("locals",jo);
		json.add("effects", SerializeUtil.toJsonList(effects, Writeable::serialize));
		json.addProperty("icon", icon.toString());
	}
	
	public void write(FriendlyByteBuf pb) {
		SerializeUtil.writeList(pb, relish, RelishConditions::write);
		pb.writeVarInt(priority);
		SerializeUtil.writeList(pb, locals.entrySet(),(t,b)->{b.writeUtf(t.getKey());t.getValue().write(b);});
		SerializeUtil.writeList(pb,effects,Writeable::write);
		pb.writeResourceLocation(icon);
	}
	public IEnvironment createChildEnv(IEnvironment par) {
		return new VEnvironment(par,locals);
	}
	public boolean canApply(BeveragePendingContext ctx) {
		return relish.stream().anyMatch(t->t.test(ctx));
	}
	public List<MobEffectInstance> getEffects(IEnvironment env){
		return effects.stream().map(t->t.getEffect(env)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}
}
