package io.github.moehreag.axolotlclient.config.screen;

import io.github.moehreag.axolotlclient.config.ConfigManager;
import io.github.moehreag.axolotlclient.config.options.*;
import io.github.moehreag.axolotlclient.config.screen.widgets.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class OptionScreenBuilder extends Screen {

    private final List<OptionWidget> optionWidgets = new ArrayList<>();
    private ButtonWidget back;
    private CustomButtonWidget dialog;
    private final OptionCategory cat;

    private final Screen parent;

    public OptionScreenBuilder(Screen parent, OptionCategory category){
        this(parent, category, null);
    }

    public OptionScreenBuilder(Screen parent, OptionCategory category, CustomButtonWidget dialog){
        this.parent=parent;
        this.cat = category;
        this.dialog=dialog;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        if(this.client.world!=null)fillGradient(0,0, width, height, new Color(0xB0100E0E, true).hashCode(), new Color(0x46212020, true).hashCode());
        else renderDirtBackground(0);

        drawCenteredString(this.textRenderer, cat.getTranslatedName(), width/2, height/4, -1);

        optionWidgets.forEach(optionWidget -> optionWidget.render(MinecraftClient.getInstance(), mouseX, mouseY));
        if(dialog!=null){
            dialog.render(client, mouseX, mouseY);
            if(dialog.textFieldWidget!=null)dialog.textFieldWidget.setFocused(true);
            if(dialog.sliderWidget!=null){
                dialog.sliderWidget.render(client, mouseX,mouseY);
            }
        }

        this.buttons.forEach(buttonWidget -> buttonWidget.render(client, mouseX, mouseY));

        back.render(client, mouseX, mouseY);
    }

    @Override
    public void init() {
        super.init();
        int lines=1;
        boolean right=false;
        this.optionWidgets.clear();
        if(cat.getOptions().size()+cat.getSubCategories().size()<=12) {
            for (Option option : cat.getOptions()) {
                if (!Objects.equals(option.getName(), "x") && !Objects.equals(option.getName(), "y")) {
                    this.optionWidgets.add(new OptionWidget(option, this.width / 2 - (right ? -50 : 200), lines, this.height, optionWidget -> this.dialog = optionWidget.getDialog()));
                    if (right) lines++;
                    right = !right;
                }
            }
            for (OptionCategory category : cat.getSubCategories()) {
                this.buttons.add(new CategoryWidget(category, this.width / 2 - (right ? -50 : 200), lines, 150, this.height));
                if (right) lines++;
                right = !right;
            }
        } else {
            int row=1;
            for (Option option : cat.getOptions()) {
                if (!Objects.equals(option.getName(), "x") && !Objects.equals(option.getName(), "y")) {
                    this.optionWidgets.add(new OptionWidget(option, this.width / 2 - (row==3 ? -100 : (row==2?50:200)), lines, 100, this.height, optionWidget -> this.dialog = optionWidget.getDialog()));
                    if (row==3) {row=0;lines++;}
                    row++;
                }
            }
            for (OptionCategory category : cat.getSubCategories()) {
                this.buttons.add(new CategoryWidget(category, this.width / 2 - (row==3 ? -100 : (row==2?50:200)), lines,100, this.height));
                if (row==3) {row=0;lines++;}
                row++;
            }
        }

        back = new ButtonWidget(0, this.width/2-75, (this.height/6) *5, 150, 20, I18n.translate("back")){
            @Override
            public void render(MinecraftClient client, int mouseX, int mouseY) {
                client.getTextureManager().bindTexture(new Identifier("axolotlclient", "textures/gui/button1.png"));
                drawTexture(x, y, 0, 0,width, height, width, height);
                drawCenteredString(client.textRenderer, message, x+width/2, y + (height - 8) / 2, -1);
            }
        };
        if(dialog!=null) {
            if (dialog.textFieldWidget != null)
                optionWidgets.forEach(optionWidget -> {
                    if (dialog.x - 2 == optionWidget.getX() + optionWidget.getWidth() && dialog.y == optionWidget.getY())
                        dialog.textFieldWidget.write(((StringOption) optionWidget.getOption()).get());
                });
            if(dialog.sliderWidget != null){
                dialog.sliderWidget.focused=true;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(dialog!=null && dialog.textFieldWidget!=null)dialog.textFieldWidget.tick();
        else if(dialog instanceof ColorSelectorWidget)((ColorSelectorWidget) dialog).tick();
    }

    @Override
    public void removed() {
        super.removed();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        AtomicBoolean onButton= new AtomicBoolean(false);
        if(button==0) {
            optionWidgets.forEach(optionWidget -> {
                if (optionWidget.isHovered(mouseX, mouseY)){ CustomWidget.onClick.onClick(optionWidget); onButton.set(true);}
            });
            if(dialog!=null && dialog.isMouseOver(client, mouseX, mouseY)) {
                dialog.playDownSound(MinecraftClient.getInstance().getSoundManager());
                optionWidgets.forEach(optionWidget -> {
                    optionWidget.playDownSound();
                    if(dialog.x-2 == optionWidget.getX() + optionWidget.getWidth() && dialog.y == optionWidget.getY()){

                        if(optionWidget.getOption() instanceof BooleanOption){
                            ((BooleanOption) optionWidget.getOption()).toggle();
                            dialog.setText(I18n.translate("options."+(((BooleanOption) optionWidget.getOption()).get()?"on":"off")));
                            //client.openScreen(new OptionScreenBuilder(parent, cat, dialog));
                        } else if(optionWidget.getOption() instanceof StringOption){
                            dialog.textFieldWidget.mouseClicked(mouseX, mouseY, 0);
                            dialog.textFieldWidget.setFocused(true);
                        } else if(optionWidget.getOption() instanceof FloatOption){
                            dialog.sliderWidget.setFocused(true);
                        } else if(optionWidget.getOption() instanceof DoubleOption){
                            dialog.sliderWidget.setFocused(true);
                        } else if(optionWidget.getOption() instanceof IntegerOption){
                            dialog.sliderWidget.setFocused(true);
                        } else if(optionWidget.getOption() instanceof EnumOption){
                            dialog.setText(((EnumOption) optionWidget.getOption()).next().toString());
                        } else if(optionWidget.getOption() instanceof ColorOption){
                            ((ColorSelectorWidget)dialog).onClick(mouseX, mouseY);
                        }
                    }
                });
            }
            if((dialog!=null && !dialog.isMouseOver(client, mouseX, mouseY)) && !onButton.get()){
                optionWidgets.forEach(optionWidget -> {
                    if (this.dialog != null) {
                        if (dialog.x - 2 == optionWidget.getX() + optionWidget.getWidth() && dialog.y == optionWidget.getY()) {
                            if (this.dialog.textFieldWidget != null && optionWidget.getOption() instanceof StringOption) {
                                ((StringOption) optionWidget.getOption()).set(dialog.textFieldWidget.getText());
                            } else if (this.dialog.sliderWidget != null && optionWidget.getOption() instanceof FloatOption) {
                                ((FloatOption) optionWidget.getOption()).set(dialog.sliderWidget.getSliderValue());
                            } else if (this.dialog.sliderWidget != null && optionWidget.getOption() instanceof DoubleOption) {
                                ((DoubleOption) optionWidget.getOption()).set(dialog.sliderWidget.getSliderValue());
                            } else if (this.dialog.sliderWidget != null && optionWidget.getOption() instanceof IntegerOption) {
                                ((IntegerOption) optionWidget.getOption()).set(dialog.sliderWidget.getSliderValueAsInt());
                            } else if(this.dialog.textFieldWidget !=null && optionWidget.getOption() instanceof ColorOption){
                                ((ColorOption)optionWidget.getOption()).set(io.github.moehreag.axolotlclient.config.Color.parse(dialog.textFieldWidget.getText()));
                            }
                        }
                    }
                });
                this.dialog = null;
                ConfigManager.save();
            }

            this.buttons.forEach(buttonWidget -> {
                if(buttonWidget.isMouseOver(client, mouseX, mouseY) && buttonWidget instanceof CategoryWidget){
                    buttonWidget.playDownSound(client.getSoundManager());
                    this.client.openScreen(new OptionScreenBuilder(this, ((CategoryWidget) buttonWidget).category));
                }
            });

            if(back.isMouseOver(client, mouseX, mouseY)){
                back.playDownSound(client.getSoundManager());

                optionWidgets.forEach(optionWidget -> {
                    if (this.dialog != null) {
                        if (dialog.x - 2 == optionWidget.getX() + optionWidget.getWidth() && dialog.y == optionWidget.getY()) {
                            if (this.dialog.textFieldWidget != null && optionWidget.getOption() instanceof StringOption) {
                                ((StringOption) optionWidget.getOption()).set(dialog.textFieldWidget.getText());
                            } else if (this.dialog.sliderWidget != null && optionWidget.getOption() instanceof FloatOption) {
                                ((FloatOption) optionWidget.getOption()).set(dialog.sliderWidget.getSliderValue());
                            } else if (this.dialog.sliderWidget != null && optionWidget.getOption() instanceof DoubleOption) {
                                ((DoubleOption) optionWidget.getOption()).set(dialog.sliderWidget.getSliderValue());
                            } else if (this.dialog.sliderWidget != null && optionWidget.getOption() instanceof IntegerOption) {
                                ((IntegerOption) optionWidget.getOption()).set(dialog.sliderWidget.getSliderValueAsInt());
                            } else if(this.dialog.textFieldWidget !=null && optionWidget.getOption() instanceof ColorOption){
                                ((ColorOption)optionWidget.getOption()).set(io.github.moehreag.axolotlclient.config.Color.parse(dialog.textFieldWidget.getText()));
                            }
                        }
                    }
                });
                ConfigManager.save();
                client.openScreen(parent);
            }
        }
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        this.dialog=null;
        this.width=width;
        this.height=height;
        init();
    }

    @Override
    protected void keyPressed(char character, int code) {
        super.keyPressed(character, code);
        if(dialog!=null) {
            if (dialog.textFieldWidget != null)
                dialog.textFieldWidget.keyPressed(character, code);
            else if(dialog instanceof ColorSelectorWidget){
                ((ColorSelectorWidget) dialog).keyPressed(character, code);
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        optionWidgets.forEach(optionWidget -> {
            if(optionWidget.getOption() instanceof FloatOption || optionWidget.getOption() instanceof IntegerOption || optionWidget.getOption() instanceof DoubleOption){
                if(dialog!=null && dialog.sliderWidget != null)
                dialog.sliderWidget.mouseReleased(mouseX, mouseY);
            } else if(optionWidget.getOption() instanceof ColorOption && dialog instanceof ColorSelectorWidget){
                dialog.mouseReleased(mouseX, mouseY);
            }
        });
    }
}
