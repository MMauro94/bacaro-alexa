import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import java.io.File

fun main() {
    val types = types()
    val entities = types.map {
        it.entitySlotTypes()
    }
    val entityTypes = JsonObject(
        mapOf(
            "name" to "entity_types",
            "values" to JsonArray(types.map {
                it.typeSlotType()
            })
        )
    )
    val propertiesByEntityType = types.map { type ->
        JsonObject(
            mapOf(
                "name" to type.propertiesSlotName,
                "values" to JsonArray(type.properties.map { prop ->
                    prop.typeSlotType()
                })
            )
        )
    }
    val jTypes = JsonArray(entities + entityTypes + propertiesByEntityType)


    val propertyGetIntents = types.map { type ->
        val name = "property_get_${type.name}"
        val propName = "property_$name"
        val entityName = "entity_$name"
        Intent(
            name = name,
            slots = listOf(
                IntentSlot(propName, type.propertiesSlotName),
                IntentSlot(entityName, type.slotName)
            ),
            samples = listOf(
                "{$propName} {$entityName}"
            )
        )
    }
    val intents = propertyGetIntents + CANCEL_INTENT + STOP_INTENT

    val jIntents = JsonArray(intents.map { it.toJson() })
    val j = JsonObject(
        mapOf(
            "interactionModel" to JsonObject(
                mapOf(
                    "languageModel" to JsonObject(
                        mapOf(
                            "invocationName" to "bacaro tour",
                            "types" to jTypes,
                            "intents" to jIntents
                        )
                    )
                )
            )
        )
    )
    File("json.json").writer().use {
        it.write(j.toJsonString(prettyPrint = true))
    }
}