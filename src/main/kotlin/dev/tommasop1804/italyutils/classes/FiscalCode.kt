package dev.tommasop1804.italyutils.classes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import dev.tommasop1804.italyutils.annotations.UnreliableYear
import dev.tommasop1804.kutils.*
import dev.tommasop1804.kutils.classes.constants.Sex
import dev.tommasop1804.kutils.exceptions.MalformedInputException
import dev.tommasop1804.kutils.exceptions.ValidationFailedException
import dev.tommasop1804.kutils.get
import dev.tommasop1804.kutils.invoke
import jakarta.persistence.AttributeConverter
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import java.time.LocalDate
import java.time.Month
import java.time.Year
import kotlin.text.iterator

/**
 * Represents an Italian fiscal code (Codice Fiscale).
 * This value class ensures a proper Codice Fiscale format through validation upon initialization.
 *
 * A Codice Fiscale is a unique alphanumeric identifier used in Italy for tax purposes.
 * It consists of 16 characters and must comply with the specific pattern:
 * - 6 uppercase letters.
 * - 2 digits for the year of birth.
 * - 1 uppercase letter representing the month of birth.
 * - 2 digits for the day of birth (with additional encoding rules for gender).
 * - 1 uppercase letter for a checksum.
 *
 * Instances are immutable and validated to ensure compliance with the above format.
 * The class provides several utility operations such as `subSequence` and conversion utilities.
 *
 * The underlying string value is always stored in uppercase form.
 *
 * @constructor Ensures the given value is a valid Codice Fiscale.
 * Throws [MalformedInputException] if the initial string does not match the required format.
 * @param value The Codice Fiscale string.
 * @since 2026-02.1
 * @author Tommaso Pastorelli
 */
