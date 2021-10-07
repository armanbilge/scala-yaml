package org.virtuslab.yaml.internal.load.parse

import org.virtuslab.yaml.internal.load.parse.Event._
import org.virtuslab.yaml.internal.load.reader.Scanner

class CommentSpec extends BaseParseSuite:

  test("should parse key value with ignoring comments") {
    val yaml =
      s"""|#Comment.
          |
          |apiVersion: apps/v1  app # comment
          |""".stripMargin

    val reader = Scanner(yaml)
    val events = ParserImpl(reader).getEvents()

    val expectedEvents = List(
      StreamStart,
      DocumentStart(),
      MappingStart(),
      Scalar("apiVersion"),
      Scalar("apps/v1  app"),
      MappingEnd(),
      DocumentEnd(),
      StreamEnd
    )
    assertEventsEquals(events, expectedEvents)
  }

  test("should parse empty document".ignore) {
    val yaml =
      s"""|#Comment.
          |""".stripMargin

    val reader = Scanner(yaml)
    val events = ParserImpl(reader).getEvents()

    val expectedEvents = Right(
      List(
        StreamStart,
        DocumentEnd(),
        StreamEnd
      )
    )
    assertEquals(events, expectedEvents)
  }

  test("should parse mapping with comments") {
    val yaml =
      s"""|spec:
          |  # comment or delete
          |  type: NodePort
          |  # if your cluster supports it, uncomment the following to automatically create
          |  # an external load-balanced IP for the frontend service.
          |  # type: LoadBalancer
          |""".stripMargin

    val reader = Scanner(yaml)
    val events = ParserImpl(reader).getEvents()

    val expectedEvents = List(
      StreamStart,
      DocumentStart(),
      MappingStart(),
      Scalar("spec"),
      MappingStart(),
      Scalar("type"),
      Scalar("NodePort"),
      MappingEnd(),
      MappingEnd(),
      DocumentEnd(),
      StreamEnd
    )
    assertEventsEquals(events, expectedEvents)
  }