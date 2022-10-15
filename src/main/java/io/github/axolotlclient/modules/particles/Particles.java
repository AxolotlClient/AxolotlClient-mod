package io.github.axolotlclient.modules.particles;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.ColorOption;
import io.github.axolotlclient.AxolotlclientConfig.options.IntegerOption;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionBase;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.mixin.ParticleAccessor;
import io.github.axolotlclient.modules.AbstractModule;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class Particles extends AbstractModule {

    private static final Particles Instance = new Particles();

    public final HashMap<ParticleType<?>, HashMap<String, OptionBase<?>>> particleOptions = new HashMap<>();
    public final HashMap<Particle, ParticleType<?>> particleMap = new HashMap<>();

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
        for(ParticleType<?> type : Registry.PARTICLE_TYPE.stream().sorted(new AlphabeticalComparator()).toList()){
            if(Registry.PARTICLE_TYPE.getId(type) != null) {
                OptionCategory category = new OptionCategory(Arrays.stream(Registry.PARTICLE_TYPE.getId(type).getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" ")), false);
                HashMap<String, OptionBase<?>> optionsByKey = new LinkedHashMap<>();

                populateMap(optionsByKey,
                        new BooleanOption("showParticle", true),
                        new IntegerOption("count", 1, 1, 20),
                        new BooleanOption("customColor", false),
                        new ColorOption("color", "particles", Color.WHITE));

                if(type == ParticleTypes.CRIT || type == ParticleTypes.ENCHANTED_HIT){
                    populateMap(optionsByKey, new BooleanOption("alwaysCrit", false));
                }

                category.add(optionsByKey.values());
                particleOptions.put(type, optionsByKey);

                cat.addSubCategory(category);
            }
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
                ((ParticleAccessor) particle).setColorAlpha(color.getAlpha() / 255F);
            }
        }
    }

    public int getMultiplier(ParticleType<?> type) {
        if(enabled.get()) {
            HashMap<String, OptionBase<?>> options = particleOptions.get(type);

            return ((IntegerOption) options.get("count")).get();
        }
        return 1;
    }

    public boolean getAlwaysOn(ParticleType<?> type){
        return enabled.get() && ((BooleanOption)Particles.getInstance().particleOptions.get(type).get("alwaysCrit")).get();
    }

    public boolean getShowParticle(ParticleType<?> type){
        return enabled.get() ? ((BooleanOption)Particles.getInstance().particleOptions.get(type).get("showParticle")).get() : true;
    }

    protected static class AlphabeticalComparator implements Comparator<ParticleType<?>> {

        // Function to compare
        public int compare(ParticleType<?> s1, ParticleType<?> s2) {
            if(getName(s1).equals(getName(s2))) return 0;
            String[] strings = {getName(s1), getName(s2)};
            Arrays.sort(strings, Collections.reverseOrder());

            if (strings[0].equals(getName(s1)))
                return 1;
            else
                return -1;
        }

        private String getName(ParticleType<?> type){
            if(Registry.PARTICLE_TYPE.getId(type) != null) {
                return Registry.PARTICLE_TYPE.getId(type).getPath();
            }
            return "";
        }
    }
}
