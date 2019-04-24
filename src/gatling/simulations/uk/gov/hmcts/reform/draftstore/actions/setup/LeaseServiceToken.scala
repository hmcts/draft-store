package uk.gov.hmcts.reform.draftstore.actions.setup

import com.typesafe.config.ConfigFactory
import com.warrenstrange.googleauth.GoogleAuthenticator

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames._
import io.gatling.http.HeaderValues._
import io.gatling.http.Predef._

import scala.util.Random

object LeaseServiceToken {

  val config = ConfigFactory.load()

  private val s2sUrl =  config.getString("auth.s2s.url")
  private val s2sTesting = config.getBoolean("auth.s2s.testing")
  private val s2sSecret = config.getString("auth.s2s.secret")

  private val authenticator = new GoogleAuthenticator()

  private val serviceConcreteNameFeeder =
    Iterator.continually(
      Map(
        "totp" -> authenticator.getTotpPassword(s2sSecret)
      )
    )

  /**
    * Calls S2S service to retrieve service token later used in auth headers sent to draft-store
    */
  private val leaseConcreteServiceToken: ChainBuilder =
    feed(serviceConcreteNameFeeder)
      .exec(
        http("Lease service token")
          .post(s2sUrl + "/lease")
          .header(ContentType, ApplicationJson)
          .header(Accept, TextPlain)
          .body(StringBody(
            """
                {
                  "microservice": "DRAFT_STORE_TESTS",
                  "oneTimePassword": "${totp}"
                }
            """
          ))
          .check(bodyString.saveAs("service_token"))
      )


  private val serviceTestNameFeeder =
    Iterator.continually(Map("service_name" -> ("service_" + Random.nextInt(10))))

  /**
    * Calls S2S testing support service to retrieve (a fake) service token
    */
  private val leaseTestServiceToken: ChainBuilder =
    feed(serviceTestNameFeeder)
      .exec(
        http("Lease service token")
          .post(s2sUrl + "/testing-support/lease")
          .header(ContentType, ApplicationJson)
          .header(Accept, TextPlain)
          .body(StringBody(
            """
                {
                  "microservice": "${service_name}"
                }
            """
          ))
          .check(bodyString.saveAs("service_token"))
      )


  val leaseServiceToken: ChainBuilder = if(s2sTesting) leaseTestServiceToken else leaseConcreteServiceToken

}
