package dev.tommasop1804.italyutils.classes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import dev.tommasop1804.kutils.exceptions.MalformedInputException
import dev.tommasop1804.kutils.invoke
import dev.tommasop1804.kutils.unaryPlus
import jakarta.persistence.AttributeConverter
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize

/**
 * Represents an SDI (Sistema di Interscambio) recipient code used in electronic invoicing systems in Italy.
 * This value class ensures that the provided string is a valid SDI recipient code by adhering to specific
 * length constraints and pattern rules.
 *
 * The SDI recipient code can either belong to a public administration (PA) if it contains 6 characters,
 * or a private entity if it contains 7 characters.
 *
 * @property value Encapsulated string value representing the SDI recipient code.
 * @property length The length of the SDI recipient code.
 * @throws MalformedInputException If the SDI recipient code does not meet the required format or pattern.
 * @constructor Creates an SDIRecipientCode from the given string after validation.
 * @since 2026-02.1
 * @author Tommaso Pastorelli
 */
@JvmInline
@JsonSerialize(using = PartitaIVA.Companion.Serializer::class)
@JsonDeserialize(using = PartitaIVA.Companion.Deserializer::class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = PartitaIVA.Companion.OldSerializer::class)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = PartitaIVA.Companion.OldDeserializer::class)
@Suppress("unused")
value class SDIRecipientCode private constructor(private val value: String): CharSequence {

    /**
     * Provides the length of the `SDIRecipientCode` value.
     * 
     * This property represents the number of characters in the underlying value of the `SDIRecipientCode`. 
     * The length will always be 6 or 7, as per the validation rules for SDI recipient codes.
     *
     * @return The length of the SDI recipient code.
     * @since 2026-02.1
     */
    override val length: Int
        get() = value.length

    /**
     * Indicates whether the SDI recipient code represents a public administration (PA) entity.
     * 
     * Returns `true` if the length of the code is exactly 6, which corresponds to codes associated
     * with public administration entities.
     * 
     * Used to differentiate between public administration and private recipient codes based on
     * their length.
     *
     * @since 2026-02.1
     */
    val isPA
        get() = value.length == 6
    /**
     * Indicates whether the current SDI recipient code represents a private recipient.
     * A recipient code is considered private if its length is exactly 7 characters.
     *
     * @return `true` if the code represents a private recipient, `false` otherwise.
     * @since 2026-02.1
     */
    val isPrivate
        get() = value.length == 7

    /**
     * Constructs an instance of [SDIRecipientCode] from the provided character sequence.
     * 
     * @param code The character sequence representing the SDI recipient code.
     * @throws MalformedInputException If the input string is not a valid SDI recipient code.
     * @since 2026-02.1
     */
    constructor(code: CharSequence) : this(+code.toString())

    init {
        PATTERN(value) || throw MalformedInputException("The string is not a valid SDI recipient code")
    }

    companion object {
        /**
         * Defines a regular expression pattern used to validate SDI (Sistema di Interscambio) recipient codes.
         * The pattern ensures that the input string consists of 6 or 7 alphanumeric characters,
         * where each character must be an uppercase letter (A-Z) or a digit (0-9).
         *
         * This is typically used to validate codes for public administrations or private entities
         * in accordance with the Italian SDI protocol.
         *
         * Constraints:
         * - Length: 6 or 7 characters.
         * - Characters: Uppercase letters (A-Z) and digits (0-9) only.
         *
         * @since 2026-02.1
         */
        @JvmStatic
        val PATTERN = Regex("^[A-Z0-9]{6,7}$")
        /**
         * A predefined SDI recipient code representing a generic private recipient.
         * This code is used in contexts where a private recipient must be represented generically,
         * adhering to the required SDI recipient code structure.
         *
         * The value of this code is "0000000", which conforms to the expected format for private recipient codes.
         *
         * @since 2026-02.1
         */
        @JvmStatic
        val GENERIC_PRIVATE = SDIRecipientCode("0000000")
        /**
         * Represents the predefined SDI recipient code for foreign entities.
         * This constant signifies the special SDI recipient code "XXXXXXX"
         * often used in contexts where the recipient is a foreign organization or entity
         * and does not fall under typical PA (Public Administration) or private entity constraints.
         *
         * @since 2026-02.1
         */
        @JvmStatic
        val FOREIGN = SDIRecipientCode("XXXXXXX")

        /**
         * Validates whether the given input is a valid SDI recipient code.
         *
         * An SDI recipient code is considered valid if it adheres to the conditions
         * defined in the `SDIRecipientCode` rules, which enforce specific length 
         * and pattern requirements.
         *
         * @param code The input sequence to validate as an SDI recipient code.
         * @return `true` if the input is a valid SDI recipient code, otherwise `false`.
         * @since 2026-02.1
         */
        @JvmStatic
        fun isValidSDIRecipientCode(code: CharSequence) = runCatching { SDIRecipientCode(code) }.isSuccess
        /**
         * Attempts to convert the receiving [CharSequence] to an instance of [SDIRecipientCode].
         *
         * This function validates whether the [CharSequence] adheres to the format required for an SDI recipient code.
         * Depending on the length and structure of the provided [CharSequence], it will determine if it can represent
         * a valid code. The result of the conversion is encapsulated within a [Result] object.
         *
         * @return A [Result] containing the successfully created [SDIRecipientCode], or a failure if the 
         *         validation or conversion fails.
         * @receiver The input [CharSequence] to be evaluated as an SDI recipient code.
         *
         * @since 2026-02.1
         */
        @JvmStatic
        fun CharSequence.toSDIRecipientCode() = runCatching { SDIRecipientCode(this) }

        class Serializer : ValueSerializer<SDIRecipientCode>() {
            override fun serialize(value: SDIRecipientCode, gen: tools.jackson.core.JsonGenerator, ctxt: SerializationContext) {
                gen.writeString(value.value)
            }
        }

        class Deserializer : ValueDeserializer<SDIRecipientCode>() {
            override fun deserialize(p: tools.jackson.core.JsonParser, ctxt: tools.jackson.databind.DeserializationContext) = SDIRecipientCode(p.string)
        }

        class OldSerializer : JsonSerializer<SDIRecipientCode>() {
            override fun serialize(value: SDIRecipientCode, gen: JsonGenerator, serializers: SerializerProvider) =
                gen.writeString(value.value)
        }

        class OldDeserializer : JsonDeserializer<SDIRecipientCode>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): SDIRecipientCode = SDIRecipientCode(p.text)
        }

        @jakarta.persistence.Converter(autoApply = true)
        class Converter : AttributeConverter<SDIRecipientCode?, String?> {
            override fun convertToDatabaseColumn(attribute: SDIRecipientCode?): String? = attribute?.value
            override fun convertToEntityAttribute(dbData: String?): SDIRecipientCode? = dbData?.let { SDIRecipientCode(it) }
        }
    }

    /**
     * Returns the character at the specified index within the underlying string.
     *
     * @param index the index of the character to return, must be within the bounds of the string.
     * @return the character at the specified position.
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= length).
     * @since 2026-02.1
     */
    override fun get(index: Int) = value[index]

    /**
     * Returns a new character sequence that is a subsequence of this character sequence, starting
     * at the specified `startIndex` (inclusive) and ending at the specified `endIndex` (exclusive).
     *
     * @param startIndex the starting index of the subsequence, inclusive. Must be within the valid
     * bounds of this sequence.
     * @param endIndex the ending index of the subsequence, exclusive. Must be within the valid
     * bounds of this sequence and not less than `startIndex`.
     * @return the specified subsequence as a new character sequence.
     * @throws IndexOutOfBoundsException if `startIndex` or `endIndex` are out of bounds.
     * @since 2026-02.1
     */
    override fun subSequence(startIndex: Int, endIndex: Int) = value.subSequence(startIndex, endIndex)

    /**
     * Returns a string representation of the SDIRecipientCode instance.
     * This representation is based on the underlying value of the object.
     *
     * @return a string equivalent to the value representation of this object.
     * @since 2026-02.1
     */
    override fun toString() = value
}