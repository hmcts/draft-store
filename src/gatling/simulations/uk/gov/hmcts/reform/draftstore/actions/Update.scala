package uk.gov.hmcts.reform.draftstore.actions

import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.util.Random


object Update {

  val update: ChainBuilder =
    exec(
      http("Update draft")
        .put(url = "/${id}")
        .headers(Map(
          "ServiceAuthorization" -> "Bearer ${service_token}",
          "Authorization" -> "Bearer ${user_token}",
          "Secret" -> "${secret}"
        ))
        .body(
          StringBody(
            s"""
              |{
              |  "document": {
              |    "a": "some updated value",
              |    "b": "some other updated value",
              |    "c": "yet another update",
              |    "nested": {
              |      "xxx": "zzz"
              |    }
              |  },
              |  "type": "my updated type"
              |}
            """.stripMargin
          )
        )
        .check(status.is(204))
    )
}
