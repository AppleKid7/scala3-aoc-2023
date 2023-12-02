package main

import zio.*
import zio.stream.*
import scala.annotation.tailrec
import java.io.File

object Day1 extends ZIOAppDefault {
  private val stream: ZStream[Any, Throwable, String] =
    ZStream.fromFileName("input.txt").via(ZPipeline.utf8Decode >>> ZPipeline.splitLines)

  private val part1: ZPipeline[Any, Nothing, String, Int] =
    ZPipeline.map { string =>
    string.filter(_.isDigit).split("").toList match {
      case head :: Nil => s"$head$head".toInt
      case head :: tail => s"$head${tail.last}".toInt
      case _ => 0
    }
  }

  val part1Sink = ZSink
    .collectAll[Int]
    .mapZIO(numbers => Console.printLine(s"part1: ${numbers.sum}"))

  val part1Stream = stream.via(part1).run(part1Sink)

  enum Digits {
    case zero, one, two, three, four, five, six, seven, eight, nine
  }

  @tailrec
  private def findFirstDigit(input: String): Option[String] = {
    if (input.isEmpty) None
    else if (input.head.isDigit) Some(input.head.toString)
    else Digits.values.find(digit => input.startsWith(digit.toString)) match {
      case Some(digit) => Some(digit.ordinal.toString)
      case None => findFirstDigit(input.tail)
    }
  }

  @tailrec
  private def findLastDigit(input: String): Option[String] = {
    if (input.isEmpty) None
    else if (input.last.isDigit) Some(input.last.toString)
    else Digits.values.find(digit => input.endsWith(digit.toString)) match {
      case Some(digit) => Some(digit.ordinal.toString)
      case None => findLastDigit(input.dropRight(1))
    }
  }

  private val part2: ZPipeline[Any, Nothing, String, Int] =
    ZPipeline.map { item =>
      (findFirstDigit(item), findLastDigit(item)) match {
        case (Some(first), Some(last)) => s"$first$last".toInt
        case (Some(first), None) => s"$first$first".toInt
        case (None, Some(last)) => s"$last$last".toInt
        case _ => 0
      }
    }

  val part2Sink = ZSink
    .collectAll[Int]
    .mapZIO(numbers => Console.printLine(s"part2: ${numbers.sum}"))

  private val part2Stream = stream.via(part2).run(part2Sink)

  val run = part1Stream *> part2Stream
}
