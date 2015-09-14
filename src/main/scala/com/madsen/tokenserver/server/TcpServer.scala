package com.madsen.tokenserver.server

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString

/**
 * Created by erikmadsen on 14/09/2015.
 */
class TcpServer(
  private val port: Int,
  private val delegate: ActorRef
) extends Actor {

  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", port))


  override def receive: Receive = {
    case Bound(localAddress) ⇒ println(s"bound at $localAddress")

    case CommandFailed(_: Bind) ⇒ context stop self

    case Connected(remote, local) ⇒
      val connection: ActorRef = sender()
      connection ! Register(delegate)
  }
}

object TcpServer {

  def props(port: Int, delegate: ActorRef): Props = Props(classOf[TcpServer], port, delegate)
}


class AutoReply extends Actor {

  override def receive: Actor.Receive = {

    case Received(data) ⇒
      val reply: String = data.utf8String.trim match {
        case "ha" ⇒ "hillarious!\n"
        case _ ⇒ "try again!\n"
      }
      sender() ! Tcp.Write(ByteString(reply))

    case PeerClosed ⇒ context stop self
  }
}


object TcpServerApp extends App {

  val system = ActorSystem("echo-service-system")

  val autoReply = system.actorOf(Props[AutoReply], "auto-reply")

  system.actorOf(TcpServer.props(9000, autoReply))

  readLine("Press ENTER to exit")

  system.shutdown()
}
