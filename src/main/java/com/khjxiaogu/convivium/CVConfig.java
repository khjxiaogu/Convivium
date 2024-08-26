/*
 * Copyright (c) 2023 IEEM Trivium Society/khjxiaogu
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

package com.khjxiaogu.convivium;

import java.util.ArrayList;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class CVConfig {

	public static void register() {
		// ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,
		// Config.CLIENT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CVConfig.COMMON_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CVConfig.SERVER_CONFIG);
	}

	public static class Client {
		/**
		 * @param builder
		 */
		Client(ModConfigSpec.Builder builder) {
		}
	}

	public static class Common {

		/**
		 * @param builder
		 */
		public BooleanValue canPlacePotion;
		Common(ModConfigSpec.Builder builder) {
			canPlacePotion=builder.comment("Set vanilla potion placable as beverage block").define("canPlacePotion", true);
		}
	}

	public static class Server {
		public IntValue kineticRange;
		public IntValue kineticValidation;
		Server(ModConfigSpec.Builder builder) {
			builder.push("kinetics");
			kineticRange=builder.comment("Kinetic Transfer radius for aeolipile").defineInRange("kineticTransferRange",4, 1, Integer.MAX_VALUE);
			kineticValidation=builder.comment("Kinetic validation ticks").defineInRange("kineticValidationTicks",20, 1, Integer.MAX_VALUE);
			builder.pop();
		}
	}

	// public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final ModConfigSpec COMMON_CONFIG;
	public static final ModConfigSpec SERVER_CONFIG;
	// public static final Client CLIENT;
	public static final Common COMMON;
	public static final Server SERVER;

	public static ArrayList<String> DEFAULT_WHITELIST = new ArrayList<>();

	static {
		// ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
		// CLIENT = new Client(CLIENT_BUILDER);
		// CLIENT_CONFIG = CLIENT_BUILDER.build();
		ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
		COMMON = new Common(COMMON_BUILDER);
		COMMON_CONFIG = COMMON_BUILDER.build();
		ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
		SERVER = new Server(SERVER_BUILDER);
		SERVER_CONFIG = SERVER_BUILDER.build();
	}
}
