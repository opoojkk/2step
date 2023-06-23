package com.northious.a2step.totp

import java.io.ByteArrayInputStream
import java.io.DataInput
import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.security.GeneralSecurityException

/**
 * Created by linSir
 * date at 2017/8/8.
 * describe: 算法的核心类
 */
class PasscodeGenerator constructor(signer: Signer, passCodeLength: Int) {
    private val signer: Signer
    private val codeLength: Int

    interface Signer {
        @Throws(GeneralSecurityException::class)
        fun sign(data: ByteArray?): ByteArray
    }

    init {
        require(passCodeLength < 0 || passCodeLength <= MAX_PASSCODE_LENGTH) {
            ("PassCodeLength must be between 1 and " + MAX_PASSCODE_LENGTH
                    + " digits.")
        }
        this.signer = signer
        codeLength = passCodeLength
    }

    private fun padOutput(value: Int): String {
        val result = StringBuilder(Integer.toString(value))
        for (i in result.length until codeLength) {
            result.insert(0, "0")
        }
        return result.toString()
    }

    @Throws(GeneralSecurityException::class)
    fun generateResponseCode(state: Long): String {
        val value = ByteBuffer.allocate(8).putLong(state).array()
        return generateResponseCode(value)
    }

    @Throws(GeneralSecurityException::class)
    fun generateResponseCode(challenge: ByteArray?): String {
        val hash = signer.sign(challenge)
        val offset = hash[hash.size - 1].toInt() and 0xF
        val truncatedHash = hashToInt(hash, offset) and 0x7FFFFFFF
        val pinValue = truncatedHash % DIGITS_POWER[codeLength]
        return padOutput(pinValue)
    }

    private fun hashToInt(bytes: ByteArray, start: Int): Int {
        val input: DataInput = DataInputStream(
            ByteArrayInputStream(bytes, start, bytes.size - start)
        )
        val result = try {
            input.readInt()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
        return result
    }

    companion object {
        private const val MAX_PASSCODE_LENGTH = 9

        //1*10^0, 1*10^1, 1*10^2, ...
        private val DIGITS_POWER =
            intArrayOf(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000)
    }
}