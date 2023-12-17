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

package io.github.axolotlclient.api.requests;

import java.lang.ref.WeakReference;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.APIError;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.types.GlobalData;
import io.github.axolotlclient.api.util.BufferUtil;

public class GlobalDataRequest {

	private static WeakReference<GlobalData> cachedData = new WeakReference<>(null);

	public static GlobalData get(){
		if (cachedData.get() != null){
			return cachedData.get();
		}
		return (cachedData = new WeakReference<>(API.getInstance().send(new Request(Request.Type.GLOBAL_DATA)).handleAsync((buf, th) -> {
					if(th != null){
						APIError.display(th);
						return GlobalData.EMPTY;
					}
					return BufferUtil.unwrap(buf, GlobalData.class);
				}).getNow(GlobalData.EMPTY))).get();
	}
}
