package io.github.axolotlclient.modules;

import io.github.axolotlclient.config.screen.CreditsScreen;
import io.github.axolotlclient.util.Logger;
import org.quiltmc.loader.api.ModContributor;
import org.quiltmc.loader.api.ModMetadata;
import org.quiltmc.loader.api.QuiltLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleLoader {

    public static List<AbstractModule> loadExternalModules(){
        ArrayList<AbstractModule> modules = new ArrayList<>();
        QuiltLoader.getEntrypointContainers("axolotlclient.module", AbstractModule.class).forEach(entrypoint -> {
            try {
                AbstractModule module = entrypoint.getEntrypoint();
                if(module != null) {
                    modules.add(module);
                    String modName = entrypoint.getProvider().metadata().name();
                    ModMetadata data = entrypoint.getProvider().metadata();
                    List<String> authorsNContributors = new ArrayList<>();

                    List<String> authors = data.contributors().stream().filter(contributor -> contributor.roles().contains("Author") || contributor.roles().contains("Owner")).map(ModContributor::name).collect(Collectors.toList());

                    List<String> contributors = data.contributors().stream().map(ModContributor::name).filter(name -> !authors.contains(name)).toList();
                    if (authors.isEmpty()) {
                        data.contributors().stream().findFirst().ifPresent(modContributor -> authors.add(modContributor.name()));
                    }
                    authorsNContributors.add("Author(s):");
                    authorsNContributors.addAll(authors);
                    authorsNContributors.add("");

                    authorsNContributors.add("Contributor(s):");
                    authorsNContributors.addAll(contributors);
                    CreditsScreen.externalModuleCredits.put(modName, authorsNContributors.toArray(new String[0]));
                }
            } catch (Exception e){
                Logger.warn("Skipping module: "+entrypoint.getProvider().metadata().name() + " because of error:");
                e.printStackTrace();
            }
        });
        return modules;
    }
}
