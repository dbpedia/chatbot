import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class Test {
    public static void main(String[] args) {
        QueryEngineHTTP q = new QueryEngineHTTP("http://test.com", "SELECT * WHERE {?s ?p ?o}");
        q.addDefaultHeader("User-Agent", "TestClient");
        q.addParam("timeout", "30000");
    }
}
