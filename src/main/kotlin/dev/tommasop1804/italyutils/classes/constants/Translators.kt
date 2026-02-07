package dev.tommasop1804.italyutils.classes.constants

import dev.tommasop1804.italyutils.classes.constants.Translators.ITALIAN_COUNTRIES_TRANSLATOR
import dev.tommasop1804.kutils.annotations.CheckTranslationKey
import dev.tommasop1804.kutils.classes.translators.Translator
import dev.tommasop1804.kutils.compute
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object Translators {
    /**
     * Provides translations for country names in Italian.
     *
     * This variable initializes a `Translator` instance with a resource file
     * containing country names translated into Italian. The translation data
     * is expected to be stored in a YAML file located at "it-country.yml".
     *
     * **WARNING: USE `alpha2` AS KEY OF TRANSLATION**
     *
     * @property ITALIAN_COUNTRIES_TRANSLATOR A `Translator` instance that loads
     * translations for country names in Italian from the specified resource file.
     * @since 2026-02.1
     */
    @CheckTranslationKey(correctKey = $$"Country$alpha2")
    val ITALIAN_COUNTRIES_TRANSLATOR
        get() = Translator(compute {
            val resource = this::class.java.classLoader.getResource(
                "it-country.yml"
            ) ?: throw FileNotFoundException("it-country.yml")
            val tempFile = Files.createTempFile("it-country", ".yml").toFile()
            Files.copy(resource.openStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            tempFile
        })
}