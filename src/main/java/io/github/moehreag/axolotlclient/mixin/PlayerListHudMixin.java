package io.github.moehreag.axolotlclient.mixin;

import com.mojang.authlib.GameProfile;
import io.github.moehreag.axolotlclient.Axolotlclient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
	MinecraftClient client = MinecraftClient.getInstance();


	//@Shadow protected abstract Text applyGameModeFormatting(PlayerListEntry entry, MutableText name);

	/*@Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
	public void addBadge(PlayerListEntry entry, CallbackInfoReturnable<Text> cir){

		if (!Util.getGame().toLowerCase().contains("skyblock")) {

			/*if (Axolotlclient.features && Axolotlclient.CONFIG.showBadge && Axolotlclient.isUsingClient(entry.getProfile().getId())) {
				cir.setReturnValue(new LiteralText(Axolotlclient.CONFIG.badgeOptions.CustomBadge ? Axolotlclient.CONFIG.badgeOptions.badgeText + " " : Axolotlclient.badge).setStyle(Style.EMPTY.withFont(Axolotlclient.FONT)).append(entry.getDisplayName() != null ? this.applyGameModeFormatting(entry, (!Objects.equals(Axolotlclient.CONFIG.OwnName, "") ? new LiteralText(Axolotlclient.CONFIG.OwnName).shallowCopy() : entry.getDisplayName().shallowCopy())) : this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(!Objects.equals(Axolotlclient.CONFIG.OwnName, "") ? Axolotlclient.CONFIG.OwnName : entry.getProfile().getName())))));
				cir.cancel();
			}


		}
	}*/
	private int x;
	private int y;
	private GameProfile player;

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Ljava/lang/String;"))
	public PlayerListEntry getPlayer(PlayerListEntry playerEntry){
		this.player = playerEntry.getProfile();
		return playerEntry;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/PlayerListEntry;getGameMode()Lnet/minecraft/world/level/LevelInfo$GameMode;", ordinal = 0))
	public void renderBadge(int width, Scoreboard scoreboard, ScoreboardObjective playerListScoreboardObjective, CallbackInfo ci){
		if(Axolotlclient.features && Axolotlclient.CONFIG.showBadges.get() && Axolotlclient.isUsingClient(player.getId())){
			//boolean bl = this.client.isIntegratedServerRunning() || this.client.getNetworkHandler().getClientConnection().isEncrypted();

			MinecraftClient.getInstance().getTextureManager().bindTexture(Axolotlclient.badgeIcon);
			DrawableHelper.drawTexture(x+10, y,0,0,8,8,8,8);
		}
	}


	/*@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getStringWidth(Ljava/lang/String;)I"))
	public String moveName(String string){
		return "  "+string;
	}*/
	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I"), index = 1)
	public float moveName(float x){
		if(Axolotlclient.features && Axolotlclient.CONFIG.showBadges.get() && Axolotlclient.isUsingClient(player.getId())) {
			return x + 10;
		}
		return x;
	}

	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(IIILnet/minecraft/client/network/PlayerListEntry;)V"))
	public void moveLatencyIcon(Args args){
		if(Axolotlclient.features && Axolotlclient.CONFIG.showBadges.get() && Axolotlclient.isUsingClient(player.getId())) {
			args.set(0,(int)args.get(0)+ 10);
		}

	}

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;fill(IIIII)V"), index = 3)
	public int enlargeBackground(int par1){
		if(Axolotlclient.features && Axolotlclient.CONFIG.showBadges.get() && Axolotlclient.isUsingClient(player.getId()))
			return par1+10;
		return par1;
	}


	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawTexture(IIFFIIIIFF)V"))
	public void getCoords(Args args){
		this.x = args.get(0);
		this.y = args.get(1);
	}
}
