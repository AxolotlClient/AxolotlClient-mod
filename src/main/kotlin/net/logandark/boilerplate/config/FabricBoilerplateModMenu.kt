package net.logandark.boilerplate.config

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import net.logandark.boilerplate.FabricBoilerplate

object FabricBoilerplateModMenu : ModMenuApi {
	override fun getModId() = FabricBoilerplate.modid
	override fun getModConfigScreenFactory() = ConfigScreenFactory(FabricBoilerplateConfig::createConfigScreen)
}