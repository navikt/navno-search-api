package no.nav.navnosearchapi.exception

class NoIndexForAppException(val appName: String, cause: Throwable) : Exception(cause)