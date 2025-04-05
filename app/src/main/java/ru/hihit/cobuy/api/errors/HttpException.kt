package ru.hihit.cobuy.api.errors

class HttpException(val code: Int, val body: String?) : Exception("HTTP $code: $body")