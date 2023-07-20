package com.khjxiaogu.convivium.data.recipes.relishcondition;

import com.google.gson.JsonObject;
import com.khjxiaogu.convivium.data.recipes.RelishRecipe;
import com.khjxiaogu.convivium.data.recipes.compare.Compare;
import com.khjxiaogu.convivium.data.recipes.compare.Compares;
import com.khjxiaogu.convivium.util.BeveragePendingContext;
import com.teammoeg.caupona.data.TranslationProvider;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class RelishGTCondition extends AbstractRelishCondition {
	Compare comp;
	public RelishGTCondition(FriendlyByteBuf buffer) {
		super(buffer);
		comp=Compares.of(buffer);
	}

	public RelishGTCondition(JsonObject json) {
		super(json);
		comp=Compares.of(json);
	}

	public RelishGTCondition(String relish) {
		super(relish);
	}

	@Override
	public boolean test(BeveragePendingContext t) {
		return t.relishes.getOrDefault(relish,0)>num;
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.convivium.relish_cond.greater",RelishRecipe.getText(super.relish));
	}

}
