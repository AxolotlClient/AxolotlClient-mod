package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class FloatOption extends OptionBase implements Option{

    float min;
    float max;
    float def;
    float option;

    public FloatOption(String name, Float min, Float max, Float def) {
        super(name);
        this.min=min;
        this.max=max;
        this.def=def;
    }

    public float get(){
        return option;
    }

    public void set(float set){
        option=set;
    }

    public float getMin(){return min;}
    public float getMax(){return max;}

    @Override
    public OptionType getType() {
        return OptionType.FLOAT;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        option = element.getAsFloat();
    }

    @Override
    public void setDefaults() {
        option=def;
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
				value=option;
			}
		};
	}
}
