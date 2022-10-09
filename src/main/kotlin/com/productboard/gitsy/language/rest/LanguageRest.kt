package com.productboard.gitsy.language.rest

import com.productboard.gitsy.language.domain.calculation.CalculatedLanguageSet
import com.productboard.gitsy.language.service.RepositoryLanguagesService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.joda.time.Instant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotBlank

/**
 * REST API controller for repository languages.
 *
 * @author Aleksei Ermak
 * @date 09.10.2022
 */
@RestController
@RequestMapping(value = ["/api/language"])
class LanguageRest(
    @Autowired private val languagesService: RepositoryLanguagesService,
) {

    /**
     * Gets the latest language set for the given repository.
     */
    @Operation(
        summary = "Get the latest language set for the given repository",
        description = "Returns the latest language set if successful",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully found the latest language set"),
            ApiResponse(responseCode = "404", description = "Can't find any language sets"),
            ApiResponse(responseCode = "500", description = "Internal Server Error"),
        ]
    )
    @GetMapping(value = ["/{organization}/{repository}/latest"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLatestLanguageSet(

        @Parameter(
            name = "organization",
            example = "productboard",
            description = "name of organization",
            required = true,
            `in` = ParameterIn.PATH,
        )
        @PathVariable("organization")
        @NotBlank
        organizationName: String,

        @Parameter(
            name = "repository",
            example = "locatorjs",
            description = "name of repository",
            required = true,
            `in` = ParameterIn.PATH,
        )
        @PathVariable("repository")
        @NotBlank
        repositoryName: String,

    ): ResponseEntity<Map<String, Double>> {
        val languageSet = languagesService.getLatestLanguageSet(organizationName, repositoryName)
        if (languageSet != null) {
            return ResponseEntity(languageSet.languageMap, HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Gets the history of language set changes for the given repository.
     */
    @Operation(
        summary = "Get the history of language set changes for the given repository",
        description = "Returns the history of language set changes if successful",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully collect the history of language set changes"),
            ApiResponse(responseCode = "404", description = "Can't find any language sets"),
            ApiResponse(responseCode = "500", description = "Internal Server Error"),
        ]
    )
    @GetMapping(value = ["/{organization}/{repository}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLanguageEvolution(

        @Parameter(
            name = "organization",
            example = "productboard",
            description = "name of organization",
            required = true,
            `in` = ParameterIn.PATH,
        )
        @PathVariable("organization")
        @NotBlank
        organizationName: String,

        @Parameter(
            name = "repository",
            example = "locatorjs",
            description = "name of organization",
            required = true,
            `in` = ParameterIn.PATH,
        )
        @PathVariable("repository")
        @NotBlank
        repositoryName: String,

        @Parameter(
            name = "from",
            example = "2022-10-08T10:30:00.000+02:00",
            description = "start of the date range",
            required = false,
            `in` = ParameterIn.QUERY
        )
        @RequestParam("from")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        from: Instant?,

        @Parameter(
            name = "to",
            example = "2000-10-10T10:30:00.000+02:00",
            description = "end of the date range",
            required = false,
            `in` = ParameterIn.QUERY
        )
        @RequestParam("to")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        to: Instant?,

    ): ResponseEntity<List<CalculatedLanguageSet>> {
        val languageEvolution = if (from != null && to != null) {
            languagesService.getLanguageEvolution(organizationName, repositoryName, from, to)
        } else {
            languagesService.getLanguageEvolution(organizationName, repositoryName)
        }
        return ResponseEntity(languageEvolution, HttpStatus.OK)
    }

    /**
     * Gets the aggregation of language sets of all repositories the given organization owns.
     */
    @Operation(
        summary = "Get the aggregation of language sets of all repositories the given organization owns",
        description = "Returns the aggregation of language sets of all repositories if successful",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully aggregate language sets of all repositories"),
            ApiResponse(responseCode = "404", description = "Can't find any language sets"),
            ApiResponse(responseCode = "500", description = "Internal Server Error"),
        ]
    )
    @GetMapping(value = ["/{organization}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRepositoryAggregation(

        @Parameter(
            name = "organization",
            example = "productboard",
            description = "name of organization",
            required = true,
            `in` = ParameterIn.PATH
        )
        @PathVariable("organization")
        @NotBlank
        organizationName: String,

    ): ResponseEntity<Map<String, Double>> {
        val languageSet = languagesService.getAggregatedLanguageSetForOrganization(organizationName)
        if (languageSet != null) {
            return ResponseEntity(languageSet.languageMap, HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
