package dev.tommasop1804.italyutils.classes.constants

import dev.tommasop1804.kutils.equalsIgnoreCase

/**
 * Represents the geographical distribution of an area within a predefined set of regions or distributions.
 * Each enum constant has a corresponding Italian name and numeric code.
 *
 * @property italianName The Italian name of the geographical distribution.
 * @property code The numeric code representing the geographical distribution.
 * @since 2026-02.1
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
     * @since 2026-05
     */
    Center("Centro", 3),
    /**
     * Represents the geographical distribution corresponding to the islands of Italy.
     *
     * @since 2026-05
     */
    Islands("Isole", 5),
    /**
     * Represents the north-western geographic distribution within a specific context.
     *
     * The `NorthWest` region is identified by its Italian name "Nord-ovest" and its numeric code 1.
     * It is one of the predefined entries in the `GeographicDistribution` enumeration, used for
     * categorizing regions or areas with geographic relevance.
     *
     * @since 2026-05
     */
    NorthWest("Nord-ovest", 1),
    /**
     * Represents the North-East geographic distribution in Italy.
     * This entry is part of the `GeographicDistribution` enumeration.
     *
     * @since 2026-05
     */
    NorthEast("Nord-est", 2),
    /**
     * Represents the southern geographic distribution in Italy.
     *
     * This entry is part of the `GeographicDistribution` enum and corresponds to the southern region of Italy.
     * It is characterized by the Italian name "Sud" and has a code value of 4.
     *
     * @since 2026-05
     */
    South("Sud", 4);

    companion object {
        /**
         * Retrieves the `GeographicDistribution` entry that matches the given Italian name.
         *
         * @param name The Italian name to search for, ignoring case sensitivity.
         * @return The `GeographicDistribution` entry that matches the given name, or null if no match is found.
         * @since 2026-02.1
         */
        infix fun ofItalianName(name: String) = entries.find { it.italianName equalsIgnoreCase name }
        
        /**
         * Retrieves the `GeographicDistribution` entry that matches the given code.
         *
         * @param code The numeric code to search for.
         * @return The `GeographicDistribution` entry with the specified code, or null if no match is found.
         * @since 2026-02.1
         */
        infix fun ofCode(code: Int) = entries.find { it.code == code }
        /**
         * Retrieves the `GeographicDistribution` entry that matches the given code as a string.
         *
         * @param code The code to search for as a string.
         * @return The `GeographicDistribution` entry that matches the given code, or null if no match is found.
         * @since 2026-02.1
         */
        infix fun ofCode(code: String) = entries.find { it.code.toString() == code }
    }
    
    /**
     * Provides the `component1` functionality for destructuring declarations.
     * 
     * This operator function allows retrieval of the `italianName` property from an instance of the class
     * in which it is defined. It is typically used in scenarios where destructuring declarations are utilized
     * to extract individual components of an object.
     * 
     * @return The value of the `italianName` property.
     * @since 2026-03
     */
    operator fun component1() = italianName
    /**
     * Retrieves the second component of this object when destructuring.
     *
     * This method allows destructuring declarations to access the `code` field.
     *
     * @return The value of the `code` field.
     * @since 2026-03
     */
    operator fun component2() = code
}