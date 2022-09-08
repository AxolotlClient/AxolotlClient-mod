package io.github.axolotlclient.modules.particles;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.*;
import io.github.axolotlclient.mixin.AccessorParticle;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Particles extends AbstractModule {

    private static final Particles Instance = new Particles();

    public final HashMap<ParticleType, HashMap<String, OptionBase<?>>> particleOptions = new HashMap<>();
    public final HashMap<Particle, ParticleType> particleMap = new HashMap<>();

    private final OptionCategory cat = new OptionCategory("particles");
    private final BooleanOption enabled = new BooleanOption("enabled", false);

    public static Particles getInstance() {
        return Instance;
    }

    @Override
    public void init() {
        cat.add(enabled);

        addParticleOptions();
        AxolotlClient.CONFIG.rendering.addSubCategory(cat);
    }

    private void addParticleOptions(){
        for(ParticleType type : Arrays.stream(ParticleType.values()).sorted(new AlphabeticalComparator()).collect(Collectors.toList())){
            OptionCategory category = new OptionCategory(StringUtils.capitalize(Util.splitAtCapitalLetters(type.getName().replace("_", ""))));
            HashMap<String, OptionBase<?>> optionsByKey = new LinkedHashMap<>();

            populateMap(optionsByKey,
                    new IntegerOption("count", 1, 1, 20),
                    new BooleanOption("customColor", false),
                    new ColorOption("color", "particles", new Color(-1)));

            if(type == ParticleType.CRIT || type == ParticleType.CRIT_MAGIC){
                populateMap(optionsByKey, new BooleanOption("alwaysCrit", false));
            }

            category.add(optionsByKey.values());
            particleOptions.put(type, optionsByKey);

            cat.addSubCategory(category);
        }
    }

    private void populateMap(HashMap<String, OptionBase<?>> map, OptionBase<?>... options){
        for(OptionBase<?> option:options){
            map.put(option.getName(), option);
        }
    }

    public void applyOptions(Particle particle){
        if(enabled.get() && particleMap.containsKey(particle)) {
            HashMap<String, OptionBase<?>> options = particleOptions.get(particleMap.get(particle));

            if (((BooleanOption)options.get("customColor")).get()) {
                Color color = ((ColorOption) options.get("color")).get();
                particle.setColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
                ((AccessorParticle) particle).setAlpha(color.getAlpha() / 255F);
            }
        }
    }

    public int getMultiplier(ParticleType type) {
        if(enabled.get()) {
            HashMap<String, OptionBase<?>> options = particleOptions.get(type);

            return ((IntegerOption) options.get("count")).get();
        }
        return 1;
    }

    public boolean getAlwaysOn(ParticleType type){
        return enabled.get() && ((BooleanOption)Particles.getInstance().particleOptions.get(type).get("alwaysCrit")).get();
    }

    protected static class AlphabeticalComparator implements Comparator<ParticleType> {

        // Function to compare
        public int compare(ParticleType s1, ParticleType s2) {
            if(s1.getName().equals(s2.getName())) return 0;
            String[] strings = {s1.getName(), s2.getName()};
            Arrays.sort(strings, Collections.reverseOrder());

            if (strings[0].equals(s1.getName()))
                return 1;
            else
                return -1;
        }
    }
}
