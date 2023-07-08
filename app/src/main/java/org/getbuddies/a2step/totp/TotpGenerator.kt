package org.getbuddies.a2step.totp

import java.security.GeneralSecurityException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TotpGenerator {
    private const val TIME_STAMP_ZERO: Long = 0
    private const val TIME_STEP_DEFAULT: Long = 30

    @Throws(OtpSourceException::class)
    fun generateNow(secret: String?): String {
        val otpState = getValueAtTime(System.currentTimeMillis() / 1000)
        return computePin(secret, otpState)
    }

    @Throws(OtpSourceException::class)
    private fun computePin(secret: String?, otpState: Long): String {
        if (secret.isNullOrEmpty()) {
            throw OtpSourceException("null or empty secret")
        }
        return try {
            val signer = getSigningOracle(secret)
            val pcg = PasscodeGenerator(signer, 6)
            pcg.generateResponseCode(otpState)
        } catch (e: GeneralSecurityException) {
            throw OtpSourceException("Crypto failure", e)
        }
    }

    private fun getValueAtTime(time: Long): Long {
        return getValueAtTime(time, TIME_STAMP_ZERO, TIME_STEP_DEFAULT)
    }

    private fun getValueAtTime(time: Long, startTime: Long, timeStep: Long): Long {
        val timeSinceStartTime = time - startTime
        return if (timeSinceStartTime >= 0) {
            timeSinceStartTime / timeStep
        } else {
            (timeSinceStartTime - (timeStep - 1)) / timeStep
        }
    }

    private fun getSigningOracle(secret: String): PasscodeGenerator.Signer {
        val keyBytes = Base32String.decode(secret)
        val mac = Mac.getInstance("HMACSHA1")
        mac.init(SecretKeySpec(keyBytes, ""))
        return object : PasscodeGenerator.Signer {
            override fun sign(data: ByteArray?): ByteArray {
                return mac.doFinal(data)
            }

        }
    }

    class OtpSourceException : Exception {
        constructor(message: String?) : super(message) {}
        constructor(message: String?, cause: Throwable?) : super(message, cause) {}
    }
}