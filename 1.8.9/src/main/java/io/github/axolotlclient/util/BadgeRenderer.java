package io.github.axolotlclient.util;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class BadgeRenderer {
	public static void renderNametagBadge(Entity entity) {
		if (entity instanceof PlayerEntity && !entity.isSneaking()) {
			if (AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(entity.getUuid())) {
				GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.enableDepthTest();
				GlStateManager.enableAlphaTest();
				MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);

				int x = -(MinecraftClient.getInstance().textRenderer
					.getStringWidth(entity.getUuid() == MinecraftClient.getInstance().player.getUuid()
						? (NickHider.getInstance().hideOwnName.get() ? NickHider.getInstance().hiddenNameSelf.get()
						: entity.getName().asFormattedString())
						: (NickHider.getInstance().hideOtherNames.get() ? NickHider.getInstance().hiddenNameOthers.get()
						: entity.getName().asFormattedString()))
					/ 2
					+ (AxolotlClient.CONFIG.customBadge.get() ? MinecraftClient.getInstance().textRenderer
					.getStringWidth(" " + AxolotlClient.CONFIG.badgeText.get()) : 10));

				GlStateManager.color(1, 1, 1, 1);

				if (AxolotlClient.CONFIG.customBadge.get())
					MinecraftClient.getInstance().textRenderer.draw(AxolotlClient.CONFIG.badgeText.get(), x, 0, -1,
						AxolotlClient.CONFIG.useShadows.get());
				else
					DrawableHelper.drawTexture(x, 0, 0, 0, 8, 8, 8, 8);
			}
		}
	}
}
