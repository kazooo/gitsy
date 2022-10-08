package com.productboard.gitsy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Main class of the application.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
@SpringBootApplication
class GitsyApplication

/**
 * Main entry point of the application.
 *
 * @param args list of arguments
 */
fun main(args: Array<String>) {
    runApplication<GitsyApplication>(*args)
}
