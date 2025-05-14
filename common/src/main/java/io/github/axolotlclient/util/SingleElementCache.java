/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.util;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class SingleElementCache<K, V> {
	private final Function<K, @UnknownNullability V> op;

	@Nullable
	private K key;

	@UnknownNullability
	private V value;

	public SingleElementCache(Function<K, @UnknownNullability V> op) {
		this.op = op;
	}

	public V get(@NotNull K key) {
		if (!Objects.equals(this.key, key)) {
			invalidate();
		}

		if (this.key == null) {
			value = op.apply(key);
		}

		return value;
	}

	public void invalidate() {
		key = null;
	}
}
