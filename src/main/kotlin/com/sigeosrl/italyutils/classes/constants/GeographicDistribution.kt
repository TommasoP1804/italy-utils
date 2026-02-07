package com.sigeosrl.italyutils.classes.constants

import dev.tommasop1804.kutils.equalsIgnoreCase

/**
 * Represents the geographical distribution of an area within a predefined set of regions or distributions.
 * Each enum constant has a corresponding Italian name and numeric code.
 *
 * @property italianName The Italian name of the geographical distribution.
 * @property code The numeric code representing the geographical distribution.
 * @since 2026-02
 */
@Suppress("unused")
enum class GeographicDistribution(val italianName: String, val code: Int) {
    /**
     * Represents the geographical area of Italy identified as "Centro" (Center).
     *
     * This enum constant is part of `GeographicDistribution` and includes
     * properties such as an Italian name and a numeric code associated
     * with the specific distribution.
     *
     * @property italianName The Italian name for the geographic distribution.
     * @property code The numeric code representing the geographic area.
     * @since 2026-02
     */
    CENTER("Centro", 3),
    /**
     * Represents the geographical distribution corresponding to the islands of Italy.
     *
     * @property italianName The Italian name for this distribution.
     * @property code The numerical code associated with this distribution.
     * @since 2026-02
     */
    ISLANDS("Isole", 5),
    /**
     * Represents the north-western geographic distribution within a specific context.
     *
     * The `NORTH_WEST` region is identified by its Italian name "Nord-ovest" and its numeric code 1.
     * It is one of the predefined entries in the `GeographicDistribution` enumeration, used for
     * categorizing regions or areas with geographic relevance.
     *
     * @property italianName The name of the distribution in Italian.
     * @property code The numeric code associated with the distribution.
     * @since 2026-02
     */
    NORTH_WEST("Nord-ovest", 1),
    /**
     * Represents the North-East geographic distribution in Italy.
     * This entry is part of the `GeographicDistribution` enumeration.
     *
     * @property italianName The Italian name of the geographic area.
     * @property code The numeric code associated with this geographic area.
     * @since 2026-02
     */
    NORTH_EAST("Nord-est", 2),
    /**
     * Represents the southern geographic distribution in Italy.
     *
     * This entry is part of the `GeographicDistribution` enum and corresponds to the southern region of Italy.
     * It is characterized by the Italian name "Sud" and has a code value of 4.
     *
     * @property italianName The Italian name for the geographic distribution, specifically "Sud" for this entry.
     * @property code The numeric code associated with this distribution, which is 4 for the south.
     * @since 2026-02
     */
    SOUTH("Sud", 4);

    companion object {
        /**
         * Retrieves the `GeographicDistribution` entry that matches the given Italian name.
         *
         * @param name The Italian name to search for, ignoring case sensitivity.
         * @return The `GeographicDistribution` entry that matches the given name, or null if no match is found.
         * @since 2026-02
         */
        infix fun ofItalianName(name: String) = entries.find { it.italianName equalsIgnoreCase name }
        
        /**
         * Retrieves the `GeographicDistribution` entry that matches the given code.
         *
         * @param code The numeric code to search for.
         * @return The `GeographicDistribution` entry with the specified code, or null if no match is found.
         * @since 2026-02
         */
        infix fun ofCode(code: Int) = entries.find { it.code == code }
        /**
         * Retrieves the `GeographicDistribution` entry that matches the given code as a string.
         *
         * @param code The code to search for as a string.
         * @return The `GeographicDistribution` entry that matches the given code, or null if no match is found.
         * @since 2026-02
         */
        infix fun ofCode(code: String) = entries.find { it.code.toString() == code }
    }
}