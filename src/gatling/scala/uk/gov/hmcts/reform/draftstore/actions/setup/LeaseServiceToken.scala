package uk.gov.hmcts.reform.draftstore.actions.setup

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames._
import io.gatling.http.HeaderValues._
import io.gatling.http.Predef._

import scala.util.Random

object LeaseServiceToken {

  private val s2sUrl = ConfigFactory.load().getString("auth.s2s.url")

  private val serviceNameFeeder =
    Iterator.continually(Map("service_name" -> ("service_" + Random.nextInt(10))))

  /**
    * Calls S2S service to retrieve service token later used in auth headers sent to draft-store
    */
  val leaseServiceToken: ChainBuilder =
    feed(serviceNameFeeder)
      .exec(
        http("Lease service token")
          .post(s2sUrl + "/testing-support/lease")
          .header(ContentType, ApplicationFormUrlEncoded)
          .formParam("microservice", "${service_name}")
          .check(bodyString.saveAs("service_token"))
      )
}
