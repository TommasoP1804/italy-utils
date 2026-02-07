@file:Suppress("unused")

package dev.tommasop1804.italyutils

import dev.tommasop1804.italyutils.classes.DriverLicence
import dev.tommasop1804.italyutils.classes.FiscalCode
import dev.tommasop1804.italyutils.classes.IdentityCard
import dev.tommasop1804.italyutils.classes.SDIRecipientCode

/**
 * Type alias for `IdentityCard`, commonly used to simplify
 * and shorten the reference to the type in the codebase.
 *
 * @since 2026-02.1
 */
typealias CIE = IdentityCard
/**
 * Represents an alias for the IdentityCard type.
 *
 * This typealias can be used interchangeably with IdentityCard to improve code readability
 * or adapt terminology to specific domain requirements.
 *
 * @since 2026-02.1
 */
typealias CartaIdentita = IdentityCard
/**
 * Represents a type alias for the DriverLicence type. 
 * This allows the usage of "Patente" as an alternative name for 
 * the DriverLicence type, improving code readability or aligning 
 * with specific domain terminology.
 *
 * @since 2026-02.1
 */
typealias Patente = DriverLicence
/**
 * Type alias for `FiscalCode`.
 * This alias can be used interchangeably with `FiscalCode` to improve code readability and simplicity in relevant contexts.
 * 
 * @since 2026-02.1
 */
typealias CF = FiscalCode
/**
 * A type alias for `FiscalCode`, providing an alternative name for readability
 * or domain-specific purposes.
 *
 * @since 2026-02.1
 */
typealias CodiceFiscale = FiscalCode
/**
 * Represents a type alias for the [SDIRecipientCode] class.
 *
 * The type alias `CodiceDestinatarioSDI` is used as a simplified terminology for working
 * with SDI recipient codes in Italian electronic invoicing systems.
 *
 * By utilizing this alias, it provides semantic clarity and domain-specific meaning
 * for contexts where SDI recipient codes are referred to as "Codice Destinatario SDI."
 *
 * It ensures that the same validation and behavior are inherited from [SDIRecipientCode].
 *
 * @see SDIRecipientCode
 * @since 2026-02.1
 */
typealias CodiceDestinatarioSDI = SDIRecipientCode