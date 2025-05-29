package hr.pet.api

import java.io.IOException

class TokenRepository(private val petApi: PetApi) {
    @Volatile private var token: String?  = null
    @Volatile private var expiresAt: Long = 0

    /**
     * Synchronously fetches (or reuses) the access token.
     * Must be called off the main thread (e.g. inside an Interceptor).
     */
    @Synchronized
    fun getToken(): String {
        val now = System.currentTimeMillis()
        // reuse if still valid
        if (token != null && now < expiresAt) {
            return token!!
        }

        // else fetch a new one
        val resp = petApi.getToken().execute()
        if (!resp.isSuccessful) {
            throw IOException("Token fetch failed: ${resp.code()} ${resp.message()}")
        }
        val body = resp.body()!!
        token     = body.accessToken
        // expiresIn is in seconds
        expiresAt = now + body.expiresIn * 1_000
        return token!!
    }
}