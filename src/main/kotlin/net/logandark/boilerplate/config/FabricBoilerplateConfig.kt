package net.logandark.boilerplate.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.logandark.boilerplate.FabricBoilerplate
import net.logandark.config.Config

object FabricBoilerplateConfig : Config("fabric-boilerplate.json", 1) {
	val example = add(
		object : ConfigOption<Boolean>(
			FabricBoilerplate.identifier("example"),
			"example",
			false,
			null
		) {
			override fun makeEntry(entryBuilder: ConfigEntryBuilder) =
				entryBuilder
					.startBooleanToggle(translationKey, get())
					.setDefaultValue(defaultValue)
					.setSaveConsumer(this::set)
					.build()

			override fun serialize() = JsonPrimitive(get())

			override fun deserialize(jsonElement: JsonElement) =
				if (jsonElement is JsonPrimitive && jsonElement.isBoolean)
					jsonElement.asBoolean
				else
					error("Invalid JsonElement to deserialize")
		}
	)
}