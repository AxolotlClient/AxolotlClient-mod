package io.github.axolotlclient.mixin;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin extends DrawableHelper {

	MinecraftClient client = MinecraftClient.getInstance();
	private PlayerListEntry playerListEntry;

	@Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
	public void nickHider(PlayerListEntry playerEntry, CallbackInfoReturnable<String> cir){
		if(playerEntry.getProfile().getId()==MinecraftClient.getInstance().player.getUuid() &&
				NickHider.Instance.hideOwnName.get()){
			cir.setReturnValue(NickHider.Instance.hiddenNameSelf.get());
		} else if(playerEntry.getProfile().getId()!=MinecraftClient.getInstance().player.getUuid() &&
			NickHider.Instance.hideOtherNames.get()){
			cir.setReturnValue(NickHider.Instance.hiddenNameOthers.get());
		}
	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Ljava/lang/String;"))
	public PlayerListEntry getPlayer(PlayerListEntry playerEntry){
		playerListEntry = playerEntry;
		return playerEntry;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getStringWidth(Ljava/lang/String;)I", ordinal = 0))
	public int moveName(TextRenderer instance, String text){
		if(AxolotlClient.isUsingClient(playerListEntry.getProfile().getId())) return instance.getStringWidth(text)+10;
		return instance.getStringWidth(text);
	}

	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I", ordinal = 1))
	public void getCoords(Args args){
		float x = args.get(1);
		float y = args.get(2);
		if(AxolotlClient.isUsingClient(playerListEntry.getProfile().getId())) {
			client.getTextureManager().bindTexture(AxolotlClient.badgeIcon);
			DrawableHelper.drawTexture((int) x, (int) y, 0, 0,  8, 8, 8, 8);
			args.set(1, x+10);
		}

	}

	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I", ordinal = 2))
	public void getCoords2(Args args){
		float x=args.get(1);
		float y=args.get(2);
		if(AxolotlClient.isUsingClient(playerListEntry.getProfile().getId())) {
			client.getTextureManager().bindTexture(AxolotlClient.badgeIcon);
			DrawableHelper.drawTexture((int) x, (int) y, 0, 0,  8, 8, 8, 8);
			args.set(1, x+10);
		}

	}
}
