package org.virtuslab.yaml

import org.virtuslab.yaml.internal.dump.present.PresenterImpl
import org.virtuslab.yaml.internal.dump.serialize.SerializerImpl
import org.virtuslab.yaml.internal.load.compose.ComposerImpl
import org.virtuslab.yaml.internal.load.parse.ParserImpl
import org.virtuslab.yaml.internal.load.reader.YamlReader

import scala.deriving.Mirror

inline def deriveYamlEncoder[T](using m: Mirror.Of[T]): YamlEncoder[T] = YamlEncoder.derived[T]
inline def deriveYamlDecoder[T](using m: Mirror.Of[T]): YamlDecoder[T] = YamlDecoder.derived[T]
inline def deriveYamlCodec[T](using m: Mirror.Of[T]): YamlCodec[T]     = YamlCodec.derived[T]

extension (str: String)
  /**
   * Parse YAML from the given [[String]], returning either [[YamlError]] or [[T]].
   * 
   * According to the specification:
   * - [[Parser]] takes input string and produces sequence of events
   * - then [[Composer]] produces a representation graph from events
   * - finally [[YamlDecoder]] (construct phase from the YAML spec) constructs data type [[T]] from the YAML representation. 
   */
  def as[T](using c: YamlDecoder[T]): Either[YamlError, T] =
    for
      events <- ParserImpl.getEvents(YamlReader(str))
      node   <- ComposerImpl.fromEvents(events)
      t      <- c.construct(node)
    yield t

extension [T](t: T)
  /**
   * Serialize a [[T]] into a YAML string.
   * 
   * According to the specification:
   * - [[YamlEncoder]] encode type [[T]] into [[Node]]
   * - [[Serializer]] serializes [[Node]] into sequence of [[Event]]s
   * - [[Presenter]] present [[Events]] as a character stream
   */
  def asYaml(using encoder: YamlEncoder[T]): String =
    val node   = encoder.asNode(t)
    val events = SerializerImpl.toEvents(node)
    PresenterImpl.asString(events)