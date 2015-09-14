package com.madsen.tokenserver.server

import akka.actor.Actor
import akka.io.Tcp._

/**
 * Created by erikmadsen on 14/09/2015.
 */
class TcpEcho extends Actor {


  override def receive: Receive = {

    case Received(data) ⇒ sender() ! Write(data)
    case PeerClosed ⇒ context stop self
  }
}
