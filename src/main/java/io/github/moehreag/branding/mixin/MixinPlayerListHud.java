package io.github.moehreag.branding.mixin;

import io.github.moehreag.branding.Axolotlclient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud {


	@Shadow protected abstract Text applyGameModeFormatting(PlayerListEntry entry, MutableText name);

	@Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
	public void addBadge(PlayerListEntry entry, CallbackInfoReturnable<Text> cir){

		if (!Axolotlclient.TitleDisclaimer && Axolotlclient.CONFIG.showBadge && Axolotlclient.isUsingClient(entry.getProfile().getId())){

		cir.setReturnValue(entry.getDisplayName() != null ? new LiteralText("✵ ").append(applyGameModeFormatting(
			entry,
			entry.getDisplayName().shallowCopy())) : new LiteralText("✵ ").append(this.applyGameModeFormatting(
				entry,
			Team.decorateName(entry.getScoreboardTeam(),
				new LiteralText(entry.getProfile().getName())))));
		cir.cancel();
		}
	}
}
