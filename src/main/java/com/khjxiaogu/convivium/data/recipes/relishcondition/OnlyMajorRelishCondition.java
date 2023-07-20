package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.network.FriendlyByteBuf;

public class OnlyMajorRelishCondition extends AbstractRelishCondition {

	public OnlyMajorRelishCondition(FriendlyByteBuf buffer) {
		super(buffer);
	}

	public OnlyMajorRelishCondition(JsonObject json) {
		super(json);
	}

	public OnlyMajorRelishCondition(String relish) {
		super(relish);
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		return t.activerelish.size()==1&&t.activerelish.get(0).equals(relish);
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.convivium.relish_cond.only_major",RelishRecipe.getText(super.relish));
	}

}
