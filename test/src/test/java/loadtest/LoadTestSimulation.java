package loadtest;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoadTestSimulation extends Simulation {
    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8092");

    ScenarioBuilder scn = scenario("Order a lot")
            .repeat(100)
            .on(exec(http("post request").post("/orders?accountId=6&productId=6")));

    {
        setUp(
                scn.injectOpen(atOnceUsers(100))
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(2000)
                );
    }
}
