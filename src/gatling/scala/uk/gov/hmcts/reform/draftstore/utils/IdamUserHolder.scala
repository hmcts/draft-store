package uk.gov.hmcts.reform.draftstore.utils

import java.util.concurrent.LinkedBlockingDeque

/**
  * Stores IDAM user email and token to use between scenarios.
  */
object IdamUserHolder {

  private val deque = new LinkedBlockingDeque[User]()

  def push(user: User): Unit = deque.push(user)
  def pop() : User = deque.pop()

}

case class User(email: String, token: String)
