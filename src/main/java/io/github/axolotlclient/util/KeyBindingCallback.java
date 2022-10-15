package io.github.axolotlclient.util;

import com.mojang.blaze3d.platform.InputUtil;
import net.minecraft.client.option.KeyBind;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public final class KeyBindingCallback {

	public interface ChangeBind {
		void setBoundKey(InputUtil.Key boundKey);
	}

    public interface OnPress {
        void onPress(KeyBind binding);
    }
}
