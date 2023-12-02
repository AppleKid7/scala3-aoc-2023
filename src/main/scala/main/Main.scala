package main

import main.client.InputFetcher
import scala.io.BufferedSource
import scala.util.Try
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio.*
import scala.annotation.tailrec

object Day1 extends ZIOAppDefault {
  private def readFileZio(file: String): ZIO[Any, Throwable, List[String]] = for {
    source <- ZIO.attempt(scala.io.Source.fromFile(file))
    lines = source.getLines().toList
    _ <- ZIO.attempt(source.close())
  } yield lines

  private def getNumbers(items: List[String]): List[Int] = items.map { string =>
    string.filter(_.isDigit).split("").toList match {
      case head :: Nil => s"$head$head".toInt
      case head :: tail => s"$head${tail.last}".toInt
      case _ => 0
    }
  }

  private val part1 = for {
    contents <- readFileZio("input.txt")
    numbers <- ZIO.attempt(getNumbers(contents))
    // _ <- ZIO.foreach(numbers)(number =>
    //     Console.printLine(number)
    // )
    _ <- Console.printLine(s"part1: ${numbers.sum}")
  } yield ()

  enum Digits {
    case zero, one, two, three, four, five, six, seven, eight, nine
  }

  @tailrec
  private def findFirstDigit(input: String): Option[String] = {
    if (input.isEmpty) None
    else if (input.head.isDigit) Some(input.head.toString)
    else Digits.values.find(digit => input.startsWith(digit.toString)) match {
      case Some(digit) => Some(digit.ordinal.toString)
      case None => findFirstDigit(input.drop(1))
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

  private def getAllNumbers(items: List[String]): List[Int] = {
    items.map { string =>
      (findFirstDigit(string), findLastDigit(string)) match {
        case (Some(first), Some(last)) => s"$first$last".toInt
        case (Some(first), None) => s"$first$first".toInt
        case (None, Some(last)) => s"$last$last".toInt
        case _ => 0
      }
    }
  }

  private val part2 = for {
    contents <- readFileZio("input.txt")
    numbers = getAllNumbers(contents)
    _ <- Console.printLine(s"part2: ${numbers.sum}")
  } yield ()

  val run = part1 *> part2

//  val run = (for {
//    fetcher <- ZIO.service[InputFetcher]
//    text <- fetcher.get("https://adventofcode.com/2023/day/1/input")
//    contents <- readFileZio("input.txt")
//    _ <- Console.printLine(contents)
//  } yield ()).provide(
//    ZLayer.make[InputFetcher](InputFetcher.live, HttpClientZioBackend.layer()),
//    Console.live
//  )
}
