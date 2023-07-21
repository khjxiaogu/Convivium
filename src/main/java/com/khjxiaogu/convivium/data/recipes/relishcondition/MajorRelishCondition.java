package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.network.FriendlyByteBuf;

public class MajorRelishCondition extends AbstractRelishCondition {

	public MajorRelishCondition(FriendlyByteBuf buffer) {
		super(buffer);
	}

	public MajorRelishCondition(JsonObject json) {
		super(json);
	}

	public MajorRelishCondition(String relish) {
		super(relish);
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		return t.activerelish.contains(relish);
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.convivium.relish_cond.major",RelishRecipe.recipes.get(relish).getText());
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "major";
	}

}
