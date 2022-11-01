package io.github.axolotlclient.modules.hud.gui.component;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 *
 * An interface that represents an object that contains an Identifier, as well as ways to translate itself into a
 */
public interface Identifiable {

    /**
     * Returns a unique identifier for this object
     *
     * @return The identifier
     */
    Identifier getId();

    /**
     * Gets the display name key
     *
     * @return The display name key
     */
    default String getNameKey() {
        return getId().getPath();
    }

    /**
     * The translated name of the object
     *
     * @return String containing the name
     */
    default String getName() {
        return I18n.translate(getNameKey());
    }

}
