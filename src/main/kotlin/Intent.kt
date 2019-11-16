import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

class Intent(
    val name: String,
    val slots: List<IntentSlot>,
    val samples: List<String>
) {
    fun toJson() = JsonObject(
        mapOf(
            "name" to name,
            "slots" to JsonArray(slots.map { it.toJson() }),
            "samples" to JsonArray(samples)
        )
    )
}

class IntentSlot(val name: String, val type: String) {
    fun toJson() = JsonObject(
        mapOf(
            "name" to name,
            "type" to type
        )
    )
}

val CANCEL_INTENT = Intent("AMAZON.CancelIntent", emptyList(), emptyList())
val STOP_INTENT = Intent("AMAZON.StopIntent", emptyList(), emptyList())