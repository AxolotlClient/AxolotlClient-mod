package io.github.axolotlclient.config.options;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public interface Tooltippable {

	String getName();

    default Text getTooltip(){
        return getTooltip(getName());
    }

	default @Nullable Text getTooltip(String location){

        if(!Objects.equals(new TranslatableText(location + ".tooltip").getString(), location + ".tooltip")) {
            return new TranslatableText(location + ".tooltip");
        }

		return null;
	}

    class AlphabeticalComparator implements Comparator<Tooltippable> {

        // Function to compare
        public int compare(Tooltippable s1, Tooltippable s2) {
            if(s1.toString().equals(s2.toString())) return 0;
            String[] strings = {s1.toString(), s2.toString()};
            Arrays.sort(strings, Collections.reverseOrder());

            if (strings[0].equals(s1.toString()))
                return 1;
            else
                return -1;
        }
    }
}
