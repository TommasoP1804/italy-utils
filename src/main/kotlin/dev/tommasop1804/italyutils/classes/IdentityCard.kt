package dev.tommasop1804.italyutils.classes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import dev.tommasop1804.kutils.*
import dev.tommasop1804.kutils.classes.constants.Sex
import dev.tommasop1804.kutils.classes.geography.Country
import dev.tommasop1804.kutils.classes.measure.MeasureUnit
import dev.tommasop1804.kutils.classes.registry.Contact
import dev.tommasop1804.kutils.exceptions.ExpectationMismatchException
import dev.tommasop1804.kutils.exceptions.MalformedInputException
import dev.tommasop1804.kutils.invoke
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.node.ObjectNode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Represents an Italian Electronic Identity Card (Carta d'Identità Elettronica - CIE)
 * with all the data fields present on the physical card.
 *
 * The CIE number follows the format: 2 uppercase letters + 5 digits + 2 uppercase letters
 * (e.g., CA12345AA). Validation is performed upon construction to ensure the number
 * matches the expected pattern.
 *
 * @property number The CIE card number (format: [A-Z]{2}[0-9]{5}[A-Z]{2}).
 * @property surname The surname of the card holder.
 * @property name The name of the card holder.
 * @property birthDate The date of birth of the card holder.
 * @property birthPlace The place of birth of the card holder (may be a foreign location).
 * @property sex The biological sex of the card holder.
 * @property height The height of the card holder.
 * @property citizenship The citizenship/nationality of the card holder.
 * @property issueDate The date when the card was issued.
 * @property expiryDate The date when the card expires.
 * @property issuingMunicipality The name of the municipality that issued the card.
 * @property fiscalCode The Italian fiscal code of the card holder.
 * @property residentialAddress The residential address of the card holder.
 * @since 2026-02
 * @author Tommaso Pastorelli
 */
@Suppress("unused")
@JsonSerialize(using = IdentityCard.Companion.Serializer::class)
@JsonDeserialize(using = IdentityCard.Companion.Deserializer::class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = IdentityCard.Companion.OldSerializer::class)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = IdentityCard.Companion.OldDeserializer::class)
data class IdentityCard(
    val number: String,
    val surname: String,
    val name: String,
    val birthDate: LocalDate,
    val birthPlace: String,
    val sex: Sex,
    val height: Length,
    val citizenship: Country,
    val issueDate: LocalDate,
    val expiryDate: LocalDate,
    val issuingMunicipality: String,
    val fiscalCode: FiscalCode,
    val residentialAddress: Contact.Address
) {
    /**
     * Indicates whether the card has expired based on the current date.
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
        get() = Municipality.Companion ofDenomination birthPlace

    init {
        CIE_NUMBER_REGEX(number) || throw MalformedInputException(
            "The string is not a valid CIE number. Expected format: AA00000AA"
        )
        validateExpiration(issueDate, birthDate, expiryDate) || throw ExpectationMismatchException("Check expiration date.")
    }

    companion object {
        /**
         * Regular expression pattern that validates the format of an Italian Electronic Identity Card (CIE) number.
         * The CIE number must conform to the pattern of exactly 2 uppercase letters,
         * followed by 5 digits, and ending with 2 uppercase letters.
         *
         * This is used within the IdentityCard context to ensure CIE numbers meet the expected standard.
         *
         * @since 2026-02
         */
        val CIE_NUMBER_REGEX = Regex("^[A-Z]{2}[0-9]{5}[A-Z]{2}$")

        /**
         * Validates whether the given string is a valid CIE number.
         *
         * @receiver The string to validate.
         * @return `true` if the string matches the CIE number format, `false` otherwise.
         * @since 2026-02
         */
        @JvmStatic
        fun String.isValidCIENumber() = CIE_NUMBER_REGEX(uppercase())

        /**
         * Computes the expiration date for a document based on the provided issue date and birth date.
         *
         * The expiration is determined by the age of the individual at the time of issue. The rules are:
         * - Ages 0 to 2 years: valid for 3 years.
         * - Ages 3 to 17 years: valid for 5 years.
         * - Ages 18 to 69 years: valid for 10 years.
         * - Ages 70 years and older: valid for 50 years.
         * The expiration date is aligned to the individual's birthday after the calculated standard expiration date.
         *
         * @param issueDate The date the document was issued.
         * @param birthDate The birth date of the individual.
         * @return The calculated expiration date of the document.
         * @since 2026-02
         */
        @JvmStatic
        fun computeExpiration(issueDate: LocalDate, birthDate: LocalDate): LocalDate {
            val age = birthDate.until(issueDate, ChronoUnit.YEARS)
            val standard = when (age) {
                in 0..<3 -> issueDate.plusYears(3)
                in 3..<18 -> issueDate.plusYears(5)
                in 18..<70 -> issueDate.plusYears(10)
                else -> issueDate.plusYears(50)
            }
            val birthdayInExpiryYear = LocalDate(standard.year, birthDate.month, birthDate.dayOfMonth)

            return if (!birthdayInExpiryYear.isBefore(standard)) birthdayInExpiryYear
            else birthdayInExpiryYear.plusYears(1)
        }

        private fun validateExpiration(issueDate: LocalDate, birthDate: LocalDate, expiryDate: LocalDate): Boolean {
            val computedExpiration = computeExpiration(issueDate, birthDate)
            val age = birthDate.until(issueDate, ChronoUnit.YEARS)
            if (computedExpiration.isEqual(expiryDate)) return true
            if (age < 70) return false
            return expiryDate.isEqual(computedExpiration) || !issueDate.plusYears(10).isAfter(expiryDate)
        }

        class Serializer : ValueSerializer<IdentityCard>() {
            override fun serialize(value: IdentityCard, gen: tools.jackson.core.JsonGenerator, ctxt: SerializationContext) {
                gen.writeStartObject()
                gen.writeStringProperty("number", value.number)
                gen.writeStringProperty("surname", value.surname)
                gen.writeStringProperty("name", value.name)
                gen.writeStringProperty("birthDate", value.birthDate.toString())
                gen.writeStringProperty("sex", value.sex.name)
                gen.writeNumberProperty("height", (value.height convertTo MeasureUnit.LengthUnit.METER)().value * 100)
                gen.writeStringProperty("citizenship", value.citizenship.alpha3)
                gen.writeStringProperty("issueDate", value.issueDate.toString())
                gen.writeStringProperty("expiryDate", value.expiryDate.toString())
                gen.writeStringProperty("issuingMunicipality", value.issuingMunicipality)
                gen.writeStringProperty("fiscalCode", value.fiscalCode.toString())
                gen.writePOJOProperty("residentialAddress", value.residentialAddress)
                gen.writeEndObject()
            }
        }

        class Deserializer : ValueDeserializer<IdentityCard>() {
            override fun deserialize(p: tools.jackson.core.JsonParser, ctxt: DeserializationContext): IdentityCard {
                val node = p.objectReadContext().readTree<ObjectNode>(p)
                return IdentityCard(
                    number = node["number"].asString(),
                    surname = node["surname"].asString(),
                    name = node["name"].asString(),
                    birthDate = LocalDate(node["birthDate"].asString())(),
                    birthPlace = node["birthPlace"].asString(),
                    sex = node["sex"].asString().toEnumConst(),
                    height = Length(node["height"].asDouble() / 100, MeasureUnit.LengthUnit.METER),
                    citizenship = (Country ofAlpha3 node["citizenship"].asString())!!,
                    issueDate = LocalDate(node["issueDate"].asString())(),
                    expiryDate = LocalDate(node["expiryDate"].asString())(),
                    issuingMunicipality = node["issuingMunicipality"].asString(),
                    fiscalCode = FiscalCode(node["fiscalCode"].asString()),
                    residentialAddress = node["residentialAddress"].traverse(p.objectReadContext()).readValueAs(Contact.Address::class.java)
                )
            }
        }

        class OldSerializer : JsonSerializer<IdentityCard>() {
            override fun serialize(value: IdentityCard, gen: JsonGenerator, serializers: SerializerProvider) {
                gen.writeStartObject()
                gen.writeStringField("number", value.number)
                gen.writeStringField("surname", value.surname)
                gen.writeStringField("name", value.name)
                gen.writeStringField("birthDate", value.birthDate.toString())
                gen.writeStringField("sex", value.sex.name)
                gen.writeNumberField("height", (value.height convertTo MeasureUnit.LengthUnit.METER)().value * 100)
                gen.writeStringField("citizenship", value.citizenship.alpha3)
                gen.writeStringField("issueDate", value.issueDate.toString())
                gen.writeStringField("expiryDate", value.expiryDate.toString())
                gen.writeStringField("issuingMunicipality", value.issuingMunicipality)
                gen.writeStringField("fiscalCode", value.fiscalCode.toString())
                gen.writeObjectField("residentialAddress", value.residentialAddress)
                gen.writeEndObject()
            }
        }

        class OldDeserializer : JsonDeserializer<IdentityCard>() {
            override fun deserialize(p: JsonParser, ctxt: com.fasterxml.jackson.databind.DeserializationContext): IdentityCard {
                val node = p.codec.readTree<com.fasterxml.jackson.databind.node.ObjectNode>(p)
                return IdentityCard(
                    number = node["number"].asText(),
                    surname = node["surname"].asText(),
                    name = node["name"].asText(),
                    birthDate = LocalDate(node["birthDate"].asText())(),
                    birthPlace = node["birthPlace"].asText(),
                    sex = node["sex"].asText().toEnumConst(),
                    height = Length(node["height"].asDouble() / 100, MeasureUnit.LengthUnit.METER),
                    citizenship = (Country ofAlpha3 node["citizenship"].asText())!!,
                    issueDate = LocalDate(node["issueDate"].asText())(),
                    expiryDate = LocalDate(node["expiryDate"].asText())(),
                    issuingMunicipality = node["issuingMunicipality"].asText(),
                    fiscalCode = FiscalCode(node["fiscalCode"].asText()),
                    residentialAddress = node["residentialAddress"].asText().deserialize<Contact.Address>()()
                )
            }
        }
    }
}
