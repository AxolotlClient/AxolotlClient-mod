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

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class ScreenshotUtils extends AbstractModule {
    private static final ScreenshotUtils Instance = new ScreenshotUtils();

    private final OptionCategory category = new OptionCategory("axolotlclient.screenshotUtils");

    private final BooleanOption enabled = new BooleanOption("axolotlclient.enabled", false);


    @Override
    public void init() {

        category.add(enabled);

        AxolotlClient.CONFIG.general.addSubCategory(category);
    }

    public static ScreenshotUtils getInstance(){
        return Instance;
    }

    public Text onScreenshotTaken(Text text, File shot){
        if(enabled.get()){
            return text.append("\n").append(getUtilsText(shot));
        }
        return text;
    }

    private Text getUtilsText(File file){

        return new LiteralText(I18n.translate("axolotlclient.copyAction"))
                .setStyle(new Style()
                        .setFormatting(Formatting.BLUE)
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(I18n.translate("axolotlclient.copy_image"))))
                        .setClickEvent(new CustomClickEvent(() -> {
                            FileTransferable selection = new FileTransferable(file);
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                        })))
                .append(" ")
                .append(
                        new LiteralText(I18n.translate("axolotlclient.deleteAction")).setStyle(new Style()
                                .setFormatting(Formatting.RED)
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(I18n.translate("axolotlclient.delete_image"))))
                                .setClickEvent(new CustomClickEvent(() -> {
                                    try {
                                        Files.delete(file.toPath());
                                        Util.sendChatMessage(new LiteralText(I18n.translate("axolotlclient.screenshot_deleted").replace("<name>", file.getName())));
                                    } catch (Exception e) {
                                        Logger.warn("Couldn't delete Screenshot " + file.getName());
                                    }
                                })))
                );
    }

    protected static class FileTransferable implements Transferable {
        private final File file;

        public FileTransferable(File file) {
            this.file = file;
        }

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

        public CustomClickEvent(OnActionCall action) {
            super(Action.byName(""), "");
            this.action = action;
        }

        public void doAction(){
            action.doAction();
        }
    }

    interface OnActionCall {
        void doAction();
    }
}
