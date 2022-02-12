package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud {


	@Shadow protected abstract Text applyGameModeFormatting(PlayerListEntry entry, MutableText name);

	@Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
	public void addBadge(PlayerListEntry entry, CallbackInfoReturnable<Text> cir){

		if (!Util.getGame().toLowerCase().contains("skyblock")) {

			if (Axolotlclient.features && Axolotlclient.CONFIG.showBadge && Axolotlclient.isUsingClient(entry.getProfile().getId())) {

				assert MinecraftClient.getInstance().player != null;

				//System.out.println(entry.getProfile().getId());

				if(Objects.equals(entry.getProfile().getName(), MinecraftClient.getInstance().player.getName().asString())){
					cir.setReturnValue(new LiteralText(Axolotlclient.CONFIG.badgeOptions.CustomBadge ?
						Axolotlclient.CONFIG.badgeOptions.badgeText + " " :
						Axolotlclient.badge).setStyle(Style.EMPTY.withFont(Axolotlclient.FONT)).append(
						entry.getDisplayName() != null ?
							this.applyGameModeFormatting(entry, (Axolotlclient.CONFIG.hideOwnName ?
								new LiteralText(Axolotlclient.CONFIG.OwnName).shallowCopy() :
								entry.getDisplayName().shallowCopy())) :
							this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(Axolotlclient.CONFIG.hideOwnName ?
								Axolotlclient.CONFIG.OwnName :
								entry.getProfile().getName())))));
				} else {

					cir.setReturnValue(new LiteralText(Axolotlclient.CONFIG.badgeOptions.CustomBadge ?
						Axolotlclient.CONFIG.badgeOptions.badgeText + " " :
						Axolotlclient.badge).setStyle(Style.EMPTY.withFont(Axolotlclient.FONT)).append(
						entry.getDisplayName() != null ?
							this.applyGameModeFormatting(entry, (Axolotlclient.CONFIG.hideOtherNames ?
								new LiteralText(Axolotlclient.CONFIG.otherName).shallowCopy() :
								entry.getDisplayName().shallowCopy())) :
							this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(Axolotlclient.CONFIG.hideOtherNames ?
								Axolotlclient.CONFIG.otherName :
								entry.getProfile().getName())))));
				}
				cir.cancel();
			}
		}
	}
}
