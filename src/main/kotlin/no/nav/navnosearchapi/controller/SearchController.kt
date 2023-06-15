package no.nav.navnosearchapi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController {
    @GetMapping("/")
    fun helloWorld(): String {
        return "Hello world"
    }
}