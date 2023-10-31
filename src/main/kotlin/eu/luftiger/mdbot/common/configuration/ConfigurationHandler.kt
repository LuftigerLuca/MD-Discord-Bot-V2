package eu.luftiger.mdbot.common.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.nio.file.Files

class ConfigurationHandler {

    private val objectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    fun loadConfiguration(): Configuration {
        val configurationFile = File("config.yaml")
        if (!configurationFile.exists()) {
            val inputStream = this.javaClass.classLoader.getResourceAsStream("config.yaml")
            Files.copy(inputStream!!, configurationFile.toPath())
        }

        return objectMapper.readValue(configurationFile, Configuration::class.java)
    }
}