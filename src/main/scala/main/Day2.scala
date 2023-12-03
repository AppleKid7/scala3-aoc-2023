package main

import java.io.IOException
import zio.*
import zio.stream.*

object Day2 extends ZIOAppDefault {

  enum Balls {
    case red, green, blue
  }

  final case class Game(id: Int, draws: Set[Map[String, Int]])

  val stream =
    ZStream.fromFileName("day2Input.txt").via(ZPipeline.utf8Decode >>> ZPipeline.splitLines)

//   @tailrec
  private def parseDraw(input: String): Set[Map[String, Int]] = ???

  val pipeLine: ZPipeline[Any, RuntimeException, String, (Int, Set[Map[String, Int]])] =
    ZPipeline.mapZIO { line =>
      for {
        separated <- ZIO.succeed(line.split(":"))
        gameId <- separated.headOption match {
          case Some(s"Game $id") => ZIO.succeed(id.toInt)
          case _ => ZIO.fail(new RuntimeException("malformed input file!"))
        }
        drawStrings = separated(1).split(";")
        sets = drawStrings.flatMap(setString => parseDraw(setString)).toSet
      } yield (gameId -> sets)
    }

  val sink =
    ZSink.collectAll[(Int, Set[Map[String, Int]])].mapZIO { lines =>
      ZIO.foreach(lines)(line => Console.printLine(s"id: ${line._1}, first: ${line._2.headOption}"))
    }

  val run = stream.via(pipeLine).run(sink)
}
