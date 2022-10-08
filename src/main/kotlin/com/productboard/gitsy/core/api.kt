@file:Suppress("Filename")

package com.productboard.gitsy.core

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.support.HttpRequestWrapper
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriTemplate
import java.lang.reflect.Type
import java.net.URI
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.Validation

/**
 * Builds URI from the given components.
 *
 * @param baseApiUrlScheme scheme of the base URL
 * @param baseApiUrl       base API URL
 * @param relativeUrl      relative URL
 * @param uriVariables     URI template variables to replace
 * @return configured URI
 */
fun buildApiUri(
    baseApiUrlScheme: String,
    baseApiUrl: String,
    relativeUrl: String,
    uriVariables: Map<String, Any>,
): URI {
    require(baseApiUrlScheme.isNotBlank()) { "|baseApiUrlScheme| can't be empty or blank" }
    require(baseApiUrl.isNotBlank()) { "|baseApiUrl| can't be empty or blank" }
    require(relativeUrl.isNotBlank()) { "|relativeUrl| can't be empty or blank" }

    val url: String = UriComponentsBuilder.newInstance()
        .scheme(baseApiUrlScheme)
        .host(baseApiUrl)
        .path(relativeUrl)
        .build()
        .toString()
    val uriTemplate = UriTemplate(url)
    return uriTemplate.expand(uriVariables)
}

/**
 * Configures REST API template with custom preconfigured converters and interceptors.
 *
 * @return preconfigured REST API template
 */
fun getConfiguredTemplate(): RestTemplate {
    val restTemplate = RestTemplate()
    restTemplate.messageConverters.add(0, createMappingJacksonHttpMessageConverter())
    restTemplate.interceptors = listOf(PlusEncoderInterceptor())
    return restTemplate
}

/**
 * Configures custom Jackson message converter that
 *  - invokes validation of REST API response entities
 *  - accepts case-insensitive enums
 *  - accepts empty string as null object
 *  - ignores unknown properties
 *
 * @return preconfigured Jackson message converter
 */
private fun createMappingJacksonHttpMessageConverter(): MappingJackson2HttpMessageConverter {
    val converter = MappingJackson2HttpMessageValidationConverter()
    converter.objectMapper = JsonMapper.builder()
        .addModule(JodaModule())
        .addModule(KotlinModule.Builder().build())
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()
    return converter
}

/**
 * Message converter that provides validation for serialized REST API response entities.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
class MappingJackson2HttpMessageValidationConverter : MappingJackson2HttpMessageConverter() {
    override fun read(type: Type, contextClass: Class<*>?, inputMessage: HttpInputMessage): Any {
        val input = super.read(type, contextClass, inputMessage)
        val factory = Validation.buildDefaultValidatorFactory()
        val validator = factory.validator
        val violations: Set<ConstraintViolation<Any>> = validator.validate(input)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
        return input
    }
}

/**
 * Custom encoder interceptor solving Spring Boot REST API template problems.
 * Detailed explanation on <a href="https://stackoverflow.com/questions/54294843/plus-sign-not-encoded-with-resttemplate-using-string-url-but-interpreted">StackOverflow</a>.
 *
 * @author Aleksei Ermak
 * @date 07.10.2022
 */
class PlusEncoderInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        val requestWrapper = object : HttpRequestWrapper(request) {
            override fun getURI(): URI {
                val u: URI = super.getURI()
                return if (u.rawQuery != null) {
                    val strictlyEscapedQuery: String = u.rawQuery.replace("+", "%2B")
                    UriComponentsBuilder
                        .fromUri(u)
                        .replaceQuery(strictlyEscapedQuery)
                        .build(true)
                        .toUri()
                } else {
                    u
                }
            }
        }
        return execution.execute(requestWrapper, body)
    }
}
