/*
 * Copyright © 2021-2022 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.modules.screenshotUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.EnumOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.OSUtil;
import io.github.axolotlclient.util.Util;
import lombok.AllArgsConstructor;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class ScreenshotUtils extends AbstractModule {

    private static final ScreenshotUtils Instance = new ScreenshotUtils();

    private final OptionCategory category = new OptionCategory("axolotlclient.screenshotUtils");

    private final BooleanOption enabled = new BooleanOption("axolotlclient.enabled", false);

    private final List<Action> actions = Util.make(() -> {
        List<Action> actions = new ArrayList<>();
        actions.add(new Action("axolotlclient.copyAction",
                Formatting.AQUA,
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new LiteralText(I18n.translate("axolotlclient.copy_image"))),
                new CustomClickEvent((file) -> {
                    FileTransferable selection = new FileTransferable(file);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                })
        ));

        actions.add(new Action("axolotlclient.deleteAction",
                Formatting.LIGHT_PURPLE,
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new LiteralText(I18n.translate("axolotlclient.delete_image"))),
                new CustomClickEvent((file) -> {
                    try {
                        Files.delete(file.toPath());
                        Util.sendChatMessage(
                                new LiteralText(I18n.translate("axolotlclient.screenshot_deleted")
                                        .replace("<name>", file.getName())));
                    } catch (Exception e) {
                        Logger.warn("Couldn't delete Screenshot " + file.getName());
                    }
                })
        ));

        actions.add(new Action("axolotlclient.openAction",
                Formatting.WHITE,
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new LiteralText(I18n.translate("axolotlclient.open_image"))),
                new CustomClickEvent((file) -> OSUtil.getOS().open(file.toURI()))
        ));

        // If you have further ideas to what actions could be added here, please let us know!

        return actions;
    });

    private final EnumOption autoExec = new EnumOption("axolotlclient.autoExec", Util.make(() -> {
        List<String> names = new ArrayList<>();
        names.add("axolotlclient.off");
        actions.forEach(action -> names.add(action.getName()));
        return names.toArray(new String[0]);

    }), "axolotlclient.off");

    @Override
    public void init() {
        category.add(enabled, autoExec);

        AxolotlClient.CONFIG.general.addSubCategory(category);
    }

    public static ScreenshotUtils getInstance() {
        return Instance;
    }

    public Text onScreenshotTaken(Text text, File shot) {
        if (enabled.get()) {
            Text t = getUtilsText(shot);
            if (t != null) {
                return text.append("\n").append(t);
            }
        }
        return text;
    }

    private @Nullable Text getUtilsText(File file) {

        if (!autoExec.get().equals("axolotlclient.off")) {

            actions.parallelStream().filter(action -> autoExec.get().equals(action.getName())).collect(Collectors.toList()).get(0).clickEvent.setFile(file).doAction();
            return null;
        }

        Text message = new LiteralText("");
        actions.parallelStream().map(action -> action.getText(file)).iterator().forEachRemaining(text -> {
            message.append(text);
            message.append(" ");
        });
        return message;
    }

    @AllArgsConstructor
    public static class Action {

        private final String translationKey;
        private final Formatting formatting;
        private final HoverEvent hoverEvent;
        private final CustomClickEvent clickEvent;

        public Text getText(File file) {
            return new LiteralText(I18n.translate(translationKey))
                    .setStyle(new Style()
                            .setFormatting(formatting)
                            .setClickEvent(clickEvent.setFile(file))
                            .setHoverEvent(hoverEvent));
        }

        public String getName() {
            return translationKey;
        }
    }

    @AllArgsConstructor
    protected static class FileTransferable implements Transferable {

        private final File file;

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.javaFileListFlavor };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.javaFileListFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) {
            final ArrayList<File> files = new ArrayList<>();
            files.add(file);
            return files;
        }
    }

    public static class CustomClickEvent extends ClickEvent {

        private final OnActionCall action;
        private File file;

        public CustomClickEvent(OnActionCall action) {
            super(Action.byName(""), "");
            this.action = action;
        }

        public void doAction() {
            if (file != null) {
                action.doAction(file);
            } else {
                Logger.warn("How'd you manage to do this? " +
                        "Now there's a screenshot ClickEvent without a File attached to it!");
            }
        }

        public CustomClickEvent setFile(File file) {
            this.file = file;
            return this;
        }
    }

    interface OnActionCall {

        void doAction(File file);
    }
}
