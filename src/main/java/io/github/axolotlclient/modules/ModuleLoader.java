package io.github.axolotlclient.modules;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.config.screen.CreditsScreen;
import io.github.axolotlclient.util.Logger;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.ArrayList;
import java.util.List;

public class ModuleLoader {

    public static List<AbstractModule> loadExternalModules(){
        ArrayList<AbstractModule> modules = new ArrayList<>();
        FabricLoader.getInstance().getEntrypointContainers("axolotlclient.module", AbstractModule.class).forEach(entrypoint -> {
            try {
                AbstractModule module = entrypoint.getEntrypoint();
                if(module != null) {
                    modules.add(module);
                    String modName = entrypoint.getProvider().getMetadata().getName();
                    ModMetadata data = entrypoint.getProvider().getMetadata();
                    List<String> authorsNContributors = new ArrayList<>();

                    if(!data.getAuthors().isEmpty()) {
                        authorsNContributors.add("Author(s):");
                        data.getAuthors().forEach(p -> authorsNContributors.add(p.getName()));
                        authorsNContributors.add("");
                    }

                    if(!data.getContributors().isEmpty()) {
                        authorsNContributors.add("Contributor(s):");
                        data.getContributors().forEach(p -> authorsNContributors.add(p.getName()));
                    }
                    CreditsScreen.externalModuleCredits.put(modName, authorsNContributors.toArray(new String[0]));
                }
            } catch (Exception e){
                Logger.warn("Skipping module: "+entrypoint.getProvider().getMetadata().getName() + " because of error:");
                e.printStackTrace();
            }
        });
        return modules;
    }
}
