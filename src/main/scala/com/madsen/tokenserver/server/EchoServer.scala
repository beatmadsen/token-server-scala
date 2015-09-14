package com.madsen.tokenserver.server

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}

/**
 * Created by erikmadsen on 14/09/2015.
 */
class EchoServer extends Actor {

  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 9000))


  override def receive: Receive = {
    case b @ Bound(localAddress) ⇒ println(s"bound at $localAddress")

    case CommandFailed(_: Bind) ⇒ context stop self

    case c @ Connected(remote, local) ⇒
      val handler: ActorRef = context actorOf Props[TcpEcho]
      val connection: ActorRef = sender()

      connection ! Register(handler)
  }
}





