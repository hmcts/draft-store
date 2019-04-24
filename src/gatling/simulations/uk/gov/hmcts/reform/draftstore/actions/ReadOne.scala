package uk.gov.hmcts.reform.draftstore.actions

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object ReadOne {

  val readOne: ChainBuilder =
    exec(
      http("Read created draft")
        .get(url = "/${id}")
        .headers(Map(
          "ServiceAuthorization" -> "Bearer ${service_token}",
          "Authorization" -> "Bearer ${user_token}",
          "Secret" -> "${secret}"
        ))
    )
}
