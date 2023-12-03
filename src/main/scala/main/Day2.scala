package main

import java.io.IOException
import zio.*
import zio.stream.*
import scala.annotation.tailrec

object Day2 extends ZIOAppDefault {
  final case class Game(id: Int, draws: Set[Map[String, Int]])

  val stream =
    ZStream.fromFileName("day2Input.txt").via(ZPipeline.utf8Decode >>> ZPipeline.splitLines)

  private def parseGame(input: String): Set[Map[String, Int]] = {
    @tailrec
    def loop(input: List[String], acc: Set[Map[String, Int]]): Set[Map[String, Int]] = {
      input match {
        case Nil => acc
        case head :: tail => loop(tail, acc + parseDraw(head))
      }
    }
    loop(input.split(";").toList, Set.empty[Map[String, Int]])
  }

  private def addToMap(key: String, value: Int, map: Map[String, Int]): Map[String, Int] =
    map.updatedWith(key) {
      case None => Some(value)
      case Some(oldValue) => Some(oldValue + value)
    }

  private def parseDraw(input: String): Map[String, Int] = {
    @tailrec
    def loop(current: List[String], acc: Map[String, Int]): Map[String, Int] = {
      if (current.isEmpty) acc
      else
        current match {
          case s"$num blue" :: tail => loop(tail, addToMap("blue", num.toInt, acc))
          case s"$num red" :: tail => loop(tail, addToMap("red", num.toInt, acc))
          case s"$num green" :: tail => loop(tail, addToMap("green", num.toInt, acc))
          case _ => acc
        }
    }
    loop(input.split(",").toList.map(_.strip()), Map.empty[String, Int])
  }

  val pipeLine: ZPipeline[Any, RuntimeException, String, (Int, Set[Map[String, Int]])] =
    ZPipeline.mapZIO { line =>
      for {
        separated <- ZIO.succeed(line.split(":"))
        gameId <- separated.headOption match {
          case Some(s"Game $id") => ZIO.succeed(id.toInt)
          case _ => ZIO.fail(new RuntimeException("malformed input file!"))
        }
        drawStrings = separated(1).split(";")
        sets = drawStrings.flatMap(setString => parseGame(setString)).toSet
      } yield (gameId -> sets)
    }

  val sink =
    ZSink.collectAll[(Int, Set[Map[String, Int]])].mapZIO { lines =>
      ZIO.foreach(lines)(line => Console.printLine(s"id: ${line._1}, first: ${line._2}"))
    }

  val run = stream.via(pipeLine).run(sink)
}
