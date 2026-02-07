package com.sigeosrl.italyutils.classes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.sigeosrl.italyutils.classes.constants.Province
import dev.tommasop1804.kutils.*
import dev.tommasop1804.kutils.exceptions.MalformedInputException
import dev.tommasop1804.kutils.exceptions.ValidationFailedException
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.node.ObjectNode
import java.time.LocalDate

/**
 * Represents an Italian Driver's Licence (Patente di Guida) with all the data fields
 * present on the physical card (EU format, post-2013 model).
 *
 * The licence number follows the format: 1 uppercase letter + 1 digit + 7 alphanumeric characters + 1 uppercase letter
 * (e.g., U1T86I309C). The first two characters identify the security printer (currently "U1" for IPZS),
 * followed by a 7-character alphanumeric sequential code, and a check letter.
 * Validation is performed upon construction to ensure the number matches the expected pattern.
 *
 * @property number The licence number.
 * @property surname The surname of the licence holder.
 * @property name The name of the licence holder.
 * @property birthDate The date of birth of the licence holder.
 * @property birthPlace The place of birth of the licence holder (may be a foreign location).
 * @property issueDate The date when the licence was issued.
 * @property expiryDate The date when the licence expires.
 * @property issuingAuthority The authority that issued the licence (e.g., "MCTC", "UCO").
 * @property categories The set of driving categories on this licence.
 * @property codes The set of additional codes associated with the licence.
 * @since 2026-02
 * @author Tommaso Pastorelli
 */
