package dev.tommasop1804.italyutils.classes.constants

import dev.tommasop1804.kutils.equalsIgnoreCase
import dev.tommasop1804.kutils.tryOr

/**
 * Represents a province in Italy, defined by various attributes including its code, display name, region,
 * whether it is a regional capital, and a numeric code.
 *
 * @property code The two-character code of the province.
 * @property displayName The full display name of the province.
 * @property region The region to which the province belongs.
 * @property isRegionalCapital Indicates if the province serves as the capital of its region.
 * @property istatCode The numeric code associated with the province.
 * @since 2026-02.1
 */
@Suppress("unused")
enum class Province(
    val code: String,
    val displayName: String,
    val region: Region,
    val isRegionalCapital: Boolean,
    val istatCode: String
) {
    LAquila("AQ", "L'Aquila", Region.Abruzzo, true, "066"),
    Chieti("CH", "Chieti", Region.Abruzzo, false, "069"),
    Pescara("PE", "Pescara", Region.Abruzzo, false, "068"),
    Teramo("TE", "Teramo", Region.Abruzzo, false, "067"),

    Matera("MT", "Matera", Region.Basilicata, true, "077"),
    Potenza("PZ", "Potenza", Region.Basilicata, false, "076"),

    Catanzaro("CZ", "Catanzaro", Region.Calabria, true, "079"),
    Cosenza("CS", "Cosenza", Region.Calabria, false, "078"),
    Crotone("KR", "Crotone", Region.Calabria, false, "101"),
    ReggioCalabria("RC", "Reggio Calabria", Region.Calabria, false, "080"),
    ViboValentia("VV", "Vibo Valentia", Region.Calabria, false, "102"),

    Avellino("AV", "Avellino", Region.Campania, false, "064"),
    Benevento("BN", "Benevento", Region.Campania, false, "062"),
    Caserta("CE", "Caserta", Region.Campania, false, "061"),
    Napoli("NA", "Napoli", Region.Campania, true, "063"),
    Salerno("SA", "Salerno", Region.Campania, false, "065"),

    Bologna("BO", "Bologna", Region.EmiliaRomagna, true, "037"),
    Ferrara("FE", "Ferrara", Region.EmiliaRomagna, false, "038"),
    ForliCesena("FC", "Forlì-Cesena", Region.EmiliaRomagna, false, "040"),
    Modena("MO", "Modena", Region.EmiliaRomagna, false, "036"),
    Parma("PR", "Parma", Region.EmiliaRomagna, false, "034"),
    Piacenza("PC", "Piacenza", Region.EmiliaRomagna, false, "033"),
    Ravenna("RA", "Ravenna", Region.EmiliaRomagna, false, "039"),
    ReggioEmilia("RE", "Reggio Emilia", Region.EmiliaRomagna, false, "035"),
    Rimini("RN", "Rimini", Region.EmiliaRomagna, false, "099"),

    Gorizia("GO", "Gorizia", Region.FriuliVeneziaGiulia, false, "031"),
    Pordenone("PN", "Pordenone", Region.FriuliVeneziaGiulia, false, "093"),
    Trieste("TS", "Trieste", Region.FriuliVeneziaGiulia, true, "032"),
    Udine("UD", "Udine", Region.FriuliVeneziaGiulia, false, "030"),

    Frosinone("FR", "Frosinone", Region.Lazio, false, "060"),
    Latina("LT", "Latina", Region.Lazio, false, "059"),
    Rieti("RI", "Rieti", Region.Lazio, false, "057"),
    Roma("RM", "Roma", Region.Lazio, true, "058"),
    Viterbo("VT", "Viterbo", Region.Lazio, false, "056"),

    Genova("GE", "Genova", Region.Liguria, true, "010"),
    Imperia("IM", "Imperia", Region.Liguria, false, "008"),
    LaSpezia("SP", "La Spezia", Region.Liguria, false, "011"),
    Savona("SV", "Savona", Region.Liguria, false, "009"),

    Bergamo("BG", "Bergamo", Region.Lombardia, false, "016"),
    Brescia("BS", "Brescia", Region.Lombardia, false, "017"),
    Como("CO", "Como", Region.Lombardia, false, "013"),
    Cremona("CR", "Cremona", Region.Lombardia, false, "019"),
    Lecco("LC", "Lecco", Region.Lombardia, false, "097"),
    Lodi("LO", "Lodi", Region.Lombardia, false, "098"),
    Mantova("MN", "Mantova", Region.Lombardia, false, "020"),
    Milano("MI", "Milano", Region.Lombardia, true, "015"),
    MonzaEDellaBrianza("MB", "Monza e della Brianza", Region.Lombardia, false, "108"),
    Pavia("PV", "Pavia", Region.Lombardia, false, "018"),
    Sondrio("SO", "Sondrio", Region.Lombardia, false, "014"),
    Varese("VA", "Varese", Region.Lombardia, false, "012"),

    Ancona("AN", "Ancona", Region.Marche, true, "042"),
    AscoliPiceno("AP", "Ascoli Piceno", Region.Marche, false, "044"),
    Fermo("FM", "Fermo", Region.Marche, false, "109"),
    Macerata("MC", "Macerata", Region.Marche, false, "043"),
    PesaroEUrbino("PU", "Pesaro e Urbino", Region.Marche, false, "041"),

    Campobasso("CB", "Campobasso", Region.Molise, true, "070"),
    Isernia("IS", "Isernia", Region.Molise, false, "094"),

    Alessandria("AL", "Alessandria", Region.Piemonte, false, "006"),
    Asti("AT", "Asti", Region.Piemonte, false, "005"),
    Biella("BI", "Biella", Region.Piemonte, false, "096"),
    Cuneo("CN", "Cuneo", Region.Piemonte, false, "004"),
    Novara("NO", "Novara", Region.Piemonte, false, "003"),
    Torino("TO", "Torino", Region.Piemonte, true, "001"),
    VerbanoCusioOssola("VB", "Verbano-Cusio-Ossola", Region.Piemonte, false, "103"),
    Vercelli("VC", "Vercelli", Region.Piemonte, false, "002"),

    Bari("BA", "Bari", Region.Puglia, true, "072"),
    BarlettaAndriaTrani("BT", "Barletta-Andria-Trani", Region.Puglia, false, "110"),
    Brindisi("BR", "Brindisi", Region.Puglia, false, "074"),
    Foggia("FG", "Foggia", Region.Puglia, false, "071"),
    Lecce("LE", "Lecce", Region.Puglia, false, "075"),
    Taranto("TA", "Taranto", Region.Puglia, false, "073"),

    Cagliari("CA", "Cagliari", Region.Sardegna, true, "318"),
    SulcisIglesiente("CI", "Sulcis Iglesiente", Region.Sardegna, false, "119"),
    MedioCampidano("VS", "Medio Campidano", Region.Sardegna, false, "117"),
    Nuoro("NU", "Nuoro", Region.Sardegna, false, "114"),
    Ogliastra("OG", "Ogliastra", Region.Sardegna, false, "116"),
    GalluraNordEstSardegna("OT", "Gallura Nord-Est Sardegna", Region.Sardegna, false, "113"),
    Oristano("OR", "Oristano", Region.Sardegna, false, "115"),
    Sassari("SS", "Sassari", Region.Sardegna, false, "312"),

    Agrigento("AG", "Agrigento", Region.Sicilia, false, "084"),
    Caltanissetta("CL", "Caltanissetta", Region.Sicilia, false, "085"),
    Catania("CT", "Catania", Region.Sicilia, false, "087"),
    Enna("EN", "Enna", Region.Sicilia, false, "086"),
    Messina("ME", "Messina", Region.Sicilia, false, "083"),
    Palermo("PA", "Palermo", Region.Sicilia, true, "082"),
    Ragusa("RG", "Ragusa", Region.Sicilia, false, "088"),
    Siracusa("SR", "Siracusa", Region.Sicilia, false, "089"),
    Trapani("TP", "Trapani", Region.Sicilia, false, "081"),

    Arezzo("AR", "Arezzo", Region.Toscana, false, "051"),
    Firenze("FI", "Firenze", Region.Toscana, true, "048"),
    Grosseto("GR", "Grosseto", Region.Toscana, false, "053"),
    Livorno("LI", "Livorno", Region.Toscana, false, "049"),
    Lucca("LU", "Lucca", Region.Toscana, false, "046"),
    MassaCarrara("MS", "Massa-Carrara", Region.Toscana, false, "045"),
    Pisa("PI", "Pisa", Region.Toscana, false, "050"),
    Pistoia("PT", "Pistoia", Region.Toscana, false, "047"),
    Prato("PO", "Prato", Region.Toscana, false, "100"),
    Siena("SI", "Siena", Region.Toscana, false, "052"),

    Bolzano("BZ", "Bolzano", Region.TrentinoAltoAdige, false, "021"),
    Trento("TN", "Trento", Region.TrentinoAltoAdige, true, "022"),

    Perugia("PG", "Perugia", Region.Umbria, true, "054"),
    Terni("TR", "Terni", Region.Umbria, false, "055"),

    Aosta("AO", "Aosta", Region.ValleDAosta, true, "007"),

    Belluno("BL", "Belluno", Region.Veneto, false, "025"),
    Padova("PD", "Padova", Region.Veneto, false, "028"),
    Rovigo("RO", "Rovigo", Region.Veneto, false, "029"),
    Treviso("TV", "Treviso", Region.Veneto, false, "026"),
    Venezia("VE", "Venezia", Region.Veneto, true, "027"),
    Verona("VR", "Verona", Region.Veneto, false, "023"),
    Vicenza("VI", "Vicenza", Region.Veneto, false, "024");

    /**
     * Determines if the current province is marked as deprecated using the @Deprecated annotation.
     *
     * This function performs a reflective check to identify whether the corresponding field
     * associated with the province is annotated with the `@Deprecated` annotation.
     * If an exception occurs during the check, the result defaults to `false`.
     *
     * @receiver The province to check for deprecation status.
     * @return `true` if the province is annotated with `@Deprecated`, `false` otherwise.
     * @since 2026-02.1
     */
    val isDeprecated
        get() = tryOr({ false }) { javaClass.getField(name).isAnnotationPresent(Deprecated::class.java) }

    companion object {
        /**
         * Represents a collection of provinces that are designated as regional capitals.
         * This subset is filtered from all available entries in the collection based on the `isRegionalCapital` property.
         * Each entry included in this collection meets the condition of being identified as a regional capital.
         *
         * @since 2026-02.1
         */
        val CAPITALS = entries.filter { it.isRegionalCapital }

        /**
         * Retrieves a Province instance by matching the given code with the code of the existing entries.
         *
         * @param code The code to match against the Province entries.
         * @return The Province instance with a matching code, or null if no matches are found.
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun ofCode(code: String) = entries.find { it.code equalsIgnoreCase code }

        /**
         * Finds the first province entry that matches the specified numeric code.
         *
         * @param code The numeric code of the province to search for.
         * @return The province entry matching the given numeric code, or null if no match is found.
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun ofIstatCode(code: String) = entries.find { it.istatCode == code }

        /**
         * Retrieves a Province instance based on the provided numeric code.
         *
         * @param code The numeric code associated with the province to be retrieved.
         * @return The Province object that matches the given numeric code, or null if no match is found.
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun ofIstatCode(code: Number) = entries.find { it.istatCode.toInt() == code.toInt() }

        /**
         * Filters entries by a specific region, excluding deprecated ones.
         *
         * @param region The region to filter the entries by.
         * @return A list of entries belonging to the specified region and not marked as deprecated.
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun byRegion(region: Region) = entries.filter { it.region == region && !it.isDeprecated }

        /**
         * Filters entries by the specified region and excludes deprecated ones.
         *
         * @param region The ISTAT code as a [Number] representing the region to filter by.
         * @return A list of entries that belong to the specified region and are not deprecated.
         * @since 2026-02.1
         */
        @JvmStatic
        infix fun byRegion(region: Number) = entries.filter {
            it.region == (Region.ofIstatCode(region)
                ?: IllegalArgumentException("Region not found.")) && !it.isDeprecated
        }
    }

    /**
     * Retrieves the `code` property of the `Province` class instance when used in a destructuring declaration.
     *
     * This operator function is typically used to extract the `code` field of a `Province` object
     * as the first component in destructuring syntax.
     *
     * @receiver The `Province` instance whose `code` field is being retrieved.
     * @return The value of the `code` property for the current `Province` instance.
     * @since 2026-03
     */
    operator fun component1() = code
    /**
     * Retrieves the second component of the `Province` class for destructuring declarations.
     *
     * @return The `displayName` property of the province, which represents the human-readable name.
     * @since 2026-03
     */
    operator fun component2() = displayName
    /**
     * Provides the third component of the `Province` data structure.
     *
     * This operator function returns the `region` field of the `Province` class,
     * allowing destructuring declarations to access the region information
     * in an instance.
     *
     * @return The region associated with this province.
     * @since 2026-03
     */
    operator fun component3() = region
    /**
     * Retrieves the `isRegionalCapital` property of the `Province` class.
     * 
     * This function is used as part of Kotlin's destructuring declarations,
     * enabling the `isRegionalCapital` property to be accessed directly as
     * the fourth component in a destructured `Province` object.
     *
     * @return The `isRegionalCapital` value of the `Province` instance.
     * @since 2026-03
     */
    operator fun component4() = isRegionalCapital
    /**
     * Retrieves the ISTAT code associated with this province.
     *
     * This function represents a destructuring component for obtaining
     * the province's ISTAT code when used in a destructuring declaration.
     *
     * @return The ISTAT code of the province.
     * @since 2026-03
     */
    operator fun component5() = istatCode
}