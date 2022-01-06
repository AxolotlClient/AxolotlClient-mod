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

	@Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
	public void addIcon(PlayerListEntry entry, CallbackInfoReturnable<Text> cir){

		Text name;

		if (entry.getDisplayName() != null) {
			name = applyGameModeFormatting(entry, entry.getDisplayName().shallowCopy());
		} else {
			name = applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));
		}
		if (Axolotlclient.CONFIG.showBadge && Axolotlclient.isUsingClient(entry.getProfile().getId())){
			cir.setReturnValue(new LiteralText("✵ "+ name.asString()));
			//System.out.println(name.asString());
		}
		else {cir.setReturnValue(name);}
		cir.cancel();
	}
}
