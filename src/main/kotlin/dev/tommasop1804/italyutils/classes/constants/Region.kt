package dev.tommasop1804.italyutils.classes.constants

import dev.tommasop1804.kutils.String2
import dev.tommasop1804.kutils.equalsIgnoreCase

/**
 * Represents geographical regions, each associated with a display name
 * and an ISTAT (Italian National Institute of Statistics) code.
 *
 * This enumeration provides definitions for various regions of Italy
 * and supports functionality to retrieve associated provinces, capitals,
 * and perform lookups based on region names or codes.
 *
 * @param displayName The human-readable name of the region.
 * @param istatCode The ISTAT code that uniquely identifies the region.
 * @since 2026-02.1
 * @author Tommaso Pastorelli
 */
@Suppress("unused")
enum class Region(
    val displayName: String,
    val istatCode: String
) {
    Abruzzo("Abruzzo", "13"),
    Basilicata("Basilicata", "17"),
    Calabria("Calabria", "18"),
    Campania("Campania", "15"),
    EmiliaRomagna("Emilia-Romagna", "08"),
    FriuliVeneziaGiulia("Friuli-Venezia Giulia", "06"),
    Lazio("Lazio", "12"),
    Liguria("Liguria", "07"),
    Lombardia("Lombardia", "03"),
    Marche("Marche", "11"),
    Molise("Molise", "14"),
    Piemonte("Piemonte", "01"),
    Puglia("Puglia", "16"),
    Sardegna("Sardegna", "20"),
    Sicilia("Sicilia", "19"),
    Toscana("Toscana", "09"),
    TrentinoAltoAdige("Trentino-Alto Adige", "04"),
    Umbria("Umbria", "10"),
    ValleDAosta("Valle d'Aosta", "02"),
    Veneto("Veneto", "05");

    /**
     * Represents a list of provinces associated with the current region.
     * This property retrieves provinces based on the region.
     *
     * @receiver The region to which the provinces belong.
     * @return A list of provinces belonging to this region.
     * @since 2026-02.1
     */
    val provinces: List<Province>
        get() = Province.byRegion(this)

    /**
     * Represents the capital of the region.
     * This value is dynamically computed from the associated province data
     * of the region instance.
     *
     * @return The name of the capital city based on the region's provinces.
     * @since 2026-02.1
     */
    val capital: String2
        get() = capitals[this]!!

    companion object {
        /**
         * Maps each entry in the collection to its corresponding capital.
         * This mapping is derived by associating each entry with the first
         * value of its province list where the Boolean condition in the pair is true.
         *
         * @since 2026-02.1
         */
        @JvmStatic
        val capitals by lazy {
            entries.associateWith {
                val p = Province.byRegion(it).first { pr -> pr.isRegionalCapital }
                p.code to p.displayName
            }
        }

        /**
         * Finds a Region instance based on the provided display name, if available.
         *
         * @param regione The display name of the region to search for.
         * @return The Region object that matches the given display name, or null if no match is found.
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun ofName(regione: String): Region? {
            entries.forEach {
                if (it.displayName equalsIgnoreCase regione)
                    return it
            }
            return null
        }

        /**
         * Finds and returns the first entry in the region list that matches the specified ISTAT code.
         *
         * @param code The ISTAT code to search for.
         * @return The region entry matching the given ISTAT code, or null if no match is found.
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun ofIstatCode(code: String) = entries.find { it.istatCode == code }
        /**
         * Finds an entry in the collection with a matching ISTAT code.
         *
         * @param code The ISTAT code as a [Number] to match against the entries.
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun ofIstatCode(code: Number) = entries.find { it.istatCode.toInt() == code.toInt() }

        /**
         * Retrieves the region associated with the given province.
         *
         * @param province The province whose associated region is to be retrieved.
         * @return The region associated with the specified province.
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun fromProvince(province: Province) = province.region

        /**
         * Retrieves the region associated with a given province code.
         *
         * @param provinceCode the code of the province to map to a region
         * @return the region corresponding to the provided province code, or null if no matching province is found
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun fromProvince(provinceCode: String) = (Province.ofCode(provinceCode))?.region
    }

    /**
     * Returns the display name of the Region when used in a destructuring declaration.
     * 
     * This method allows destructuring assignments to retrieve the display name of the region.
     *
     * @return The display name of the region.
     * @since 2026-03
     */
    operator fun component1() = displayName
    /**
     * Operator function providing destructuring functionality to retrieve the second component of the Region.
     * 
     * Returns the `istatCode` associated with the Region.
     *
     * @return The `istatCode` field of the Region.
     * @since 2026-03
     */
    operator fun component2() = istatCode
}
