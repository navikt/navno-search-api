package no.nav.navnosearchapi.utils

inline fun <reified T : Enum<T>> enumContains(name: String): Boolean {
    return enumValues<T>().any { it.toString() == name }
}

inline fun <reified T : Enum<T>> enumDescriptors(): List<String> {
    return enumValues<T>().map { it.toString() }
}