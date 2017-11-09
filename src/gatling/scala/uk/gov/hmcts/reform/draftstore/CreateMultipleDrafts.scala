package uk.gov.hmcts.reform.draftstore

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.draftstore.actions.Create.create
import uk.gov.hmcts.reform.draftstore.actions.DeleteAll.deleteAll
import uk.gov.hmcts.reform.draftstore.actions.ReadOne.readOne
import uk.gov.hmcts.reform.draftstore.actions.setup.Idam
import uk.gov.hmcts.reform.draftstore.actions.setup.LeaseServiceToken.leaseServiceToken
import uk.gov.hmcts.reform.draftstore.utils.{IdamUserHolder, User}

import scala.concurrent.duration._

class CreateMultipleDrafts extends Simulation {

  val config = ConfigFactory.load()

  val httpProtocol =
    http
      .baseURL(config.getString("baseUrl"))
      .contentTypeHeader("application/json")

  val registerAndSignIn =
    scenario("Register and sign in")
      .exec(Idam.registerAndSignIn)
      .exec(session => {
        IdamUserHolder.push(User(session("email").as[String], session("user_token").as[String]))
        session
      })

  val createAndReadDrafts =
    scenario("Create multiple drafts")
      .feed(Iterator.continually({
        val user = IdamUserHolder.pop()
        Map(
          "email" -> user.email,
          "user_token" -> user.token
        )
      }))
      .exec(leaseServiceToken)
      .during(1.minute)(
        exec(
          create,
          readOne,
          pause(2.seconds)
        )
      )
      .exec(deleteAll)
      .exec(Idam.deleteAccount)

  setUp(
    registerAndSignIn.inject(rampUsers(100).over(10.seconds)),
    createAndReadDrafts.inject(nothingFor(15.seconds), rampUsers(100).over(5.seconds))
  ).protocols(httpProtocol)
}
