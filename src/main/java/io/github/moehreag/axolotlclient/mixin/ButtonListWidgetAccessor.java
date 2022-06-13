package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(EntryListWidget.class)
public interface ButtonListWidgetAccessor {


	@Accessor
	<E extends EntryListWidget.Entry<E>>
	void setHoveredEntry(E hoveredEntry);
}
