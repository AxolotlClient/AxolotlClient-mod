package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.Util;
import io.github.axolotlclient.util.clientCommands.ClientCommands;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    // Removing text limit is useless because the packet is limited to 100 chars

    @Shadow private List<String> suggestions;

    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "setSuggestions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getText()Ljava/lang/String;"))
    public void addClientCommands(String[] smth32543, CallbackInfo ci){
        String command = this.chatField.getText().substring(0, this.chatField.getCursor());
        if(command.startsWith("/")) {
            if(command.length()==1) {

                for (String cmd : ClientCommands.getInstance().getCommands().keySet()) {
                    if (cmd.length() > 0) {
                        suggestions.add("/" + cmd);
                    }
                }
            } else {

                command = command.substring(1);
                if(command.contains(" ")) {
                    for (String string : ClientCommands.getInstance().getCommands().keySet()) {
                        if (string.contains(command.split(" ")[0])) {
                            suggestions.addAll(ClientCommands.getInstance().
                                    getCommands().get(string).
                                    getSuggestions(Util.copyArrayWithoutFirstEntry(command.trim().split(" "))));
                        }
                    }
                } else {
                    for (String string : ClientCommands.getInstance().getCommands().keySet()) {
                        if (string.toLowerCase(Locale.ROOT).startsWith(command.toLowerCase(Locale.ROOT))) {
                            suggestions.add("/"+string);
                        }
                    }
                }
            }
        }
    }

    @Redirect(method = "setSuggestions", at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils;getCommonPrefix([Ljava/lang/String;)Ljava/lang/String;"), remap = false)
    public String allowCompletion(String[] strs){
        return StringUtils.getCommonPrefix(suggestions.toArray(new String[0]));
    }
}


