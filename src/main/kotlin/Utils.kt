import org.apache.jena.query.QueryExecution
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.query.QuerySolution
import org.apache.jena.riot.system.PrefixMapStd
import org.apache.jena.riot.system.PrefixMapUnmodifiable

val SPARQL_SERVICE = "https://sparql.opendatahub.testingmachine.eu/sparql"
val PREFIX_MAP = PrefixMapUnmodifiable(PrefixMapStd().apply {
    add("schema", "http://schema.org/")
    add("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    add("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
})

fun PrefixMapUnmodifiable.prefixes() = PREFIX_MAP.mapping.map { (prefix, iri) ->
    "PREFIX $prefix: <$iri>"
}.joinToString("\n")

fun QueryExecution.select(block: (QuerySolution) -> Unit) = use {
    val q = it.execSelect()
    while (q.hasNext()) {
        block(q.next())
    }
}

fun <T> QueryExecution.selectMap(transform: (QuerySolution) -> T): List<T> = use {
    val ret = mutableListOf<T>()
    select {
        ret.add(transform(it))
    }
    return ret
}

fun sparql(query: String): QueryExecution {
    return QueryExecutionFactory.sparqlService(
        SPARQL_SERVICE, """
            ${PREFIX_MAP.prefixes()}
            $query
        """.trimIndent()
    )!!
}


