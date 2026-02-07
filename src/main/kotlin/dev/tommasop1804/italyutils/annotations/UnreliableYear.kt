package dev.tommasop1804.italyutils.annotations

@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER
)
@MustBeDocumented
@Suppress("unused")
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
annotation class UnreliableYear