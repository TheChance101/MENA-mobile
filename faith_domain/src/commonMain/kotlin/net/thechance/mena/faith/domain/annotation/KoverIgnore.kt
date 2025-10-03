package net.thechance.mena.faith.domain.annotation

@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
)
@Retention(AnnotationRetention.BINARY)
annotation class KoverIgnore
