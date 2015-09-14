package com.example

import java.net.InetSocketAddress

import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import akka.io._

class PongActor extends Actor with ActorLogging {

  import PongActor._

  def receive = {
    case PingActor.PingMessage(text) =>
      log.info("In PongActor - received message: {}", text)
      sender() ! PongMessage("pong")
  }
}

object PongActor {
  val props = Props[PongActor]

  case class PongMessage(text: String)

}



