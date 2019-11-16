import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import org.apache.jena.rdf.model.Resource

val ALLOWED_ENTITY_TYPES = mapOf(
    "Restaurant" to listOf("Restaurant"),
    "FastFoodRestaurant" to listOf("Fast Food", "Fast-Food", "Fast food restaurant"),
    "BarOrPub" to listOf("Bar", "Pub"),
    "Winery" to listOf("Winery"),
    "IceCreamShop" to listOf("Ice cream shop", "Icecream shop", "Ice cream parlor", "Icecream parlor")
)

data class Type(val resource: Resource, val names: List<String>) {

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
    val propertiesSlotName = "properties_of_$name"

    override fun toString() = name

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

    val slotName = "entity_type_" + resource.localName.toString()

    fun entitySlotTypes(): JsonObject {
        return JsonObject(
            mapOf(
                "name" to slotName,
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

    val properties by lazy {
        sparql(
            """
                select distinct ?property
                where {
                         ?instance a <${resource.uri}> . 
                         ?instance ?property ?obj .
                }
            """.trimIndent()
        ).selectMap {
            val property = it.getResource("property")
            val names = ALLOWED_PROPERTIES[property.localName]
            if (names != null) {
                Property(property, names)
            } else null
        }.filterNotNull()
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
        val type = it.getResource("type")
        val names = ALLOWED_ENTITY_TYPES[type.localName]
        if (names != null) {
            Type(type, names)
        } else null
    }.filterNotNull()
}
