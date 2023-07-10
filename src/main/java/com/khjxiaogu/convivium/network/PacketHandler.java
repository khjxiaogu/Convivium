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

package com.khjxiaogu.convivium.network;

import com.khjxiaogu.convivium.CVMain;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String VERSION = Integer.toString(1);
	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(CVMain.MODID, "network"), () -> VERSION, VERSION::equals, VERSION::equals);

	public static void send(PacketDistributor.PacketTarget target, Object message) {
		CHANNEL.send(target, message);
	}

	public static void sendToServer(Object message) {
		CHANNEL.sendToServer(message);
	}

	public static SimpleChannel get() {
		return CHANNEL;
	}

	public static void register() {
		int id = 0;
		//CHANNEL.registerMessage(id++, ClientDataMessage.class, ClientDataMessage::encode, ClientDataMessage::new,
		//		ClientDataMessage::handle);
	}
}