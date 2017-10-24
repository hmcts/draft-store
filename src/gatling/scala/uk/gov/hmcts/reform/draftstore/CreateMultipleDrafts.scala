package uk.gov.hmcts.reform.draftstore

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.draftstore.actions.Create.create
import uk.gov.hmcts.reform.draftstore.actions.ReadOne.readOne
import uk.gov.hmcts.reform.draftstore.actions.setup.Idam
import uk.gov.hmcts.reform.draftstore.actions.setup.LeaseServiceToken.leaseServiceToken

import scala.concurrent.duration._

class CreateMultipleDrafts extends Simulation {

  val config = ConfigFactory.load()

  val httpProtocol =
    http
      .baseURL(config.getString("baseUrl"))
      .contentTypeHeader("application/json")

  val scn =
    scenario("Create multiple drafts")
      .exec(leaseServiceToken())
      .exec(Idam.registerAndSignIn)
      .during(1.minute)(
        exec(
          create,
          readOne,
          pause(2.seconds)
        )
      )

  setUp(scn.inject(rampUsers(100).over(5.seconds))).protocols(httpProtocol)
}
