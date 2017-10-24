package uk.gov.hmcts.reform.draftstore.actions.setup

import java.util.Base64

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames._
import io.gatling.http.HeaderValues._
import io.gatling.http.Predef._


import scala.util.Random

object Idam {

  private val idamUrl = ConfigFactory.load().getString("auth.idam.url")

  private val randomEmailFeeder =
    Iterator.continually(
      Map("email" -> (Random.alphanumeric.take(20).mkString + "@example.com"))
    )

  val registerAndSignIn: ChainBuilder =
    feed(randomEmailFeeder)
      .exec(
        http("Create IDAM account")
          .post(idamUrl + "/testing-support/accounts")
          .header(ContentType, ApplicationJson)
          .body(StringBody(
            """
              {
                "email": "${email}",
                "forename": "John",
                "surname": "Smith",
                "password": "password"
              }
            """
          ))
      )
      .exec(session => {
        session.set("loginHeader", buildLoginHeader(session("email").as[String], "password"))
      })
      .exec(
        http("Sign in to IDAM")
          .post(idamUrl + "/oauth2/authorize")
          .header(Authorization, "${loginHeader}")
          .check(jsonPath("$['access-token']").saveAs("user_token"))
      )

  def buildLoginHeader(email: String, password: String) : String = {
    "Basic " + Base64.getEncoder.encodeToString(s"$email:$password".getBytes)
  }
}
