package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.ToggleSprintHud;
import io.github.axolotlclient.util.Util;
import io.github.axolotlclient.util.clientCommands.ClientCommands;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    /**
     * @author DragonEggBedrockBreaking
     * @license MPL-2.0
     * @param sprintKey the sprint key that the user has bound
     * @return whether or not the user should try to sprint
     */
    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/options/KeyBinding;isPressed()Z"
            )
    )
    private boolean alwaysPressed(KeyBinding sprintKey) {
        ToggleSprintHud hud = (ToggleSprintHud) HudManager.getINSTANCE().get(ToggleSprintHud.ID);
        return hud.sprintToggled.get() || sprintKey.isPressed();
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void clientCommands(String string, CallbackInfo ci){
        if(string.startsWith("/")) {
            String commandName = string.substring(1);
            ClientCommands.getInstance().getCommands().forEach((str, command) -> {

                if(command.getCommandName().equals(commandName.split(" ")[0])){
                    String[] args = Util.copyArrayWithoutFirstEntry(commandName.split(" "));
                    command.execute(args.length > 1? args:new String[]{""});
                    ci.cancel();
                }

            });
        }
    }
}
