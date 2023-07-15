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

package io.github.axolotlclient.modules.scrollableTooltips;

import java.util.List;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Mouse;

public class ScrollableTooltips extends AbstractModule {

	private static final ScrollableTooltips instance = new ScrollableTooltips();
	public final BooleanOption enabled = new BooleanOption("enabled", false);
	public final BooleanOption enableShiftHorizontalScroll = new BooleanOption("shiftHorizontalScroll",
		true);
	protected final IntegerOption scrollAmount = new IntegerOption("scrollAmount", 5, 1, 20);
	protected final BooleanOption inverse = new BooleanOption("inverse", false);
	private final BooleanOption alignToBottom = new BooleanOption("alignToBottom", false);
	private final OptionCategory category = new OptionCategory("scrollableTooltips");
	public int tooltipOffsetX;
	public int tooltipOffsetY;

	private boolean alignedToBottom;

	public static ScrollableTooltips getInstance() {
		return instance;
	}

	@Override
	public void init() {
		category.add(enabled);
		category.add(enableShiftHorizontalScroll);
		category.add(scrollAmount);
		category.add(inverse);
		category.add(alignToBottom);

		AxolotlClient.CONFIG.rendering.addSubCategory(category);
	}

	public void onRenderTooltip() {
		if (enabled.get()) {

			int i = Mouse.getDWheel();
			if (i != 0) {
				if (i < 0) {
					onScroll(applyInverse(false));
				}

				if (i > 0) {
					onScroll(applyInverse(true));
				}
			}
		}
	}

	public void onScroll(boolean reverse) {
		if (Screen.hasShiftDown()) {
			if (reverse) {
				tooltipOffsetX -= scrollAmount.get();
			} else {
				tooltipOffsetX += scrollAmount.get();
			}
		} else {
			if (reverse) {
				tooltipOffsetY -= scrollAmount.get();
			} else {
				tooltipOffsetY += scrollAmount.get();
			}
		}
	}

	protected boolean applyInverse(boolean value) {
		if (inverse.get()) {
			return !value;
		}
		return value;
	}

	public void resetScroll() {
		alignedToBottom = false;
		tooltipOffsetY = tooltipOffsetX = 0;
	}

	public void alignToScreenBottom(List<String> tooltip, int y){
		if(alignToBottom.get() && !alignedToBottom) {
			int height = tooltip.size() * 10;

			if(height + y - 4 > Util.getWindow().getHeight()){
				tooltipOffsetY = Util.getWindow().getHeight() - y - height;
			}

			alignedToBottom = true;
		}
	}
}
