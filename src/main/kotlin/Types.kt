import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import org.apache.jena.rdf.model.Resource

val ALLOWED_ENTITY_TYPES = listOf(
    "Restaurant",
    "FastFoodRestaurant",
    "BarOrPub",
    "Winery",
    "IceCreamShop"
)

data class Type(val resource: Resource) {

    fun selectAllNames(): List<Pair<Resource, String>> {
        println("Selecting all entities of type $this")
        return sparql(
            """
            SELECT ?e ?name
            WHERE {
              ?e rdf:type <${resource.uri}> ;
                   schema:name ?name
            }
        """.trimIndent()
        ).selectMap {
            val str = it.getLiteral("name").toString()
            if (str.endsWith("@en")) {
                it.getResource("e") to str.removeSuffix("@en")
            } else null
        }.filterNotNull().distinctBy { it.second }
    }

    val name = resource.localName!!

    override fun toString() = name

    fun typeSlotType(): JsonObject {
        return JsonObject(
            mapOf(
                "id" to resource.uri,
                "name" to JsonObject(
                    mapOf(
                        "value" to name
                    )
                )
            )
        )
    }

    fun entitySlotTypes(): JsonObject {
        return JsonObject(
            mapOf(
                "name" to "entity-type." + resource.localName.toString(),
                "values" to JsonArray(selectAllNames().map { (res, name) ->
                    JsonObject(
                        mapOf(
                            "id" to res.uri,
                            "name" to JsonObject(
                                mapOf(
                                    "value" to name
                                )
                            )
                        )
                    )
                })
            )
        )
    }
}

fun types(): List<Type> {
    println("Selecting types")
    return sparql(
        """
            SELECT DISTINCT ?type
            WHERE {
              ?e rdf:type ?type .
              FILTER(STRSTARTS(STR(?type),str(schema:)) )
            }
        """.trimIndent()
    ).selectMap {
        Type(it.getResource("type"))
    }.filter { it.name in ALLOWED_ENTITY_TYPES }
}
