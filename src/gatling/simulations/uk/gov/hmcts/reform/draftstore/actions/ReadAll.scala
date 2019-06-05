package uk.gov.hmcts.reform.draftstore.actions

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object ReadAll {

  val readAll: ChainBuilder =
    exec(
      http("Read all drafts")
        .get(url = "/")
        .headers(Map(
          "ServiceAuthorization" -> "Bearer ${service_token}",
          "Authorization" -> "Bearer ${user_token}",
          "Secret" -> "${secret}"
        ))
    )
}
