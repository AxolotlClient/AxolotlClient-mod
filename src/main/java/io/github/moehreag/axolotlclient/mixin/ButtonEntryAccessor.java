package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ButtonListWidget.ButtonEntry.class)
public interface ButtonEntryAccessor {
	@Accessor
	List<ClickableWidget> getButtons();

	@Mutable
	@Accessor
	void setButtons(List<ClickableWidget> buttons);
}
