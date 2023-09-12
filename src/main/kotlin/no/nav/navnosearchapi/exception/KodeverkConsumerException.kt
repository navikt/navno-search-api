package no.nav.navnosearchapi.exception

class KodeverkConsumerException : Exception {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}