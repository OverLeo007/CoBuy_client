package ru.hihit.cobuy.api.errors

import java.io.IOException


class NetworkException(message: String, cause: Throwable) : IOException(message, cause)
