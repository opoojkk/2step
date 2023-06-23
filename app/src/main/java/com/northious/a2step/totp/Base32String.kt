package com.northious.a2step.totp

class Base32String constructor(alphabet: String) {
    private val MASK: Int
    private val SHIFT: Int
    private val mCharMap: HashMap<Char, Int> by lazy { HashMap() }

    init {
        val digits = alphabet.toCharArray()
        MASK = digits.size - 1
        SHIFT = Integer.numberOfTrailingZeros(digits.size)
        for (i in digits.indices) {
            mCharMap[digits[i]] = i
        }
    }

    @Throws(DecodingException::class)
    private fun decodeInternal(original: String): ByteArray {
        var encoded = original
        encoded = encoded.trim { it <= ' ' }
            .replace(SEPARATOR.toRegex(), "").replace(" ".toRegex(), "")
        encoded = encoded.replaceFirst("=*$".toRegex(), "")
        encoded = encoded.uppercase()
        if (encoded.isEmpty()) {
            return ByteArray(0)
        }
        val encodedLength = encoded.length
        val outLength = encodedLength * SHIFT / 8
        val result = ByteArray(outLength)
        var buffer = 0
        var next = 0
        var bitsLeft = 0
        for (c in encoded.toCharArray()) {
            if (!mCharMap.containsKey(c)) {
                throw DecodingException("Illegal character: $c")
            }
            buffer = buffer shl SHIFT
            buffer = buffer or (mCharMap[c]!! and MASK)
            bitsLeft += SHIFT
            if (bitsLeft >= 8) {
                result[next++] = (buffer shr bitsLeft - 8).toByte()
                bitsLeft -= 8
            }
        }
        return result
    }

    class DecodingException(message: String?) : Exception(message)
    companion object {
        private const val ALPHABET_DEFAULT = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

        // RFC
        private val instance: Base32String by lazy { Base32String(ALPHABET_DEFAULT) }
        private const val SEPARATOR = "-"

        @Throws(DecodingException::class)
        fun decode(encoded: String): ByteArray {
            return instance.decodeInternal(encoded)
        }
    }
}