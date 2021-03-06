package org.programmiersportgruppe.redis

import akka.util.{ByteStringBuilder, ByteString}


object RValueSerializer {
  val newLine = Array[Byte]('\r', '\n')
  val nullBulkStringSerialization = Array[Byte]('-', '1') ++ newLine

  def serialize(value: RValue): ByteString = {
    val builder = new ByteStringBuilder
    serializeTo(builder)(value)
    builder.result()
  }

  def serializeTo(builder: ByteStringBuilder)(value: RValue) {
    builder.putByte(value.sigil)
    value match {
      case s: RSimpleString => builder.append(s.asByteString).putBytes(newLine)
      case e: RError => builder.append(e.asByteString).putBytes(newLine)
      case i: RInteger => builder.append(i.asByteString).putBytes(newLine)
      case RBulkString(Some(data)) => builder.append(ByteString(data.length.toString)).putBytes(newLine).append(data).putBytes(newLine)
      case RBulkString(None) => builder.putBytes(nullBulkStringSerialization)
      case RArray(items) => builder.append(ByteString(items.length.toString)).putBytes(newLine); items.foreach(serializeTo(builder))
    }
  }
}