@Suppress("unused")
@JsonSerialize(using = DriverLicence.Companion.Serializer::class)
@JsonDeserialize(using = DriverLicence.Companion.Deserializer::class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = DriverLicence.Companion.OldSerializer::class)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = DriverLicence.Companion.OldDeserializer::class)
data class DriverLicence(
    val number: String,
    val surname: String,
    val name: String,
    val birthDate: LocalDate,
    val birthPlace: String,
    val issueDate: LocalDate,
    val expiryDate: LocalDate,
    val issuingAuthority: String,
    val categories: Set<OwnedCategory>,
    val codes: StringSet = emptySet()
) {
    /**
     * Indicates whether the licence has expired based on the current date.
     *
     * @return `true` if the [expiryDate] is before today's date, `false` otherwise.
     * @since 2026-02
     */
    val isExpired: Boolean
        get() = expiryDate.isBefore(LocalDate())

    /**
     * Retrieves the [Municipality] associated with the birth place, if available.
     * This will return `null` for foreign birth places.
     *
     * @return the [Municipality] corresponding to [birthPlace], or `null` if not found.
     * @since 2026-02
     */
    val birthMunicipality: Municipality?
        get() = Municipality ofDenomination birthPlace.substringBefore('(').trim()

    init {
        LICENCE_NUMBER_REGEX(number) || throw MalformedInputException(
            "The string is not a valid driver's licence number."
        )
        if (!(2(number) == "U1" || Province.entries.map(Province::code).any { it == 2(number) }))
            log(LogLevel.WARN, "The first two letter is not a current registered province. Check manually validity of driver licence's number.")

        categories.isNotEmpty() || throw MalformedInputException(
            "At least one driving category must be specified"
        )
        categories.map(OwnedCategory::category).distinct().size == categories.size || throw ValidationFailedException("Duplicate driving categories found")
        categories.forEach { it.expiryDate.expect(it.computeExpiration(issueDate, birthDate), ::expiryDate) }
        expiryDate.expect(computeExpiration(issueDate, birthDate, categories), ::expiryDate)

        codes.validate(::codes) { isEmpty() || any { it.length == 2 && it.isNumeric } }
    }

    companion object {
        /**
         * Regular expression pattern that validates the format of an Italian driver's licence number.
         * The licence number must conform to the pattern of 1 uppercase letter, 1 digit (or letter for compatibility),
         * 7 alphanumeric characters, and 1 uppercase letter (check character).
         *
         * @since 2026-02
         */
        val LICENCE_NUMBER_REGEX = Regex("^[A-Z][0-9A-Z][0-9A-Z]{7}[A-Z]$")

        /**
         * Validates whether the given string is a valid driver's licence number.
         *
         * @receiver The string to validate.
         * @return `true` if the string matches the licence number format, `false` otherwise.
         * @since 2026-02
         */
        @JvmStatic
        fun String.isValidItalianDriverLicenceNumber() = LICENCE_NUMBER_REGEX(uppercase())

        /**
         * Computes the expiration date for a driver's licence based on the provided issue date,
         * birth date, and the set of categories held.
         *
         * The expiration is the earliest among all category-specific expirations.
         * The expiration date is aligned to the individual's birthday (regola del compleanno,
         * in force since September 17, 2012).
         *
         * @param issueDate The date the licence was issued.
         * @param birthDate The birth date of the individual.
         * @param categories The set of driving categories held.
         * @return The calculated expiration date of the licence.
         * @since 2026-02
         */
        @JvmStatic
        fun computeExpiration(issueDate: LocalDate, birthDate: LocalDate, categories: Set<OwnedCategory>): LocalDate =
            categories.minOf { println(it.computeExpiration(issueDate, birthDate)); it.computeExpiration(issueDate, birthDate) }

        class Serializer : ValueSerializer<DriverLicence>() {
            override fun serialize(value: DriverLicence, gen: tools.jackson.core.JsonGenerator, ctxt: SerializationContext) {
                gen.writeStartObject()
                gen.writeStringProperty("number", value.number)
                gen.writeStringProperty("surname", value.surname)
                gen.writeStringProperty("name", value.name)
                gen.writeStringProperty("birthDate", value.birthDate.toString())
                gen.writeStringProperty("birthPlace", value.birthPlace)
                gen.writeStringProperty("issueDate", value.issueDate.toString())
                gen.writeStringProperty("expiryDate", value.expiryDate.toString())
                gen.writeStringProperty("issuingAuthority", value.issuingAuthority)
                gen.writeArrayPropertyStart("categories")
                for (cat in value.categories)
                    gen.writePOJO(cat)
                gen.writeEndArray()
                gen.writeEndObject()
            }
        }

        class Deserializer : ValueDeserializer<DriverLicence>() {
            override fun deserialize(p: tools.jackson.core.JsonParser, ctxt: DeserializationContext): DriverLicence {
                val node = p.objectReadContext().readTree<ObjectNode>(p)
                return DriverLicence(
                    number = node["number"].asString(),
                    surname = node["surname"].asString(),
                    name = node["name"].asString(),
                    birthDate = LocalDate(node["birthDate"].asString())(),
                    birthPlace = node["birthPlace"].asString(),
                    issueDate = LocalDate(node["issueDate"].asString())(),
                    expiryDate = LocalDate(node["expiryDate"].asString())(),
                    issuingAuthority = node["issuingAuthority"].asString(),
                    categories = buildSet {
                        val arr = node["categories"]
                        for (i in 0 until arr.size()) {
                            add(arr[i].asString().deserialize<OwnedCategory>()())
                        }
                    }
                )
            }
        }

        class OldSerializer : JsonSerializer<DriverLicence>() {
            override fun serialize(value: DriverLicence, gen: JsonGenerator, serializers: SerializerProvider) {
                gen.writeStartObject()
                gen.writeStringField("number", value.number)
                gen.writeStringField("surname", value.surname)
                gen.writeStringField("name", value.name)
                gen.writeStringField("birthDate", value.birthDate.toString())
                gen.writeStringField("birthPlace", value.birthPlace)
                gen.writeStringField("issueDate", value.issueDate.toString())
                gen.writeStringField("expiryDate", value.expiryDate.toString())
                gen.writeStringField("issuingAuthority", value.issuingAuthority)
                gen.writeArrayFieldStart("categories")
                for (cat in value.categories) {
                    gen.writePOJO(cat)
                }
                gen.writeEndArray()
                gen.writeEndObject()
            }
        }

        class OldDeserializer : JsonDeserializer<DriverLicence>() {
            override fun deserialize(p: JsonParser, ctxt: com.fasterxml.jackson.databind.DeserializationContext): DriverLicence {
                val node = p.codec.readTree<com.fasterxml.jackson.databind.node.ObjectNode>(p)
                return DriverLicence(
                    number = node["number"].asText(),
                    surname = node["surname"].asText(),
                    name = node["name"].asText(),
                    birthDate = LocalDate(node["birthDate"].asText())(),
                    birthPlace = node["birthPlace"].asText(),
                    issueDate = LocalDate(node["issueDate"].asText())(),
                    expiryDate = LocalDate(node["expiryDate"].asText())(),
                    issuingAuthority = node["issuingAuthority"].asText(),
                    categories = buildSet {
                        val arr = node["categories"]
                        for (i in 0 until arr.size()) {
                            add(arr[i].asText().deserialize<OwnedCategory>()())
                        }
                    }
                )
            }
        }
    }

    /**
     * Represents the driving categories available on Italian driving licences
     * as defined by EU Directive 2006/126/EC.
     *
     * @property validityGroup The validity group that determines the expiration rules for this category.
     * @since 2026-02
     * @author Tommaso Pastorelli
     */
    enum class Category(val validityGroup: ValidityGroup) {
        /** Mopeds (ciclomotori) - max 45 km/h, max 50cc or 4kW
         * @since 2026-02
         */
        AM(ValidityGroup.STANDARD),
        /** Light motorcycles - max 125cc, max 11kW
         * @since 2026-02
         */
        A1(ValidityGroup.STANDARD),
        /** Medium motorcycles - max 35kW
         * @since 2026-02
         */
        A2(ValidityGroup.STANDARD),
        /** All motorcycles
         * @since 2026-02
         */
        A(ValidityGroup.STANDARD),
        /** Light quadricycles - max 400kg, max 15kW
         * @since 2026-02
         */
        B1(ValidityGroup.STANDARD),
        /** Motor vehicles - max 3500kg, max 8+1 passengers
         * @since 2026-02
         */
        B(ValidityGroup.STANDARD),
        /** Motor vehicles (B) with trailer - over 750kg
         * @since 2026-02
         0*/
        BE(ValidityGroup.STANDARD),
        /** Medium goods vehicles - 3500-7500kg
         * @since 2026-02
         */
        C1(ValidityGroup.PROFESSIONAL_C),
        /** Medium goods vehicles (C1) with trailer
         * @since 2026-02
         */
        C1E(ValidityGroup.PROFESSIONAL_C),
        /** Large goods vehicles - over 3500kg
         * @since 2026-02
         */
        C(ValidityGroup.PROFESSIONAL_C),
        /** Large goods vehicles (C) with trailer
         * @since 2026-02
         */
        CE(ValidityGroup.PROFESSIONAL_C),
        /** Minibuses - max 16+1 passengers
         * @since 2026-02
         */
        D1(ValidityGroup.PROFESSIONAL_D),
        /** Minibuses (D1) with trailer
         * @since 2026-02
         */
        D1E(ValidityGroup.PROFESSIONAL_D),
        /** Buses - over 8+1 passengers
         * @since 2026-02
         */
        D(ValidityGroup.PROFESSIONAL_D),
        /** Buses (D) with trailer
         * @since 2026-02
         */
        DE(ValidityGroup.PROFESSIONAL_D);

        companion object {
            /**
             * Finds an entry in the collection whose name matches the provided name.
             *
             * @param name The name of the entry to search for in the collection.
             * @return The entry with the matching name, or `null` if no match is found.
             * @since 2026-02
             */
            @JvmStatic
            infix fun of(name: String) = entries.find { it.name == name }
            /**
             * Finds the first entry in the collection of `Category` instances whose `validityGroup` matches
             * the specified `ValidityGroup`.
             *
             * @param group The `ValidityGroup` to compare against the `validityGroup` property of each entry.
             * @return The first `Category` entry with a matching `validityGroup`, or `null` if no match is found.
             * @since 2026-02
             */
            @JvmStatic
            infix fun byValidityGroup(group: ValidityGroup) = entries.filter { it.validityGroup == group }
        }

        /**
         * Computes the expiration date for a driver's licence based on the provided issue date,
         * birth date, and the set of categories held.
         *
         * The expiration is the earliest among all category-specific expirations.
         * The expiration date is aligned to the individual's birthday (regola del compleanno,
         * in force since September 17, 2012).
         *
         * @param issueDate The date the licence was issued.
         * @param birthDate The birth date of the individual.
         * @return The calculated expiration date of the licence.
         * @since 2026-02
         */
        fun computeExpiration(issueDate: LocalDate, birthDate: LocalDate): LocalDate =
            validityGroup.computeExpiration(issueDate, birthDate)

        /**
         * Defines the validity groups for driving licence categories, each with different
         * renewal periods based on the holder's age.
         *
         * @since 2026-02
         * @author Tommaso Pastorelli
         */
        enum class ValidityGroup {
            /**
             * Standard categories (AM, A1, A2, A, B1, B, BE).
             * - Under 50: 10 years.
             * - 50 to 69: 5 years.
             * - 70 to 79: 3 years.
             * - 80 and older: 2 years.
             * @since 2026-02
             */
            STANDARD,
            /**
             * Professional goods vehicle categories (C1, C1E, C, CE).
             * - Under 65: 5 years.
             * - 65 and older: 2 years.
             * @since 2026-02
             */
            PROFESSIONAL_C,
            /**
             * Professional passenger vehicle categories (D1, D1E, D, DE).
             * - Under 60: 5 years.
             * - 60 and older: 1 year.
             * @since 2026-02
             */
            PROFESSIONAL_D;

            companion object {
                /**
                 * Retrieves the validity group associated with a specific driving licence category.
                 *
                 * @param category The driving licence category from which to derive the validity group.
                 * @return The `ValidityGroup` that defines the expiration rules for the specified category.
                 * @since 2026-02
                 */
                @JvmStatic
                infix fun fromCategory(category: Category) = category.validityGroup
            }

            /**
             * Computes the expiration date for a driver's licence based on the provided issue date,
             * birth date, and a single category.
             *
             * The rules are:
             * - **Standard categories** (AM, A1, A2, A, B1, B, BE):
             *   - Under 50: valid for 10 years.
             *   - 50 to 69: valid for 5 years.
             *   - 70 to 79: valid for 3 years.
             *   - 80 and older: valid for 2 years.
             * - **Professional C categories** (C1, C1E, C, CE):
             *   - Under 65: valid for 5 years.
             *   - 65 and older: valid for 2 years.
             * - **Professional D categories** (D1, D1E, D, DE):
             *   - Under 60: valid for 5 years.
             *   - 60 and older: valid for 1 year.
             *
             * The expiration date is aligned to the individual's birthday after the calculated
             * standard expiration date (regola del compleanno).
             *
             * @param issueDate The date the licence was issued.
             * @param birthDate The birth date of the individual.
             * @param category The driving category.
             * @return The calculated expiration date for the given category.
             * @since 2026-02
             */
            fun computeExpiration(issueDate: LocalDate, birthDate: LocalDate): LocalDate {
                val age = birthDate.until(issueDate, java.time.temporal.ChronoUnit.YEARS)
                val standard = when (this) {
                    STANDARD -> when (age) {
                        in 0..<50 -> issueDate.plusYears(10)
                        in 50..<70 -> issueDate.plusYears(5)
                        in 70..<80 -> issueDate.plusYears(3)
                        else -> issueDate.plusYears(2)
                    }
                    PROFESSIONAL_C -> when (age) {
                        in 0..<65 -> issueDate.plusYears(5)
                        else -> issueDate.plusYears(2)
                    }
                    PROFESSIONAL_D -> when (age) {
                        in 0..<60 -> issueDate.plusYears(5)
                        else -> issueDate.plusYears(1)
                    }
                }
                val birthdayInExpiryYear = LocalDate(standard.year, birthDate.month, birthDate.dayOfMonth)

                return if (!birthdayInExpiryYear.isBefore(standard)) birthdayInExpiryYear
                else birthdayInExpiryYear.plusYears(1)
            }
        }
    }

    /**
     * Represents a specific driving category owned by an individual, along with its associated details
     * such as issue date and expiry date. Maintains validation for the correct calculation of expiry
     * based on the issued category and individual's birthdate.
     *
     * @property category The driving category associated with this entry.
     * @property issueDate The date on which the driving category was issued.
     * @property expiryDate The date on which the driving category expires. It is calculated and
     * validated to align with the category-specific rules and the individual's birth date.
     * @since 2026-02
     */
    @Suppress("unused")
    data class OwnedCategory(
        val category: Category,
        val issueDate: LocalDate,
        val expiryDate: LocalDate
    ) {
        /**
         * Computes the expiration date for a driver's licence based on the provided issue date,
         * birth date, and the set of categories held.
         *
         * The expiration is the earliest among all category-specific expirations.
         * The expiration date is aligned to the individual's birthday (regola del compleanno,
         * in force since September 17, 2012).
         *
         * @param issueDate The date the licence was issued.
         * @param birthDate The birth date of the individual.
         * @param category The set of driving categories held.
         * @return The calculated expiration date of the licence.
         * @since 2026-02
         */
        fun computeExpiration(issueDate: LocalDate, birthDate: LocalDate): LocalDate =
            category.computeExpiration(issueDate, birthDate)
    }
}
