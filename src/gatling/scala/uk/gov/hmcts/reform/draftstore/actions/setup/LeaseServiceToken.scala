package uk.gov.hmcts.reform.draftstore.actions.setup

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames._
import io.gatling.http.HeaderValues._
import io.gatling.http.Predef._

import scala.util.Random

object LeaseServiceToken {

  private val url = ConfigFactory.load().getString("auth.s2s.leaseUrl")

  /**
    * Calls S2S service to retrieve service token later used in auth headers sent to draft-store
    */
  def leaseServiceToken(): ChainBuilder =
    exec(
      http("Lease service token")
        .post(url)
        .header(ContentType, ApplicationFormUrlEncoded)
        .formParam("microservice", generateRandomServiceName())
        .check(bodyString.saveAs("service_token"))
    )

  private def generateRandomServiceName(): String = {
    s"some_service_${Random.nextInt(Integer.MAX_VALUE)}"
  }
}
