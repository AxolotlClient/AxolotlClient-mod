/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.modules.screenshotUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.APIError;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.util.BufferUtil;

public abstract class ImageNetworking {

	public abstract void uploadImage(File file);

	protected CompletableFuture<String> upload(File file) {
		try {
			return upload(file.getName(), Files.readAllBytes(file.toPath()));
		} catch (IOException e){
			return CompletableFuture.completedFuture("");
		}
	}

	protected CompletableFuture<String> upload(String name, byte[] data){
		return API.getInstance().send(new Request(Request.Type.UPLOAD_SCREENSHOT,
			new Request.Data().add(name.length()).add(name).add(data))).handle((buf, throwable) -> {
				if(throwable != null){
					APIError.display(throwable);
					return "";
				} else {
					return BufferUtil.getString(buf, 0x09, buf.readableBytes() - 0x09);
				}
		});
	}
}
