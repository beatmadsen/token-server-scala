package com.madsen.tokenserver.json

import play.api.libs.functional.syntax._
import play.api.libs.json._

// Combinator syntax

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

  implicit val fooFormat = Foo.fooFmt
  implicit val format: Format[SumoMessage] = Json.format[SumoMessage]
}

case class SumoMessage(name: String, foos: Seq[Foo])

object Foo {

  val fooReads: Reads[Foo] = (
    (JsPath \ "fooType").read[String] and
      (JsPath \ "value").read[Double]
    ) { (s, d) ⇒

    s match {
      case "bar" ⇒ Bar(d.toInt)
      case "baz" ⇒ Baz(d.toFloat)
    }
  }

  val fooWrites: Writes[Foo] = (
    (JsPath \ "fooType").write[String] and
      (JsPath \ "value").write[Double]
    ) { foo: Foo ⇒
    foo match {
      case Bar(i) ⇒ ("bar", i.toDouble)
      case Baz(f) ⇒ ("baz", f.toDouble)
    }
  }

  val fooFmt = Format(fooReads, fooWrites)

}

sealed trait Foo

case class Bar(i: Int) extends Foo

case class Baz(f: Float) extends Foo