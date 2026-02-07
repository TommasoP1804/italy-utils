package com.sigeosrl.italyutils.classes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import dev.tommasop1804.kutils.exceptions.ExpectationMismatchException
import dev.tommasop1804.kutils.exceptions.MalformedInputException
import dev.tommasop1804.kutils.get
import dev.tommasop1804.kutils.invoke
import dev.tommasop1804.kutils.isEven
import dev.tommasop1804.kutils.isOdd
import jakarta.persistence.AttributeConverter
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Represents a Partita IVA (Italian VAT identification number) and provides functionality
 * to interact with its components.
 *
 * The class encapsulates the properties and operations related to a Partita IVA, such as
 * retrieving individual components or performing specific transformations.
 *
 * @property value The complete string representation of the Partita IVA.
 * @property length The total length of the Partita IVA string.
 * @property serialNumber The serial number component of the Partita IVA.
 * @property provincialOfficeCode The code representing the provincial office.
 * @property controlCode The control code component of the Partita IVA.
 * @since 2026-02
 */
@JvmInline
@JsonSerialize(using = PartitaIVA.Companion.Serializer::class)
@JsonDeserialize(using = PartitaIVA.Companion.Deserializer::class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = PartitaIVA.Companion.OldSerializer::class)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = PartitaIVA.Companion.OldDeserializer::class)
@Suppress("unused")
value class PartitaIVA(private val value: String) : CharSequence {
    /**
     * Represents the length of the value string.
     * This is a read-only property that calculates and returns
     * the current length of the underlying value.
     *
     * @return The number of characters in the value string.
     * @since 2026-02
     */
    override val length: Int
        get() = value.length

    /**
     * Represents the first 7 characters of the `value` field.
     *
     * This property is a substring that starts from the beginning of the `value` field and
     * ends at the seventh character, exclusive of any other content in `value`.
     *
     * @throws IndexOutOfBoundsException if the `value` field contains less than 7 characters.
     * @since 2026-02
     */
    val serialNumber: String
        get() = value[0..<7]

    /**
     * Represents the provincial office code extracted from a specific range of the `value` property.
     *
     * This property retrieves the 8th to 10th characters (inclusive of the 8th and exclusive of the 11th)
     * from the `value` field. It is utilized to determine the specific provincial office in the context of
     * a Partita IVA code.
     *
     * @throws IndexOutOfBoundsException if the `value` does not contain sufficient characters to extract
     *         the provincial office code.
     * @since 2026-02
     */
    val provincialOfficeCode: String
        get() = value[7..<10]

    /**
     * The control character extracted from the `value` property of the containing class.
     *
     * This property retrieves the last character of the `value`, which is generally used
     * as a checksum or verification character for validation purposes.
     *
     * @throws NoSuchElementException if the `value` is empty.
     * @since 2026-02
     */
    val controlCode: Char
        get() = value.last()

    init {
        value.matches(Regex("^[0-9]{11}$")) || throw MalformedInputException("The string is not a valid Italian Partita IVA")
        validateControlCode(value) || throw ExpectationMismatchException("The string is not a valid Italian Partita IVA. Check the control code.")
    }

    companion object {
        /**
         * Validates if the current string is a valid Partita IVA (Italian VAT number).
         *
         * This function attempts to construct a `PartitaIVA` instance using the string value.
         * If the construction is successful, the string is considered a valid Partita IVA.
         *
         * @receiver The string value to validate as a Partita IVA.
         * @return `true` if the string is a valid Partita IVA, `false` otherwise.
         * @since 2026-02
         */
        @JvmStatic
        fun String.isValidPartitaIVA() = runCatching { PartitaIVA(this) }.isSuccess

        /**
         * Attempts to convert the current string into an instance of `PartitaIVA`.
         *
         * This extension function tries to create a `PartitaIVA` object using the current string
         * as input. The operation is wrapped in a `Result` to handle potential exceptions
         * that might occur during the instantiation process.
         *
         * @receiver the string to be converted into a `PartitaIVA` object.
         * @return a [Result] containing the created `PartitaIVA` object or an exception if the operation failed.
         * @since 2026-02
         */
        @JvmStatic
        fun String.toPartitaIVA() = runCatching { PartitaIVA(this) }

        /**
         * Computes the control code for the provided string value based on a specific algorithm.
         *
         * The control code is determined by processing the characters of the string in alternating
         * positions (odd and even indices) to calculate separate sums, combining them, and deriving
         * a single character as the control code.
         *
         * @param value the input string from which the control code will be computed
         * @return the computed control code as a character
         * @since 2026-02
         */
        @JvmStatic
        fun computeControlCode(value: String): Char {
            value.matches(Regex("^[0-9]{10}$")) || throw MalformedInputException("The string is not a valid part of Italian Partita IVA")

            val odd = 5(value.filterIndexed { index, _ -> index.isEven }).sumOf(Char::digitToInt)
            val even = 5(value.filterIndexed { index, _ -> index.isOdd }).sumOf {
                val twice = it.digitToInt() * 2
                if (twice > 9) twice - 9 else twice
            }
            return (10 - (odd + even).mod(10)).mod(10).digitToChar()
        }

        /**
         * Validates the control code within a given value string using a calculation involving the Luhn algorithm.
         *
         * @param valueWithControlCode The value string containing the control code to be validated.
         *                             It is assumed that the last character represents the control code.
         * @return `true` if the control code is valid based on the calculated checksum, otherwise `false`.
         * @since 2026-02
         */
        @JvmStatic
        fun validateControlCode(valueWithControlCode: String): Boolean {
            val odd = 5(valueWithControlCode.filterIndexed { index, _ -> index.isEven }).sumOf(Char::digitToInt) + valueWithControlCode.last().digitToInt()
            val even = 5(valueWithControlCode.filterIndexed { index, _ -> index.isOdd }).sumOf {
                val twice = it.digitToInt() * 2
                if (twice > 9) twice - 9 else twice
            }
            return (odd + even).mod(10) == 0
        }

        class Serializer : ValueSerializer<PartitaIVA>() {
            override fun serialize(value: PartitaIVA, gen: tools.jackson.core.JsonGenerator, ctxt: SerializationContext) {
                gen.writeString(value.value)
            }
        }

        class Deserializer : ValueDeserializer<PartitaIVA>() {
            override fun deserialize(p: tools.jackson.core.JsonParser, ctxt: tools.jackson.databind.DeserializationContext) = PartitaIVA(p.string)
        }

        class OldSerializer : JsonSerializer<PartitaIVA>() {
            override fun serialize(value: PartitaIVA, gen: JsonGenerator, serializers: SerializerProvider) =
                gen.writeString(value.value)
        }

        class OldDeserializer : JsonDeserializer<PartitaIVA>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): PartitaIVA = PartitaIVA(p.text)
        }

        @jakarta.persistence.Converter(autoApply = true)
        class Converter : AttributeConverter<PartitaIVA?, String?> {
            override fun convertToDatabaseColumn(attribute: PartitaIVA?): String? = attribute?.value
            override fun convertToEntityAttribute(dbData: String?): PartitaIVA? = dbData?.let { PartitaIVA(it) }
        }
    }

    /**
     * Retrieves the element at the specified index from the value.
     *
     * @param index the position of the desired element.
     * @return the element located at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of range.
     * @since 2026-02
     */
    override operator fun get(index: Int) = value[index]

    /**
     * Returns a new character sequence that is a subsequence of this character sequence.
     *
     * @param startIndex the start index of the subsequence, inclusive.
     * @param endIndex the end index of the subsequence, exclusive.
     * @return a new character sequence that is a subsequence of this character sequence.
     * @throws IndexOutOfBoundsException if `startIndex` or `endIndex` is out of range, or if `startIndex > endIndex`.
     * @since 2026-02
     */
    override fun subSequence(startIndex: Int, endIndex: Int) = value.subSequence(startIndex, endIndex)

    /**
     * Returns a string representation of the object.
     * This implementation converts the `value` property to uppercase and returns it.
     *
     * @return the uppercase string representation of the `value` property.
     * @since 2026-02
     */
    override fun toString() = value
}