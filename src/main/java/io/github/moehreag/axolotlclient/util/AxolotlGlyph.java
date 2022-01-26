package io.github.moehreag.axolotlclient.util;

import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

import java.io.IOException;

public enum AxolotlGlyph implements RenderableGlyph {
	INSTANCE;
	public static NativeImage icon;

	static {
		try {
			icon = NativeImage.read("assets/axolotlclient/icon.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final NativeImage IMAGE = Util.make(new NativeImage(NativeImage.Format.RGBA, 8, 8, false), (image) -> {

		image.copyFrom(icon);

		image.untrack();
	});

	@Override
	public int getWidth() {
		return 8;
	}

	@Override
	public int getHeight() {
		return 8;
	}

	@Override
	public void upload(int x, int y) {
		IMAGE.upload(0, x, y, false);
	}

	@Override
	public boolean hasColor() {
		return true;
	}

	@Override
	public float getOversample() {
		return 6.0F;
	}

	@Override
	public float getAdvance() {
		return 1.0F;
	}
}
