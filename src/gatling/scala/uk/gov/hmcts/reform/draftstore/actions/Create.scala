package uk.gov.hmcts.reform.draftstore.actions

import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Create {

  private val secretFeeder = Iterator.continually(Map("secret" -> UUID.randomUUID.toString))

  val create: ChainBuilder =
    feed(secretFeeder)
      .exec(
        http("Create draft")
          .post(url = "")
          .headers(Map(
            "ServiceAuthorization" -> "Bearer ${service_token}",
            "Authorization" -> "Bearer ${user_token}",
            "Secret" -> "${secret}"
          ))
          .body(
            StringBody(
              """
                |{
                |  "document": {
                |    "a": "some value",
                |    "b": "some other value",
                |    "c": "yet another",
                |    "nested": {
                |      "xxx": "yyy"
                |    }
                |  },
                |  "type": "my type"
                |}
              """.stripMargin
            )
          )
          .check(headerRegex("Location", """/(\d+)$""").saveAs("id"))
      )
}
