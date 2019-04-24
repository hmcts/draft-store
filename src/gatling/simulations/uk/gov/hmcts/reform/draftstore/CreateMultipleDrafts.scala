package uk.gov.hmcts.reform.draftstore

import java.util.UUID

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.draftstore.actions.Create.create
import uk.gov.hmcts.reform.draftstore.actions.Update.update
import uk.gov.hmcts.reform.draftstore.actions.DeleteAll.deleteAll
import uk.gov.hmcts.reform.draftstore.actions.ReadOne.readOne
import uk.gov.hmcts.reform.draftstore.actions.ReadAll.readAll
import uk.gov.hmcts.reform.draftstore.actions.setup.Idam
import uk.gov.hmcts.reform.draftstore.actions.setup.LeaseServiceToken.leaseServiceToken
import uk.gov.hmcts.reform.draftstore.utils.{IdamUserHolder, User}

import scala.concurrent.duration._

class CreateMultipleDrafts extends Simulation {

  val config = ConfigFactory.load()

  private val testUsers = config.getInt("params.testUsers")
  private val testRampUpSecs = config.getInt("params.testRampUpSecs")
  private val testCleanUpDelaySecs = config.getInt("params.testCleanUpDelaySecs")


  val httpProtocol =
    http
      .baseURL(config.getString("baseUrl"))
      .contentTypeHeader("application/json")

  private val secretFeeder = Iterator.continually(Map("secret" -> UUID.randomUUID.toString))

  val createAndReadDrafts =
    scenario("Register and create multiple drafts")
      .feed(secretFeeder)
      .exec(Idam.registerAndSignIn)
        .pause(2 second, 5 seconds)
        .exec(session => {
          Map(
            "email" -> session("email").as[String],
            "user_token" -> session("user_token").as[String]
          )
          IdamUserHolder.add(User(session("email").as[String], session("user_token").as[String]))
          session
        })
      .exec(leaseServiceToken)
      .during(2.minute)(
        exec(
          create,
          pause(2.seconds, 5.seconds),
          readOne,
          pause(2.seconds, 5.seconds),
          update,
          pause(2.seconds, 5.seconds),
          readAll,
          pause(2.seconds, 5.seconds),
          update,
          pause(2.seconds, 5.seconds),
          readAll,
          pause(2.seconds, 5.seconds),
          readAll,
          pause(2.seconds, 5.seconds),
          update,
          pause(2.seconds, 5.seconds),
          readAll,
          pause(2.seconds, 5.seconds),
          readAll,
          pause(2.seconds, 5.seconds)
        )
      )

  val deleteDraftsAndUser =
    scenario("Delete all drafts")
      .asLongAs(_ => IdamUserHolder.hasElement()) {
        feed(
          Iterator.continually(
            IdamUserHolder
              .poll()
              .map(user =>
                Map(
                  "email" -> user.email,
                  "user_token" -> user.token
                )
              )
          ).takeWhile(_.nonEmpty).flatten
        )
        .exec(deleteAll)
        .pause(2.seconds, 4.seconds)
        .exec(Idam.deleteAccount)
      }

  val draftsAndCleanUp =
    scenario("Use draft store and then clean up")
        .exec(createAndReadDrafts)
        .pause(testCleanUpDelaySecs.seconds)
        .exec(deleteDraftsAndUser)

  setUp(
    // Load test over 1 hour - settings
    draftsAndCleanUp.inject(rampUsers(testUsers).over(testRampUpSecs.seconds))
    //createAndReadDrafts.inject(rampUsers(3000).over(60.minutes))
    // Regression (pipeline) - settings
    //draftsAndCleanUp.inject(rampUsers(300).over(360.seconds))
    // Single user
    //draftsAndCleanUp.inject(rampUsers(1).over(1.seconds))
  ).protocols(httpProtocol)

}
