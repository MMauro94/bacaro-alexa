import com.beust.klaxon.JsonArray
import java.io.File

fun printSlotTypes() {
    val entities = types().map {
        it.entitySlotType()
    }
    val j = JsonArray(entities)
    File("types-entitytypes.json").writer().use {
        it.write(j.toJsonString(prettyPrint = true))
    }
}