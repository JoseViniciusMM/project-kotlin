package br.com.filacidada.dtos.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import br.com.filacidada.utils.AppJson

@Serializable
data class ApiResponse(
    val success: Boolean,
    val data: JsonElement? = null,
    val message: String? = null,
    val errors: List<String>? = null
) {
    companion object {
        inline fun <reified T> success(data: T, message: String? = null) = ApiResponse(
            success = true,
            data = AppJson.encodeToJsonElement(kotlinx.serialization.serializer<T>(), data),
            message = message
        )

        fun error(message: String, errors: List<String>? = null) = ApiResponse(
            success = false,
            message = message,
            errors = errors
        )
    }
}