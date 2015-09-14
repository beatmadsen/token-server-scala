package com.madsen.tokenserver.server

import akka.actor.{ActorSystem, Props}

/**
 * Created by erikmadsen on 14/09/2015.
 */
object EchoServerApp extends App {

  val system = ActorSystem("echo-service-system")

  system.actorOf(Props[EchoServer], "echo-service")

  readLine("Press ENTER to exit")

  system.shutdown()
}
