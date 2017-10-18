package uk.gov.hmcts.reform.draftstore.actions

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Create {

  val create: ChainBuilder =
    exec(
      http("Create draft")
        .post(url = "")
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
