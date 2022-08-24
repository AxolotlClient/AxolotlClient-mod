package io.github.axolotlclient.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatHud.class)
public interface AccessorChatHud {

    @Accessor
    int getScrolledLines();

    @Accessor
    List<ChatHudLine> getMessages();

    @Accessor
    List<ChatHudLine> getVisibleMessages();
}
