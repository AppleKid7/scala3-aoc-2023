package main

import main.client.InputFetcher
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio.*

import scala.io.BufferedSource
import scala.util.Try

object Main extends ZIOAppDefault {
//  def readFileZio(file: String): ZIO[Console, Throwable, String] = ZIO.attempt {
//    val source: BufferedSource = scala.io.Source.fromFile(file)
//    try source.getLines().mkString
//    finally source.close()
//  }
  private def readFileZio(file: String): ZIO[Any, Throwable, Array[String]] = for {
    source <- ZIO.attempt(scala.io.Source.fromFile(file))
    lines = source.getLines().toArray
    _ <- ZIO.attempt(source.close())
  } yield lines

  private def getNumbers(items: Array[String]) = items.map { string =>
    string.filter(_.isDigit).split("")
  }

  val run = for {
    contents <- readFileZio("input.txt")
    numbers <- ZIO.attempt(getNumbers(contents))
    _ <- ZIO.foreach(numbers)(number =>
        Console.printLine(number.mkString(""))
    )
  } yield ()

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