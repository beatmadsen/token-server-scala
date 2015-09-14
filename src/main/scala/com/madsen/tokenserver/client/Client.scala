package com.madsen.tokenserver.client

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString

/**
 * Created by erikmadsen on 14/09/2015.
 */
object Client {

  def props(remote: InetSocketAddress, replies: ActorRef): Props = Props(classOf[Client], remote, replies)
}

class Client(val remote: InetSocketAddress, val listener: ActorRef) extends Actor {

  import context.system

  IO(Tcp) ! Connect(remote)


  override def receive: Receive = {

    case CommandFailed(_: Connect) ⇒
      listener ! "connect failed"
      context stop self

    case c @ Connected(remoteAddress, local) ⇒
      listener ! c
      val connection: ActorRef = sender()
      connection ! Register(self)

      context become postInitBehaviour(connection)
  }


  private def postInitBehaviour(connection: ActorRef): Receive = {
    case data: ByteString ⇒
      connection ! Write(data)
    case CommandFailed(w: Write) ⇒
      // OS buffer was full
      listener ! "write failed"
    case Received(data) ⇒
      listener ! data
    case "close" ⇒
      connection ! Close
    case _: ConnectionClosed ⇒
      listener ! "connection closed"
      context stop self
  }
}

