package uk.gov.hmcts.reform.draftstore.utils

import java.util.concurrent.LinkedBlockingDeque

/**
  * Stores IDAM user token to use between scenarios.
  */
object IdamTokensHolder {

  private val deque = new LinkedBlockingDeque[String]()

  def push(token: String): Unit = deque.push(token)
  def pop() : String = deque.pop()

}
