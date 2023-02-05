package FiveStage

import chisel3._
import chisel3.util._

/*--------------------------------------------+
 |    Barrier between the EX and MEM stage    |
 | ------------------------------------------ |
 |       +----+    +-------+   +-----+        |
 |       | EX | -> | EXMEM | ->| MEM |        |
 |       +----+    +-------+   +-----+        |
 |         ^          |                       |
 |         |          |                       |
 |         +----------+                       |
 |          FORWARDING                        |
 |                                            |
 +--------------------------------------------*/

class EXMEM extends Module {
    val io = IO(
        new Bundle {
            val nop = Input(Bool())

            val rdIn  = Input(UInt(5.W))
            val rdOut = Output(UInt(5.W))

            val dataIn  = Input(SInt(32.W))
            val dataOut = Output(SInt(32.W))

            val writeDataIn  = Input(SInt(32.W))
            val writeDataOut = Output(SInt(32.W))

            val controlSignalsIn  = Input(new ControlSignals)
            val controlSignalsOut = Output(new ControlSignals)
        }
    )

    io.rdOut             := Mux(io.nop, RegNext(0.U), RegNext(io.rdIn))
    io.dataOut           := Mux(io.nop, RegNext(0.S), RegNext(io.dataIn))
    io.writeDataOut      := Mux(io.nop, RegNext(0.S), RegNext(io.writeDataIn))
    io.controlSignalsOut := Mux(io.nop, ControlSignals.nop, RegNext(io.controlSignalsIn))
}