@JvmInline
@JsonSerialize(using = FiscalCode.Companion.Serializer::class)
@JsonDeserialize(using = FiscalCode.Companion.Deserializer::class)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = FiscalCode.Companion.OldSerializer::class)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = FiscalCode.Companion.OldDeserializer::class)
@Suppress("unused", "kutils_collection_declaration", "kutils_temporal_of_as_temporal")
value class FiscalCode private constructor(private val value: String) : CharSequence {
    /**
     * Represents the length of the value string.
     * This is a read-only property that calculates and returns
     * the current length of the underlying value.
     *
     * @return The number of characters in the value string.
     * @since 2026-02.1
     */
    override val length: Int
        get() = value.length

    /**
     * Retrieves the birth date as a pair consisting of the year of birth and the day and month.
     *
     * The `birthDate` property extracts and computes the year, month, and day information
     * from the string value of the `FiscalCode`. The year is represented as an `Int`, because
     * we are not sure about the century, so we preferred to leave only the last 2 numbers. The mapping of months
     * to their corresponding characters is predefined, and adjustments for day values
     * are made based on specific conditions (e.g., subtracting 40 if applicable).
     *
     * @return A `Pair` where the first element is the year as an `Int`, and the second element
     *         is a `MonthDay` representing the corresponding month and day.
     * @throws StringIndexOutOfBoundsException If the value does not meet the expected length or format.
     * @since 2026-02.1
     */
    @UnreliableYear
    val birthDate: LocalDate
        get() = LocalDate.of(
            (Year.now().toString()[0..<2].toInt() - if (value[6..<8].toInt() > (-2)(Year.now().toString()).toInt()) 1 else 0) * 100 + value[6..<8].toInt(),
            when (value[8]) {
                'A' -> Month.JANUARY
                'B' -> Month.FEBRUARY
                'C' -> Month.MARCH
                'D' -> Month.APRIL
                'E' -> Month.MAY
                'H' -> Month.JUNE
                'L' -> Month.JULY
                'M' -> Month.AUGUST
                'P' -> Month.SEPTEMBER
                'R' -> Month.OCTOBER
                'S' -> Month.NOVEMBER
                'T' -> Month.DECEMBER
                else -> null
            },
            if (value[9..<11].toInt() > 40) value[9..<11].toInt() - 40 else value[9..<11].toInt()
        )
    /**
     * Retrieves a substring representing the city code from the given value.
     *
     * The city code is extracted as a three-character substring starting from the 11th position
     * (inclusive) to the 14th position (exclusive) of the `value` property.
     *
     * @return A string representing the city code.
     * @throws StringIndexOutOfBoundsException if the `value` property is shorter than 14 characters.
     * @since 2026-02.1
     */
    val municipalityOfBirthCode: String
        get() = value[11..14]

    /**
     * The `municipalityOfBirth` variable represents the municipality associated with the place of birth.
     *
     * The value is derived from a specific subset of the `value` property within the class by using the
     * cadastral code located at indices 11 to 13. This cadastral code is mapped to a `Municipality` instance
     * to determine the geographic location associated with the birth.
     *
     * If the cadastral code does not correspond to a valid municipality, the returned value may be `null`.
     *
     * @return A `Municipality` instance corresponding to the cadastral code found in the `value` property, or `null` if invalid.
     * @throws IndexOutOfBoundsException if the subset of the `value` property used for the cadastral code is out of range.
     * @since 2026-02.1
     */
    val municipalityOfBirth: Municipality?
        get() = Municipality.Companion ofCadastralCode municipalityOfBirthCode

    /**
     * Retrieves the biological sex (gender) based on encoded information in the `value` field.
     *
     * The determination logic interprets a specific substring of the `value` string to infer the
     * sex. If the numeric representation of the substring is greater than 40, the return value
     * will be `Sex.FEMALE`; otherwise, it will be `Sex.MALE`.
     *
     * @return the biological sex as an instance of the [Sex] enum.
     * @throws StringIndexOutOfBoundsException if the value string does not contain the required indices.
     * @throws NumberFormatException if the substring cannot be converted to an integer.
     * @since 2026-02.1
     */
    val sex: Sex
        get() = if (value[9..<11].toInt() > 40) Sex.FEMALE else Sex.MALE

    /**
     * Constructs an instance of the `ItalianFiscalCode` class by converting the given [value]
     * into an uppercase string representation.
     *
     * This constructor ensures that the internal representation of the given [value]
     * is stored in an uppercase format, which is typically required for proper handling
     * of codes like `ItalianFiscalCode` values.
     *
     * @param value the initial character sequence to be processed and used for the instance.
     * @since 2026-02.1
     */
    constructor(value: CharSequence) : this(+value.toString())

    init {
        value.matches(Regex("^[A-Z]{6}[0-9]{2}[A-EHLMPR-T][0-9]{2}[A-Z][0-9]{3}[A-Z]$")) || throw MalformedInputException(
            "The string is not a valid Italian fiscal code"
        )

        value.last().expect(computeControlLetter(value.dropLast(1))) {
            "The string is not a valid Italian fiscal code. Check the Control character."
        }
    }

    companion object {
        /**
         * Validates whether the string is a valid Italian Fiscal Code.
         *
         * This method checks if the current string can successfully instantiate an instance of the
         * `FiscalCode` class without exceptions being thrown. If the instantiation succeeds,
         * the string is considered a valid Italian Fiscal Code.
         *
         * @receiver the string to validate.
         * @return `true` if the string represents a valid Italian Fiscal Code, `false` otherwise.
         * @since 2026-02.1
         */
        @JvmStatic
        fun String.isValidItalianFiscalCode() = runCatching { FiscalCode(this) }.isSuccess

        /**
         * Converts the current string instance to an instance of `FiscalCode`.
         *
         * This method attempts to create an `FiscalCode` object using the string
         * on which it is called. The conversion operation is wrapped in a `Result` object
         * for safe handling of potential exceptions. If the conversion is successful,
         * the resulting `FiscalCode` instance is encapsulated in a successful `Result`.
         * If an error occurs during conversion (e.g., invalid input format or other constraints),
         * the resulting `Result` will be a failure containing the thrown exception.
         *
         * @receiver The string to be converted into an `FiscalCode`.
         * @return A `Result` containing either the successfully created `FiscalCode` instance
         *         or the exception encountered during the conversion process.
         * @since 2026-02.1
         */
        @JvmStatic
        fun String.toItalianFiscalCode() = runCatching { FiscalCode(this) }

        /**
         * Computes a unique alphanumeric code based on the provided personal details, following the Italian Fiscal Code model.
         *
         * @param lastName The last name of the individual. Must only contain alphabetic characters.
         * @param name The first name of the individual. Must only contain alphabetic characters.
         * @param birthDate The birth date of the individual.
         * @param sex The sex of the individual, either male or female.
         * @param cityCode A four-character city code. The first character must be an uppercase letter, followed by three digits.
         * @return A computed alphanumeric code derived from the provided details, wrapped in a [Result].
         * @throws ValidationFailedException If the input values do not meet the required conditions.
         * @since 2026-02.1
         */
        @JvmStatic
        fun compute(lastName: String, name: String, birthDate: LocalDate, sex: Sex, cityCode: String) = runCatching {
            validate(lastName.isAlphabetic) { "The last name must be an alphabetic character" }
            validate(name.isAlphabetic) { "The name must be an alphabetic character" }
            validate((+cityCode).matches(Regex("^[A-Z][0-9]{3}$"))) { "The city code must be an alphanumeric character as AXXX" }
            validate(cityCode.length == 4) { "The city code must be 3 characters long" }

            val code = kotlin.text.StringBuilder("")

            // LAST NAME
            var counter = 0
            for (char in lastName) {
                if (char.isConsonant) {
                    counter++
                    code.append(char)
                    if (counter == 3) break
                }
            }
            if (counter < 3) {
                for (char in lastName) {
                    if (char.isVowel) {
                        code.append(char)
                        counter++
                    }
                    if (counter == 3) break
                }
            }
            if (counter != 3) code.append("X")
            if (code.length != 3) code.append("X")

            // NAME
            counter = 0
            var numConsonant = 0
            name.forEach { if (it.isConsonant) numConsonant++ }
            if (numConsonant <= 3) {
                for (char in name) {
                    if (char.isConsonant) {
                        counter++
                        code.append(char)
                        if (counter == 3) break
                    }
                }
                if (counter < 3) {
                    for (char in name) {
                        if (char.isVowel) {
                            code.append(char)
                            counter++
                        }
                        if (counter == 3) break
                    }
                }
                if (counter != 3) code.append("X")
                if (code.length != 6) code.append("X")
            } else {
                for (char in name) {
                    if (char.isConsonant) {
                        counter++
                        if (counter != 2) code.append(char)
                        if (counter == 4) break
                    }
                }
            }

            // YEAR
            code.append((-2)(birthDate.year.toString()))

            // MONTH
            code.append(when (birthDate.month) {
                Month.JANUARY -> 'A'
                Month.FEBRUARY -> 'B'
                Month.MARCH -> 'C'
                Month.APRIL -> 'D'
                Month.MAY -> 'E'
                Month.JUNE -> 'H'
                Month.JULY -> 'L'
                Month.AUGUST -> 'M'
                Month.SEPTEMBER -> 'P'
                Month.OCTOBER -> 'R'
                Month.NOVEMBER -> 'S'
                Month.DECEMBER -> 'T'
            })

            // DAY
            if (sex == Sex.FEMALE) code.append((birthDate.dayOfMonth + 40).toString())
            else code.append(birthDate.dayOfMonth.toString())

            // CITY CODE
            code.append(cityCode)

            // VALIDATION CHAR
            val controllo = computeControlLetter(code.toString())
            if (!controllo.isLetter()) throw kotlin.RuntimeException("Unexpected error")
            code.append(controllo)
            FiscalCode(+code.toString())
        }

        /**
         * Computes the control letter based on the given code string following specific rules.
         *
         * @param code the input string used to compute the control letter. It is expected to contain valid alphanumeric characters.
         * @return the computed control letter as a single uppercase character.
         * @since 2026-02.1
         */
        @JvmStatic
        fun computeControlLetter(code: String): Char {
            val odd = mutableListOf<Char>()
            val even = mutableListOf<Char>()
            val even2 = mutableListOf<Int>()
            val odd2 = mutableListOf<Int>()

            for (i in code.indices) {
                if (i.isEven) odd.add((+code[i]).first())
                else even.add((+code[i]).first())
            }
            even.forEach { when(it) {
                '0' -> even2.add(0)
                '1' -> even2.add(1)
                '2' -> even2.add(2)
                '3' -> even2.add(3)
                '4' -> even2.add(4)
                '5' -> even2.add(5)
                '6' -> even2.add(6)
                '7' -> even2.add(7)
                '8' -> even2.add(8)
                '9' -> even2.add(9)
                'A' -> even2.add(0)
                'B' -> even2.add(1)
                'C' -> even2.add(2)
                'D' -> even2.add(3)
                'E' -> even2.add(4)
                'F' -> even2.add(5)
                'G' -> even2.add(6)
                'H' -> even2.add(7)
                'I' -> even2.add(8)
                'J' -> even2.add(9)
                'K' -> even2.add(10)
                'L' -> even2.add(11)
                'M' -> even2.add(12)
                'N' -> even2.add(13)
                'O' -> even2.add(14)
                'P' -> even2.add(15)
                'Q' -> even2.add(16)
                'R' -> even2.add(17)
                'S' -> even2.add(18)
                'T' -> even2.add(19)
                'U' -> even2.add(20)
                'V' -> even2.add(21)
                'W' -> even2.add(22)
                'X' -> even2.add(23)
                'Y' -> even2.add(24)
                'Z' -> even2.add(25)
            } }
            odd.forEach { when(it) {
                '0' -> odd2.add(1)
                '1' -> odd2.add(0)
                '2' -> odd2.add(5)
                '3' -> odd2.add(7)
                '4' -> odd2.add(9)
                '5' -> odd2.add(13)
                '6' -> odd2.add(15)
                '7' -> odd2.add(17)
                '8' -> odd2.add(19)
                '9' -> odd2.add(21)
                'A' -> odd2.add(1)
                'B' -> odd2.add(0)
                'C' -> odd2.add(5)
                'D' -> odd2.add(7)
                'E' -> odd2.add(9)
                'F' -> odd2.add(13)
                'G' -> odd2.add(15)
                'H' -> odd2.add(17)
                'I' -> odd2.add(19)
                'J' -> odd2.add(21)
                'K' -> odd2.add(2)
                'L' -> odd2.add(4)
                'M' -> odd2.add(18)
                'N' -> odd2.add(20)
                'O' -> odd2.add(11)
                'P' -> odd2.add(3)
                'Q' -> odd2.add(6)
                'R' -> odd2.add(8)
                'S' -> odd2.add(12)
                'T' -> odd2.add(14)
                'U' -> odd2.add(16)
                'V' -> odd2.add(10)
                'W' -> odd2.add(22)
                'X' -> odd2.add(25)
                'Y' -> odd2.add(24)
                'Z' -> odd2.add(23)
            }}

            var counter = 0
            var counter2 = 0
            odd2.forEach { counter2 += it }
            even2.forEach { counter += it }
            counter += counter2
            counter %= 26
            var controllo = '0'
            when (counter) {
                0 -> controllo = 'A'
                1 -> controllo = 'B'
                2 -> controllo = 'C'
                3 -> controllo = 'D'
                4 -> controllo = 'E'
                5 -> controllo = 'F'
                6 -> controllo = 'G'
                7 -> controllo = 'H'
                8 -> controllo = 'I'
                9 -> controllo = 'J'
                10 -> controllo = 'K'
                11 -> controllo = 'L'
                12 -> controllo = 'M'
                13 -> controllo = 'N'
                14 -> controllo = 'O'
                15 -> controllo = 'P'
                16 -> controllo = 'Q'
                17 -> controllo = 'R'
                18 -> controllo = 'S'
                19 -> controllo = 'T'
                20 -> controllo = 'U'
                21 -> controllo = 'V'
                22 -> controllo = 'W'
                23 -> controllo = 'X'
                24 -> controllo = 'Y'
                25 -> controllo = 'Z'
                else -> throw kotlin.RuntimeException("Unexpected error")
            }
            return controllo
        }

        class Serializer : ValueSerializer<FiscalCode>() {
            override fun serialize(value: FiscalCode, gen: tools.jackson.core.JsonGenerator, ctxt: SerializationContext) {
                gen.writeString(value.value)
            }
        }

        class Deserializer : ValueDeserializer<FiscalCode>() {
            override fun deserialize(p: tools.jackson.core.JsonParser, ctxt: tools.jackson.databind.DeserializationContext) = FiscalCode(p.string)
        }

        class OldSerializer : JsonSerializer<FiscalCode>() {
            override fun serialize(value: FiscalCode, gen: JsonGenerator, serializers: SerializerProvider) =
                gen.writeString(value.value)
        }

        class OldDeserializer : JsonDeserializer<FiscalCode>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): FiscalCode = FiscalCode(p.text)
        }

        @jakarta.persistence.Converter(autoApply = true)
        class Converter : AttributeConverter<FiscalCode?, String?> {
            override fun convertToDatabaseColumn(attribute: FiscalCode?): String? = attribute?.value
            override fun convertToEntityAttribute(dbData: String?): FiscalCode? = dbData?.let { FiscalCode(it) }
        }
    }

    /**
     * Retrieves the element at the specified index from the value.
     *
     * @param index the position of the desired element.
     * @return the element located at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of range.
     * @since 2026-02.1
     */
    override operator fun get(index: Int) = value[index]

    /**
     * Returns a new character sequence that is a subsequence of this character sequence.
     *
     * @param startIndex the start index of the subsequence, inclusive.
     * @param endIndex the end index of the subsequence, exclusive.
     * @return a new character sequence that is a subsequence of this character sequence.
     * @throws IndexOutOfBoundsException if `startIndex` or `endIndex` is out of range, or if `startIndex > endIndex`.
     * @since 2026-02.1
     */
    override fun subSequence(startIndex: Int, endIndex: Int) = value.subSequence(startIndex, endIndex)

    /**
     * Returns a string representation of the object.
     * This implementation converts the `value` property to uppercase and returns it.
     *
     * @return the uppercase string representation of the `value` property.
     * @since 2026-02.1
     */
    override fun toString() = value
}