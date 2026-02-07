package com.sigeosrl.italyutils.classes

import com.sigeosrl.italyutils.classes.constants.GeographicDistribution
import com.sigeosrl.italyutils.classes.constants.Province
import com.sigeosrl.italyutils.classes.constants.Region
import dev.tommasop1804.kutils.*
import dev.tommasop1804.kutils.classes.coding.JSON
import dev.tommasop1804.kutils.classes.geography.GeoCoordinate
import dev.tommasop1804.kutils.classes.measure.MeasureUnit
import dev.tommasop1804.kutils.classes.measure.RMeasurement
import dev.tommasop1804.kutils.classes.measure.RMeasurement.Companion.ofUnit
import dev.tommasop1804.kutils.classes.web.HttpMethod
import dev.tommasop1804.kutils.exceptions.HttpRequestException
import dev.tommasop1804.kutils.exceptions.HttpResponseException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.NoSuchFileException
import java.time.Year

/**
 * Represents a municipality with various geographical, administrative, and population attributes.
 *
 * @property region The geographical region where the municipality is located.
 * @property supraMunicipalTerritorialUnitCode The code of the supra-municipal territorial unit.
 * @property supraMunicipalTerritorialType The type of supra-municipal territorial unit.
 * @property storicProvinceCode The code of the historical province.
 * @property province The province to which the municipality belongs.
 * @property progressiveCode A progressive identification code for the municipality.
 * @property alphanumericCode An alphanumeric ISTAT code for the municipality.
 * @property numericCode A numeric ISTAT code for the municipality.
 * @property denomination The denomination of the municipality.
 * @property italianDenomination The official denomination of the municipality in Italian.
 * @property otherLangDenomination The denomination of the municipality in other languages, if applicable.
 * @property geographicalDistributionCode The code representing the geographical distribution of the municipality.
 * @property geographicalDistribution The geographical distribution type of the municipality.
 * @property isCapitalOrMetropolitanOrFreeConsortium Indicates if the municipality is a capital, metropolitan, or part of a free consortium.
 * @property isIslandMunicipality Indicates if the municipality is located on an island.
 * @property isCoastalMunicipality Indicates if the municipality is a coastal location.
 * @property hasCoastalAreas Indicates if the municipality has coastal areas.
 * @property automobilisticCode The automobile code associated with the municipality.
 * @property cadastralCode The cadastral code of the municipality.
 * @property legalPopulation The legal population count of the municipality.
 * @property territorialSurface The territorial surface area of the municipality.
 * @property residentialPopulation The residential population of the municipality.
 * @property altimetricZone The altimetric zone classification of the municipality.
 * @property altitude The altitude of the municipality in meters.
 * @property degurba2011 The DEGURBA classification code from 2011 for the municipality.
 * @property ecoregionDivisionCode The code of the ecoregion division the municipality belongs to.
 * @property ecoregionDivision The ecoregion division of the municipality.
 * @property ecoregionProvinceCode The code of the ecoregion province the municipality belongs to.
 * @property ecoregionProvince The ecoregion province of the municipality.
 * @property ecoregionSectionCode The code of the ecoregion section the municipality belongs to.
 * @property ecoregionSection The ecoregion section of the municipality.
 * @property ecoregionSubsectionCode The code of the ecoregion subsection the municipality belongs to.
 * @property ecoregionSubsection The ecoregion subsection of the municipality.
 * @property postalCodes A list of postal codes associated with the municipality.
 * @property position The geographical coordinates of the municipality.
 * @property props Additional properties related to the municipality.
 * @since 2026-02
 * @author Tommaso Pastorelli
 */
