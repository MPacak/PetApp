package hr.pet.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor (private val tokenRepo: TokenRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val bearer = "Bearer ${tokenRepo.getToken()}"
        val request = chain.request().newBuilder()
            .addHeader("Authorization", bearer)
            .build()
        return chain.proceed(request)
    }
}