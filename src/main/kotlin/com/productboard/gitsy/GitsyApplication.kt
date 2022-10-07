package com.productboard.gitsy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitsyApplication

fun main(args: Array<String>) {
    runApplication<GitsyApplication>(*args)
}
