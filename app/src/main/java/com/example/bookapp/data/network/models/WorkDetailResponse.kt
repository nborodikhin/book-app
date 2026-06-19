package com.example.bookapp.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class WorkDetailResponse(
    @SerialName("key") val key: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: JsonElement? = null,
    @SerialName("covers") val covers: List<Long>? = null
) {
    // OpenLibrary description is either a plain string or {"type":..., "value":"..."}
    fun synopsis(): String = when (val d = description) {
        null -> ""
        is JsonPrimitive -> if (d.isString) d.content else ""
        is JsonObject -> d["value"]?.jsonPrimitive?.content ?: ""
        else -> ""
    }

    fun coverUrl(): String? =
        covers?.firstOrNull()?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
}
