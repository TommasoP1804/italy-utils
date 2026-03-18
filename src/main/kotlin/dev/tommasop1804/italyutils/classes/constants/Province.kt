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
    L_AQUILA("AQ", "L'Aquila", Region.ABRUZZO, true, "066"),
    CHIETI("CH", "Chieti", Region.ABRUZZO, false, "069"),
    PESCARA("PE", "Pescara", Region.ABRUZZO, false, "068"),
    TERAMO("TE", "Teramo", Region.ABRUZZO, false, "067"),

    MATERA("MT", "Matera", Region.BASILICATA, true, "077"),
    POTENZA("PZ", "Potenza", Region.BASILICATA, false, "076"),

    CATANZARO("CZ", "Catanzaro", Region.CALABRIA, true, "079"),
    COSENZA("CS", "Cosenza", Region.CALABRIA, false, "078"),
    CROTONE("KR", "Crotone", Region.CALABRIA, false, "101"),
    REGGIO_CALABRIA("RC", "Reggio Calabria", Region.CALABRIA, false, "080"),
    VIBO_VALENTIA("VV", "Vibo Valentia", Region.CALABRIA, false, "102"),

    AVELLINO("AV", "Avellino", Region.CAMPANIA, false, "064"),
    BENEVENTO("BN", "Benevento", Region.CAMPANIA, false, "062"),
    CASERTA("CE", "Caserta", Region.CAMPANIA, false, "061"),
    NAPOLI("NA", "Napoli", Region.CAMPANIA, true, "063"),
    SALERNO("SA", "Salerno", Region.CAMPANIA, false, "065"),

    BOLOGNA("BO", "Bologna", Region.EMILIA_ROMAGNA, true, "037"),
    FERRARA("FE", "Ferrara", Region.EMILIA_ROMAGNA, false, "038"),
    FORLI_CESENA("FC", "Forlì-Cesena", Region.EMILIA_ROMAGNA, false, "040"),
    MODENA("MO", "Modena", Region.EMILIA_ROMAGNA, false, "036"),
    PARMA("PR", "Parma", Region.EMILIA_ROMAGNA, false, "034"),
    PIACENZA("PC", "Piacenza", Region.EMILIA_ROMAGNA, false, "033"),
    RAVENNA("RA", "Ravenna", Region.EMILIA_ROMAGNA, false, "039"),
    REGGIO_EMILIA("RE", "Reggio Emilia", Region.EMILIA_ROMAGNA, false, "035"),
    RIMINI("RN", "Rimini", Region.EMILIA_ROMAGNA, false, "099"),

    GORIZIA("GO", "Gorizia", Region.FRIULI_VENEZIA_GIULIA, false, "031"),
    PORDENONE("PN", "Pordenone", Region.FRIULI_VENEZIA_GIULIA, false, "093"),
    TRIESTE("TS", "Trieste", Region.FRIULI_VENEZIA_GIULIA, true, "032"),
    UDINE("UD", "Udine", Region.FRIULI_VENEZIA_GIULIA, false, "030"),

    FROSINONE("FR", "Frosinone", Region.LAZIO, false, "060"),
    LATINA("LT", "Latina", Region.LAZIO, false, "059"),
    RIETI("RI", "Rieti", Region.LAZIO, false, "057"),
    ROMA("RM", "Roma", Region.LAZIO, true, "058"),
    VITERBO("VT", "Viterbo", Region.LAZIO, false, "056"),

    GENOVA("GE", "Genova", Region.LIGURIA, true, "010"),
    IMPERIA("IM", "Imperia", Region.LIGURIA, false, "008"),
    LA_SPEZIA("SP", "La Spezia", Region.LIGURIA, false, "011"),
    SAVONA("SV", "Savona", Region.LIGURIA, false, "009"),

    BERGAMO("BG", "Bergamo", Region.LOMBARDIA, false, "016"),
    BRESCIA("BS", "Brescia", Region.LOMBARDIA, false, "017"),
    COMO("CO", "Como", Region.LOMBARDIA, false, "013"),
    CREMONA("CR", "Cremona", Region.LOMBARDIA, false, "019"),
    LECCO("LC", "Lecco", Region.LOMBARDIA, false, "097"),
    LODI("LO", "Lodi", Region.LOMBARDIA, false, "098"),
    MANTOVA("MN", "Mantova", Region.LOMBARDIA, false, "020"),
    MILANO("MI", "Milano", Region.LOMBARDIA, true, "015"),
    MONZA_E_DELLA_BRIANZA("MB", "Monza e della Brianza", Region.LOMBARDIA, false, "108"),
    PAVIA("PV", "Pavia", Region.LOMBARDIA, false, "018"),
    SONDRIO("SO", "Sondrio", Region.LOMBARDIA, false, "014"),
    VARESE("VA", "Varese", Region.LOMBARDIA, false, "012"),

    ANCONA("AN", "Ancona", Region.MARCHE, true, "042"),
    ASCOLI_PICENO("AP", "Ascoli Piceno", Region.MARCHE, false, "044"),
    FERMO("FM", "Fermo", Region.MARCHE, false, "109"),
    MACERATA("MC", "Macerata", Region.MARCHE, false, "043"),
    PESARO_E_URBINO("PU", "Pesaro e Urbino", Region.MARCHE, false, "041"),

    CAMPOBASSO("CB", "Campobasso", Region.MOLISE, true, "070"),
    ISERNIA("IS", "Isernia", Region.MOLISE, false, "094"),

    ALESSANDRIA("AL", "Alessandria", Region.PIEMONTE, false, "006"),
    ASTI("AT", "Asti", Region.PIEMONTE, false, "005"),
    BIELLA("BI", "Biella", Region.PIEMONTE, false, "096"),
    CUNEO("CN", "Cuneo", Region.PIEMONTE, false, "004"),
    NOVARA("NO", "Novara", Region.PIEMONTE, false, "003"),
    TORINO("TO", "Torino", Region.PIEMONTE, true, "001"),
    VERBANO_CUSIO_OSSOLA("VB", "Verbano-Cusio-Ossola", Region.PIEMONTE, false, "103"),
    VERCELLI("VC", "Vercelli", Region.PIEMONTE, false, "002"),

    BARI("BA", "Bari", Region.PUGLIA, true, "072"),
    BARLETTA_ANDRIA_TRANI("BT", "Barletta-Andria-Trani", Region.PUGLIA, false, "110"),
    BRINDISI("BR", "Brindisi", Region.PUGLIA, false, "074"),
    FOGGIA("FG", "Foggia", Region.PUGLIA, false, "071"),
    LECCE("LE", "Lecce", Region.PUGLIA, false, "075"),
    TARANTO("TA", "Taranto", Region.PUGLIA, false, "073"),

    CAGLIARI("CA", "Cagliari", Region.SARDEGNA, true, "318"),
    SULCIS_IGLESIENTE("CI", "Sulcis Iglesiente", Region.SARDEGNA, false, "119"),
    MEDIO_CAMPIDANO("VS", "Medio Campidano", Region.SARDEGNA, false, "117"),
    NUORO("NU", "Nuoro", Region.SARDEGNA, false, "114"),
    OGLIASTRA("OG", "Ogliastra", Region.SARDEGNA, false, "116"),
    GALLURA_NORD_EST_SARDEGNA("OT", "Gallura Nord-Est Sardegna", Region.SARDEGNA, false, "113"),
    ORISTANO("OR", "Oristano", Region.SARDEGNA, false, "115"),
    SASSARI("SS", "Sassari", Region.SARDEGNA, false, "312"),

    AGRIGENTO("AG", "Agrigento", Region.SICILIA, false, "084"),
    CALTANISSETTA("CL", "Caltanissetta", Region.SICILIA, false, "085"),
    CATANIA("CT", "Catania", Region.SICILIA, false, "087"),
    ENNA("EN", "Enna", Region.SICILIA, false, "086"),
    MESSINA("ME", "Messina", Region.SICILIA, false, "083"),
    PALERMO("PA", "Palermo", Region.SICILIA, true, "082"),
    RAGUSA("RG", "Ragusa", Region.SICILIA, false, "088"),
    SIRACUSA("SR", "Siracusa", Region.SICILIA, false, "089"),
    TRAPANI("TP", "Trapani", Region.SICILIA, false, "081"),

    AREZZO("AR", "Arezzo", Region.TOSCANA, false, "051"),
    FIRENZE("FI", "Firenze", Region.TOSCANA, true, "048"),
    GROSSETO("GR", "Grosseto", Region.TOSCANA, false, "053"),
    LIVORNO("LI", "Livorno", Region.TOSCANA, false, "049"),
    LUCCA("LU", "Lucca", Region.TOSCANA, false, "046"),
    MASSA_CARRARA("MS", "Massa-Carrara", Region.TOSCANA, false, "045"),
    PISA("PI", "Pisa", Region.TOSCANA, false, "050"),
    PISTOIA("PT", "Pistoia", Region.TOSCANA, false, "047"),
    PRATO("PO", "Prato", Region.TOSCANA, false, "100"),
    SIENA("SI", "Siena", Region.TOSCANA, false, "052"),

    BOLZANO("BZ", "Bolzano", Region.TRENTINO_ALTO_ADIGE, false, "021"),
    TRENTO("TN", "Trento", Region.TRENTINO_ALTO_ADIGE, true, "022"),

    PERUGIA("PG", "Perugia", Region.UMBRIA, true, "054"),
    TERNI("TR", "Terni", Region.UMBRIA, false, "055"),

    AOSTA("AO", "Aosta", Region.VALLE_D_AOSTA, true, "007"),

    BELLUNO("BL", "Belluno", Region.VENETO, false, "025"),
    PADOVA("PD", "Padova", Region.VENETO, false, "028"),
    ROVIGO("RO", "Rovigo", Region.VENETO, false, "029"),
    TREVISO("TV", "Treviso", Region.VENETO, false, "026"),
    VENEZIA("VE", "Venezia", Region.VENETO, true, "027"),
    VERONA("VR", "Verona", Region.VENETO, false, "023"),
    VICENZA("VI", "Vicenza", Region.VENETO, false, "024");

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