package com.madsen.tokenserver.json

import play.api.libs.json.Json

/**
 * Created by erikmadsen on 15/09/2015.
 */
object SimpleTest extends App {

  val s = Json.toJson(SumoMessage("abc")).toString()
  val json = Json.parse(s)
  val sumoMessage: Option[SumoMessage] = json.asOpt[SumoMessage]

  sumoMessage foreach { m ⇒
    println(m)
  }
}


object SumoMessage {
  implicit val format = Json.format[SumoMessage]
}

case class SumoMessage(name: String)

