package io.github.axolotlclient.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

	private GameProfile cachedPlayer;


	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/PlayerListEntry;getProfile()Lcom/mojang/authlib/GameProfile;"))
	public GameProfile getPlayerGameProfile(PlayerListEntry instance){
		cachedPlayer = instance.getProfile();
		return instance.getProfile();
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
	public int moveName(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color){
		if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(cachedPlayer.getId())){

			RenderSystem.setShaderTexture(0, AxolotlClient.badgeIcon);
			RenderSystem.setShaderColor(1,1,1,1);

			DrawableHelper.drawTexture(matrices, (int) x, (int) y, 8, 8, 0, 0, 8, 8, 8, 8);

			x+=9;
		}
		cachedPlayer=null;
		return instance.drawWithShadow(matrices, text, x, y, color);
	}

	@ModifyArg(method = "getPlayerName", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;applyGameModeFormatting(Lnet/minecraft/client/network/PlayerListEntry;Lnet/minecraft/text/MutableText;)Lnet/minecraft/text/Text;"), index = 1)
	public MutableText hideNames(MutableText name){
		if(NickHider.Instance.hideOwnName.get()){
			return Text.literal(NickHider.Instance.hiddenNameSelf.get());
		}
		if(NickHider.Instance.hideOtherNames.get()){
			return Text.literal(NickHider.Instance.hiddenNameOthers.get());
		}
		return name;
	}
}
