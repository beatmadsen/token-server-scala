package com.madsen.tokenserver.json

import play.api.libs.json._

/**
 * Created by erikmadsen on 15/09/2015.
 */
object SimpleTest extends App {

  val s: JsValue = Json.toJson(SumoMessage("abc", Seq(Baz(0.2f), Bar(54))))

  println(Json.prettyPrint(s))

  val json: JsResult[SumoMessage] = Json.fromJson[SumoMessage](s)
  val sumoMessage: Option[SumoMessage] = json.asOpt

  sumoMessage foreach { m ⇒
    println(m)
  }
}


object SumoMessage {
  implicit val format: Format[SumoMessage] = Json.format[SumoMessage]
}

case class SumoMessage(name: String, foos: Seq[Foo])

object Foo {
  implicit val barFmt = Json.format[Bar]
  implicit val bazFmt = Json.format[Baz]
  implicit val fooFmt: Format[Foo] = new Format[Foo] {

    def reads(json: JsValue): JsResult[Foo] = json match {
      case JsObject(Seq(("class", JsString(name)), ("data", data))) ⇒
        name match {
          case "Bar" ⇒ Json.fromJson[Bar](data)(barFmt)
          case "Baz" ⇒ Json.fromJson[Baz](data)(bazFmt)
          case _ ⇒ JsError(s"Unknown class '$name'")
        }

      case _ ⇒ JsError(s"Unexpected JSON value $json")
    }


    def writes(foo: Foo): JsValue = {
      val (prod: Product, sub) = foo match {
        case b: Bar ⇒ (b, Json.toJson(b)(barFmt))
        case b: Baz ⇒ (b, Json.toJson(b)(bazFmt))
      }
      JsObject(Seq("class" -> JsString(prod.productPrefix), "data" -> sub))
    }
  }
}

sealed trait Foo

case class Bar(i: Int) extends Foo

case class Baz(f: Float) extends Foo