@ConsistentCopyVisibility
@Suppress("unused")
data class Municipality private constructor(
    val region: Region,
    val supraMunicipalTerritorialUnitCode: String,
    val supraMunicipalTerritorialType: String,
    val storicProvinceCode: String,
    val province: Province,
    val progressiveCode: String,
    val alphanumericCode: String,
    val numericCode: String,
    val denomination: String,
    val italianDenomination: String,
    val otherLangDenomination: String? = null,
    val geographicalDistributionCode: String,
    val geographicalDistribution: GeographicDistribution,
    val isCapitalOrMetropolitanOrFreeConsortium: Boolean,
    val isIslandMunicipality: Boolean,
    val isCoastalMunicipality: Boolean,
    val hasCoastalAreas: Boolean,
    val automobilisticCode: String,
    val cadastralCode: String,
    val legalPopulation: Pair<Year, Long>,
    val territorialSurface: Pair<Year, RMeasurement<MeasureUnit.AreaUnit>>,
    val residentialPopulation: Pair<Year, Long>,
    val altimetricZone: Int,
    val altitude: RMeasurement<MeasureUnit.LengthUnit>? = null,
    val degurba2011: Int,
    val ecoregionDivisionCode: String,
    val ecoregionDivision: String,
    val ecoregionProvinceCode: String,
    val ecoregionProvince: String,
    val ecoregionSectionCode: String,
    val ecoregionSection: String,
    val ecoregionSubsectionCode: String,
    val ecoregionSubsection: String,
    val postalCodes: StringList? = null,
    val position: GeoCoordinate? = null,
    val props: DataMap = emptyMap()
) {
    companion object {
        /**
         * Provides a list of all municipalities in Italy by parsing the resource csv directly taken from ISTAT.
         *
         *
         * 7896 entries.
         *
         * The resource file is expected to contain detailed information about municipalities in a CSV format
         * with various fields including codes, names, geographical distribution, and other metadata.
         * This property lazily parses the CSV and constructs a list of `Municipality` objects.
         *
         * @throws NoSuchFileException if the resource file is not found.
         * @since 2026-02
         */
        @JvmStatic
        val LIST: List<Municipality>
            get() {
                val changeList = CHANGE_LIST.groupBy { it["Codice comune precedente"] ?: String.EMPTY }.mapValues { it.value.first() }
                val municipalities = emptyMList<Municipality>()

                val dimensionsList = getListFromCSV("dimensioni.csv")
                val dimensionsMap = dimensionsList.groupBy { it["Codice Comune alfanumerico"] ?: String.EMPTY }.mapValues { it.value.first() }

                val caracteristicList = getListFromCSV("caratteristiche.csv")
                val caracteristicMap = caracteristicList.groupBy { it["Codice Comune alfanumerico"] ?: String.EMPTY }.mapValues { it.value.first() }

                val capCsvList = getListFromCSV("gi_comuni_cap.csv")
                val capCsvMap = capCsvList.groupBy { it["codice_istat"] ?: String.EMPTY }

                val inputStream = this::class.java.classLoader.getResourceAsStream("elenco-comuni-italiani.csv")
                    ?: throw NoSuchFileException("Resource not found: elenco-comuni-italiani.csv")
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.ISO_8859_1)).use { reader ->
                    val header = reader.readLine().split(Char.SEMICOLON).map { it.trim(Char.QUOTATION_MARK, Char.SPACE) }
                    reader.lineSequence().forEach { line ->
                        if (line.isBlank()) return@forEach

                        val cols = line.split(Char.SEMICOLON).map { it.trim(Char.QUOTATION_MARK, Char.SPACE) }
                        val row = header.zip(cols).toMap()

                        val alphanumericCode = row["Codice Comune formato alfanumerico"]!!
                        val newData = changeList[alphanumericCode]
                        val dimensionEntry = dimensionsMap[newData?.get("Codice comune") ?: alphanumericCode]!!
                        val caracteristicEntry = caracteristicMap[newData?.get("Codice comune") ?: alphanumericCode]!!
                        val capCsvEntry = capCsvMap[if (newData.isNullOrEmpty()) row["Codice Comune formato alfanumerico"]!! else newData["Codice comune"]!!]
                        municipalities += Municipality(
                            region = Region.ofIstatCode(row["Codice Regione"]!!)!!,
                            supraMunicipalTerritorialUnitCode = row["Codice dell'Unita territoriale sovracomunale valida a fini statistici"]!!,
                            supraMunicipalTerritorialType = row["Tipologia di Unita territoriale sovracomunale"]!!,
                            storicProvinceCode = row["Codice Provincia Storico 1"]!!,
                            province = Province.ofIstatCode(if (newData.isNullOrEmpty()) row["Codice Provincia Storico 1"]!! else newData["Codice provincia citta metropolitana"]!!)!!,
                            progressiveCode = row["Progressivo del Comune 2"]!!,
                            alphanumericCode = if (newData.isNullOrEmpty()) row["Codice Comune formato alfanumerico"]!! else newData["Codice comune"]!!,
                            numericCode = row["Codice Comune formato numerico"]!!,
                            denomination = row["Denominazione Italiana e straniera"]!!,
                            italianDenomination = row["Denominazione in italiano"]!!,
                            otherLangDenomination = row["Denominazione altra lingua"].takeUnless { it.isNullOrBlank() },
                            geographicalDistributionCode = row["Codice Ripartizione Geografica"]!!,
                            geographicalDistribution = GeographicDistribution.ofCode(row["Codice Ripartizione Geografica"]!!)!!,
                            isCapitalOrMetropolitanOrFreeConsortium = (row["Flag Comune capoluogo di provincia citta metropolitana libero consorzio"]!!) == "1",
                            isIslandMunicipality = caracteristicEntry["Comune isolano"] == "1",
                            isCoastalMunicipality = caracteristicEntry["Comune litoraneo"] == "1",
                            hasCoastalAreas = caracteristicEntry["Zone costiere"] == "1",
                            automobilisticCode = row["Sigla automobilistica"]!!,
                            cadastralCode = row["Codice Catastale del comune"]!!,
                            legalPopulation = Year.of(dimensionEntry["Anno Censimento"]!!.toInt()) to dimensionEntry["Popolazione legale"]!!.toLong(),
                            territorialSurface = Year.of(dimensionEntry["Anno Superficie territoriale"]!!.toInt()) to (dimensionEntry["Superficie territoriale Kmq"]!!.replace(Char.COMMA, Char.DOT).toDouble() ofUnit MeasureUnit.AreaUnit.SQUARE_KILOMETER),
                            residentialPopulation = Year.of(dimensionEntry["Anno Popolazione residente"]!!.toInt()) to (dimensionEntry["Popolazione residente"]!!.toLong()),
                            altimetricZone = caracteristicEntry["Zona altimetrica"]!!.toInt(),
                            altitude = caracteristicEntry["Altitudine metri"]!!.replace(String.DOT, String.EMPTY).run {
                                if (isEmpty()) null else (toDouble() ofUnit MeasureUnit.LengthUnit.METER)
                            },
                            degurba2011 = caracteristicEntry["Degurba 2011"]!!.toInt(),
                            ecoregionDivisionCode = caracteristicEntry["Codice Ecoregioni Divisioni"]!!,
                            ecoregionDivision = caracteristicEntry["Ecoregioni Divisioni"]!!,
                            ecoregionProvinceCode = caracteristicEntry["Codice Ecoregioni Province"]!!,
                            ecoregionProvince = caracteristicEntry["Ecoregioni Province"]!!,
                            ecoregionSectionCode = caracteristicEntry["Codice Ecoregioni Sezioni"]!!,
                            ecoregionSection = caracteristicEntry["Ecoregioni Sezioni"]!!,
                            ecoregionSubsectionCode = caracteristicEntry["Codice Ecoregioni Sottosezioni"]!!,
                            ecoregionSubsection = caracteristicEntry["Ecoregioni Sottosezioni"]!!,
                            postalCodes = capCsvEntry?.map { it["cap"]!! },
                            position = capCsvEntry?.first()?.run { GeoCoordinate(get("lat")!!.replace(Char.COMMA, Char.DOT).toDouble(), get("lon")!!.replace(Char.COMMA, Char.DOT).toDouble()) },
                            props = if (newData.isNullOrEmpty()) emptyMap() else mapOf(
                                "oldAlphanumericCode" to newData["Codice comune precedente"]!!,
                                "oldProvinceCode" to newData["Codice provincia citta metropolitana precedente"]!!,
                                "oldProvinceName" to newData["Denominazione provincia citta metropolinata precedente"]!!.after("Provincia di ").after("Provincia del "),
                            )
                        )
                    }
                }
                return municipalities
            }

        /**
         * A list containing the next change of the dataset.
         * Valid from 2026-01-01T00:00:00+01:00.
         *
         * @since 2026-02
         */
        @JvmStatic
        val CHANGE_LIST: List<StringMap>
            get() {
                val list = emptyMList<StringMap>()
                val inputStream = this::class.java.classLoader.getResourceAsStream("change.csv")
                    ?: throw NoSuchFileException("Resource not found: change.csv")

                BufferedReader(InputStreamReader(inputStream, StandardCharsets.ISO_8859_1)).use { reader ->
                    val header = reader.readLine().split(Char.SEMICOLON).map { it.trim(Char.QUOTATION_MARK, Char.SPACE) }

                    reader.lineSequence().forEach { line ->
                        if (line.isBlank()) return@forEach

                        val cols = line.split(Char.SEMICOLON).map { it.trim(Char.QUOTATION_MARK, Char.SPACE) }
                        list += header.zip(cols).toMap()
                    }
                }
                return list
            }

        /**
         * Provides a filtered list of municipalities that are classified as capitals, metropolitan cities, or free consortia.
         *
         * This property accesses the primary dataset and filters out only the municipalities
         * meeting the specified classification criteria by checking the `isCapitalOrMetropolitanOrFreeConsortium` property.
         *
         * @return A list of municipalities that are either capitals, part of a metropolitan area, or free consortia.
         * @since 2026-02
         */
        val CAPITALS_OR_METROPOLITAN_OR_FREE_CONSORTIUM: List<Municipality>
            get() = LIST.filter { it.isCapitalOrMetropolitanOrFreeConsortium }

        /**
         * Finds an item in the `LIST` with a matching name, either in the Italian denomination
         * or the other language denomination if available.
         *
         * @param name the name to search for in the list.
         * @return the matching item from the list, or `null` if no match is found.
         * @since 2026-02
         */
        @JvmStatic
        infix fun ofDenomination(name: String) =
            LIST.find { it.italianDenomination equalsIgnoreCase name } ?: LIST.find { it.otherLangDenomination?.equalsIgnoreCase(name) ?: false }

        /**
         * Finds a municipality by its progressive identifier.
         *
         * @param code The progressive identifier of the municipality to search for.
         * @return The municipality with the specified progressive identifier, or null if not found.
         * @since 2026-02
         */
        @JvmStatic
        infix fun ofProgressiveCode(code: String) = LIST.find { it.progressiveCode == code }
        /**
         * Finds a municipality based on the given progressive number.
         *
         * @param code the progressive number to search for
         * @return the municipality object with a matching progressive number, or null if not found
         * @since 2026-02
         */
        @JvmStatic
        infix fun ofProgressiveCode(code: Number) = LIST.find { it.progressiveCode.toInt() == code.toInt() }

        /**
         * Finds and returns the first element in the list that matches the provided alphanumeric code,
         * ignoring case sensitivity.
         *
         * @param code The alphanumeric code to match against the elements in the list.
         * @since 2026-02
         */
        @JvmStatic
        infix fun ofAlphanumericCode(code: String) = LIST.find { it.alphanumericCode equalsIgnoreCase code }

        /**
         * Finds and returns an element from the LIST collection where the numericCode matches the given code.
         *
         * @param code The numeric code to search for in the LIST collection.
         * @return The first element in the LIST collection with a matching numericCode, or null if no match is found.
         * @since 2026-02
         */
        @JvmStatic
        infix fun ofNumericCode(code: String) = LIST.find { it.numericCode.toInt() == code.toInt() }
        /**
         * Searches for and retrieves an entry from the LIST based on the provided numeric code.
         *
         * @param code the numeric code to search for within the LIST.
         * @since 2026-02
         */
        @JvmStatic
        infix fun ofNumericCode(code: Number) = LIST.find { it.numericCode.toInt() == code.toInt() }

        /**
         * Searches for a municipality within the list based on its cadastral code.
         *
         * @param code The cadastral code used for the search.
         * @return The municipality with the matching cadastral code, or null if no match is found.
         * @since 2026-02
         */
        @JvmStatic
        infix fun ofCadastralCode(code: String) = LIST.find { it.cadastralCode equalsIgnoreCase code}

        /**
         * Finds a municipality that contains the specified postal code.
         *
         * @param code the postal code to search for within the municipalities' postal codes
         * @return the municipality that contains the postal code, or null if no such municipality exists
         * @since 2026-02
         */
        @JvmStatic
        infix fun ofPostalCode(code: String) = LIST.find { it.postalCodes?.contains(code) ?: false }

        /**
         * Finds the first element in a predefined list of municipalities that matches the specified geographical coordinate.
         *
         * @param coordinates The geographical coordinates to match against the position of municipalities in the list.
         * @since 2026-02
         */
        @JvmStatic
        infix fun ofPosition(coordinates: GeoCoordinate) = LIST.find { it.position?.equals(coordinates) ?: false }
        /**
         * Finds and returns an item in the list that matches the given geographical coordinates,
         * if available, by comparing its position with the specified latitude and longitude.
         *
         * @param latitude The latitude of the desired geographical position.
         * @param longitude The longitude of the desired geographical position.
         * @return The matched item from the list based on the position, or null if no match is found.
         * @since 2026-02
         */
        @JvmStatic
        fun ofPosition(latitude: Double, longitude: Double) = LIST.find { it.position?.equals(GeoCoordinate(latitude, longitude)) ?: false }

        /**
         * Filters the list of municipalities to include only those belonging to the specified region.
         *
         * @param region The region used as criteria to filter municipalities.
         * @since 2026-02
         */
        @JvmStatic
        infix fun byRegion(region: Region) = LIST.filter { it.region == region }

        /**
         * Filters a list of municipalities by the given supra-municipal territorial unit code.
         *
         * @param code The supra-municipal territorial unit code used as the filtering criterion.
         * @return A list of municipalities matching the specified supra-municipal territorial unit code.
         * @since 2026-02
         */
        @JvmStatic
        infix fun bySupraMunicipalTerritorialUnitcode(code: String) = LIST.filter { it.supraMunicipalTerritorialUnitCode == code }
        /**
         * Filters the list of municipalities by the specified supra-municipal territorial unit code.
         *
         * @param code The supra-municipal territorial unit code to filter municipalities by. Must be a number.
         * @since 2026-02
         */
        @JvmStatic
        infix fun bySupraMunicipalTerritorialUnitcode(code: Number) = LIST.filter { it.supraMunicipalTerritorialUnitCode.toInt() == code.toInt() }

        /**
         * Filters the list of municipalities by the specified province.
         *
         * @param province The province to filter the municipalities by.
         * @since 2026-02
         */
        @JvmStatic
        infix fun byProvince(province: Province) = LIST.filter { it.province == province }
        /**
         * Filters the list of municipalities by the given province code.
         *
         * @param provinceCode The numeric code of the province to filter municipalities by.
         * @return A new filtered list containing only the municipalities that belong to the specified province.
         * @since 2026-02
         */
        @JvmStatic
        infix fun byProvince(provinceCode: Number) = LIST.filter { it.storicProvinceCode.toInt() == provinceCode.toInt() }

        /**
         * Filters a predefined list of entities based on the specified geographical distribution.
         *
         * @param geographicalDistribution The geographical distribution to filter the entities by.
         * @since 2026-02
         */
        @JvmStatic
        infix fun byGeographicalDistribution(geographicalDistribution: GeographicDistribution) =
            LIST.filter { it.geographicalDistribution == geographicalDistribution }
        /**
         * Filters a list of municipalities based on the provided geographical distribution code.
         *
         * @param code The geographical distribution code to filter the municipalities by.
         * @return A list of municipalities matching the given geographical distribution code.
         * @since 2026-02
         */
        @JvmStatic
        infix fun byGeographicalDistributionCode(code: String) = LIST.filter { it.geographicalDistributionCode == code }
        /**
         * Filters a list of items by matching the geographical distribution code.
         *
         * @param code The numerical code representing the geographical distribution to filter by.
         * @return A filtered list containing items whose geographical distribution code matches the provided value.
         * @since 2026-02
         */
        @JvmStatic
        infix fun byGeographicalDistributionCode(code: Number) = LIST.filter { it.geographicalDistributionCode.toInt() == code.toInt() }

        /**
         * Filters a list of municipalities by the specified automobilistic code.
         *
         * @param code the automobilistic code to filter municipalities by
         * @return a list of municipalities matching the specified automobilistic code
         * @since 2026-02
         */
        @JvmStatic
        infix fun byAutomobilisticCode(code: String) = LIST.filter { it.automobilisticCode == code }

        /**
         * Attempts to retrieve a Municipality instance based on the geographic coordinates provided.
         * The method uses the Nominatim OpenStreetMap API to fetch location-related data and then
         * tries to match the municipality either by postal code or the town name (denomination).
         *
         * @param coordinates the geographic coordinates used to determine the municipality.
         * @throws HttpRequestException if there's an issue with the HTTP request.
         * @throws HttpResponseException if the HTTP response status code indicates an error.
         * @return the Municipality instance matching the given geographic coordinates,
         *         or null if no matching municipality is found.
         * @since 2026-02
         */
        @JvmStatic
        infix fun fromCoordinates(coordinates: GeoCoordinate): Municipality? {
            val url = String.format(
                "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=%f&lon=%f&zoom=10&addressdetails=1",
                coordinates.latitude,
                coordinates.longitude
            )

            val uri = URI.create(url)
            val client: HttpClient = HttpClient.newHttpClient()
            val request = tryOrThrow({ e: Throwable -> HttpRequestException(
                500,
                uri,
                HttpMethod.GET,
                e.message,
            ) }) { HttpRequest.newBuilder()
                .uri(uri)
                .header("User-Agent", "SIGEO_srl-Italy-Utils/1.0") // Required by Nominatim
                .build()
            }

            val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() !in 200..299) throw HttpResponseException(
                response.statusCode(),
                uri,
                HttpMethod.GET,
                response.body()
            )
            val json = JSON(response.body())

            return if (json["address"]!!["postalcode"].isNotNull())
                ofPostalCode(json["address"]!!["postcode"].asString())
            else ofDenomination(json["name"]!!.asString()) ?: if (json["address"]!!["town"].isNotNull())
                ofDenomination(json["address"]!!["town"].asString())
            else tryOrNull { ofDenomination(json["address"]!!["village"].asString()) }
        }

        private fun getListFromCSV(csvName: String): MList<StringMap> {
            val inputStream = this::class.java.classLoader.getResourceAsStream(csvName)
                ?: throw NoSuchFileException("Resource not found: $csvName")
            val list = emptyMList<StringMap>()
            BufferedReader(InputStreamReader(inputStream, StandardCharsets.ISO_8859_1)).use { reader ->
                val header = reader.readLine().split(Char.SEMICOLON).map { it.trim(Char.QUOTATION_MARK, Char.SPACE) }
                reader.lineSequence().forEach { line ->
                    if (line.isBlank()) return@forEach

                    val cols = line.split(Char.SEMICOLON).map { it.trim(Char.QUOTATION_MARK, Char.SPACE) }
                    list += header.zip(cols).toMap()
                }
            }
            return list
        }
    }
}