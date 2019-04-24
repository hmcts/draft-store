package uk.gov.hmcts.reform.draftstore.utils

import java.util.concurrent.LinkedBlockingDeque

/**
  * Stores IDAM user email and token to use between scenarios.
  */
object IdamUserHolder {

  private val deque = new LinkedBlockingDeque[User]()

  def push(user: User): Unit = deque.push(user)

  def pop(): Option[User] = synchronized {
    deque.peekFirst() match {
      case null => None
      case _ => Some(deque.pop())
    }
  }

  def add(user: User): Unit = deque.add(user)

  def poll(): Option[User] = synchronized {
    deque.peek() match {
      case null => None
      case _ => Some(deque.poll())
    }
  }

  def hasElement(): Boolean = deque.peek() != null

  def size(): Int = deque.size()

}

case class User(email: String, token: String)
