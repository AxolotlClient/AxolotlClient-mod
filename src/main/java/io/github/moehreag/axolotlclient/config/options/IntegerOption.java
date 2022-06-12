package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.option.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class IntegerOption extends OptionBase implements Option{

    private int option;
    private final int Default;
    private final int min;
    private final int max;

    public IntegerOption(String name, int Default, int min, int max) {
        super(name);
        this.Default=Default;
        this.min=min;
        this.max=max;
    }

    public int get(){
        return option;
    }

    public void set(int set){
        option=set;
    }

    public int getMin(){return min;}
    public int getMax(){return max;}

    @Override
    public OptionType getType() {
        return OptionType.INT;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setDefaults(){
        option = Default;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsInt();
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(option);
    }

	@Override
	public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
		return new SliderWidget(x, y, width, 20, getTranslatedName(), option) {
			@Override
			protected void updateMessage() {
				setMessage(getTranslatedName().copy().append(Text.of(": "+ option)));
			}

			@Override
			protected void applyValue() {
				option = (int) value;
			}
		};
	}
}
