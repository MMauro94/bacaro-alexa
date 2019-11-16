import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import org.apache.jena.rdf.model.Resource


val ALLOWED_PROPERTIES = mapOf(
    "telephone" to listOf("telephone", "telephone number", "number", "phone", "phone number"),
    "address" to listOf("address", "street"),
    "description" to listOf("description"),
    "name" to listOf("name"),
    "geo" to listOf("coordinates"),
    "email" to listOf("email", "e-mail", "e mail"),
    "faxNumber" to listOf("fax number")
)

data class Property(val resource: Resource, val names: List<String>) {

    fun typeSlotType(): JsonObject {
        return JsonObject(
            mapOf(
                "id" to resource.uri,
                "name" to JsonObject(
                    mapOf(
                        "value" to names[0],
                        "synonyms" to JsonArray(names.drop(1))
                    )
                )
            )
        )
    }
}

