package org.getbuddies.a2step.totp

import org.getbuddies.a2step.db.totp.entity.Totp
import java.net.URI
import java.util.Collections

object Otpauth {
    private const val SCHEME = "otpauth"
    private const val HOST = "totp"
    private const val PARENTHESES = "/"
    private const val AND = "&"
    private const val EQUAL = "="

    private const val SECRET = "secret"
    private const val DIGITS = "digits"
    private const val PERIOD = "period"

    @JvmStatic
    fun tryParseTotp(url: String?): Totp {
        if (url.isNullOrEmpty()) {
            throw IllegalArgumentException("url is null or empty")
        }
        val uri = URI.create(url)
        val scheme = uri.scheme
        if (scheme != SCHEME) {
            throw IllegalArgumentException("url is invalid, please check it.")
        }
        val host = uri.host
        if (host != HOST) {
            throw IllegalArgumentException("the host of url is not totp, please check it.")
        }
        val path = uri.path
        if (path.isNullOrEmpty()) {
            throw IllegalArgumentException("the path of url is not totp, please check it.")
        }
        val addresses = path.replaceFirst(PARENTHESES, "").split(Regex(": *"))
        val name = addresses[0]
        val account = if (addresses.size > 1) (addresses[1]) else ("")

        val queryMap = uri.query?.split(AND)?.associate {
            val (key, value) = it.split(EQUAL)
            key to value
        } ?: Collections.emptyMap()
        val secret = queryMap[SECRET]
        if (secret.isNullOrEmpty()) {
            throw IllegalArgumentException("the url doesn't has param secret, please check it.")
        }
        val digits = queryMap[DIGITS]?.toInt() ?: 6
        val period = queryMap[PERIOD]?.toInt() ?: 30
        return Totp(name, account, secret, digits, period)
    }
}