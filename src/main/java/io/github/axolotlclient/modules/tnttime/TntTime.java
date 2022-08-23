package io.github.axolotlclient.modules.tnttime;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.IntegerOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;

public class TntTime extends AbstractModule {

    public static Identifier ID = new Identifier("tnttime");
    private static final TntTime Instance = new TntTime();

    private DecimalFormat format = new DecimalFormat("##");
    private int decimals;

    private final OptionCategory category = new OptionCategory("tnttime");
    public final BooleanOption enabled = new BooleanOption("enabled", false);
    private final IntegerOption decimalPlaces = new IntegerOption("decimalPlaces", 2, 0, 6);

    public static TntTime getInstance() {
        return Instance;
    }

    @Override
    public void init() {
        category.add(enabled, decimalPlaces);
        AxolotlClient.CONFIG.rendering.addSubCategory(category);
    }

    @Override
    public void tick() {
        if(decimalPlaces.get() != decimals){
            StringBuilder string = new StringBuilder("#0");
            if (decimalPlaces.get() > 0) {
                string.append(".");
                for(int i=0;i<decimalPlaces.get();i++) {
                    string.append("0");
                }
            }
            format = new DecimalFormat(string.toString());
            decimals = decimalPlaces.get();
        }
    }

    public Text getFuseTime(int time){
        float secs = time/20F;
        return new LiteralText(format.format(secs)).copy().setStyle(new Style().setFormatting(getCurrentColor(secs)));
    }

    private Formatting getCurrentColor(float seconds){
        if (seconds > 7d) {
            return Formatting.DARK_AQUA;
        } else if (seconds > 6d) {
            return Formatting.AQUA;
        } else if (seconds > 4d) {
            return Formatting.DARK_GREEN;
        } else if (seconds > 3d) {
            return Formatting.GREEN;
        } else if (seconds > 2d) {
            return Formatting.GOLD;
        } else if (seconds > 1d) {
            return Formatting.RED;
        } else if (seconds > 0d) {
            return Formatting.DARK_RED;
        } else {
            return Formatting.WHITE;
        }
    }
}
