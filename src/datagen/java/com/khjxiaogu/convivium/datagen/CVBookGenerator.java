/*
 * Copyright (c) 2024 IEEM Trivium Society/khjxiaogu
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.convivium.CVMain;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.datagen.JsonGenerator;
import com.teammoeg.caupona.datagen.JsonStorage;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class CVBookGenerator extends JsonGenerator {
	private Map<String, JsonObject> langs = new HashMap<>();

	class DatagenTranslationProvider implements TranslationProvider {
		String lang;

		public DatagenTranslationProvider(String lang) {
			super();
			this.lang = lang;
		}

		@Override
		public String getTranslation(String key, Object... objects) {
			if (langs.get(lang).has(key))
				return String.format(langs.get(lang).get(key).getAsString(), objects);
			return Utils.translate(key, objects).getString();
		}

		@Override
		public String getTranslationOrElse(String key, String candidate, Object... objects) {
			if (langs.get(lang).has(key))
				return String.format(langs.get(lang).get(key).getAsString(), objects);
			return candidate;
		}

	}

	public CVBookGenerator(PackOutput output, ExistingFileHelper helper) {
		super(PackType.SERVER_DATA, output, helper, "Convivium Patchouli");
	}

	String[] allangs = { "zh_cn", "en_us" };

	private void loadLang(String locale) {
		try {
			Resource rc = helper.getResource(ResourceLocation.fromNamespaceAndPath(CVMain.MODID, "lang/" + locale + ".json"),
				PackType.CLIENT_RESOURCES);
			JsonObject jo = JsonParser.parseReader(new InputStreamReader(rc.open(), "UTF-8")).getAsJsonObject();
			langs.put(locale, jo);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void gather(JsonStorage reciver) {

		for (String lang : allangs)
			loadLang(lang);

	}
}
