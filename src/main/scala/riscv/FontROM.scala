// Copyright 2021 Howard Lau
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package riscv

import chisel3._
import chisel3.util._

import javax.imageio.ImageIO

object GlyphInfo {
  val glyphWidth = 8
  val glyphHeight = 16
  // ASCII printable characters start from here
  val spaceIndex = 1
}

class FontROM extends Module {
  val glyphWidth = GlyphInfo.glyphWidth
  val glyphHeight = GlyphInfo.glyphHeight

  val io = IO(new Bundle {
    val glyph_index = Input(UInt(7.W))
    val glyph_x = Input(UInt(4.W))
    val glyph_y = Input(UInt(4.W))

    val glyph_pixel_on = Output(Bool())
  })

  val glyphs = readFontBitmap()
  io.glyph_pixel_on := glyphs(io.glyph_index * GlyphInfo.glyphHeight.U + io.glyph_y)(io
    .glyph_x).asBool()

  def readFontBitmap() = {
    val inputStream = getClass.getClassLoader.getResourceAsStream("vga_font_8x16.bmp")
    val image = ImageIO.read(inputStream)

    val glyphColumns = image.getWidth() / glyphWidth
    val glyphRows = image.getHeight / glyphHeight
    val glyphCount = glyphColumns * glyphRows
    val glyphs = new Array[UInt](glyphCount * GlyphInfo.glyphHeight)

    for (row <- 0 until glyphRows) {
      for (col <- 0 until glyphColumns) {
        for (i <- 0 until glyphHeight) {
          var lineInt = 0
          for (j <- 0 until glyphWidth) {
            if (image.getRGB(col * glyphWidth + j, row * glyphHeight + i) != 0xFFFFFFFF) {
              lineInt |= (1 << j)
            }
          }
          glyphs((row * glyphColumns + col) * GlyphInfo.glyphHeight + i) = lineInt.U(8.W)
        }
      }
    }
    VecInit(glyphs)
  }
}