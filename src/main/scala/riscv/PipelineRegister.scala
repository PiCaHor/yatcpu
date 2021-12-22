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

class PipelineRegister(width: Int = 32, defaultValue: UInt = 0.U) extends Module {
  val io = IO(new Bundle {
    val hold_enable = Input(Bool())
    val in = Input(UInt(width.W))
    val out = Output(UInt(width.W))
  })

  val reg = RegInit(UInt(width.W), defaultValue)
  reg := Mux(io.hold_enable, defaultValue, io.in)
  io.out := reg
}
