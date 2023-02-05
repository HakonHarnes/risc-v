package FiveStage

import chisel3._

/*-------------------------------------------------------------------+
 |               Barrier between the MEM and WB stage                |
 | ----------------------------------------------------------------- |
 |       +----+    +-------+    +-----+    +-------+   +----+        |
 |       | EX | -> | EXMEM | -> | MEM | -> | MEMWB | ->| WB |        |
 |       +----+    +-------+    +-----+    +-------+   +----+        |
 |        ^  ^                              |     |                  |
 |        |  |                              |     |                  |
 |        |  +------------------------------+     |                  |
 |        |             FORWARDING                |                  |
 |        |                                       |                  |
 |        +---------------------------------------+                  |
 |                      FORWARDING                                   |
 |                                                                   |
 +-------------------------------------------------------------------*/

class MEMWB extends Module {
    val io = IO(
        new Bundle {
            val regDataIn  = Input(UInt(32.W))
            val regDataOut = Output(UInt(32.W))

            val memDataIn  = Input(UInt(32.W))
            val memDataOut = Output(UInt(32.W))

            val memReadIn  = Input(Bool())
            val memReadOut = Output(Bool())

            val writeEnableIn  = Input(Bool())
            val writeEnableOut = Output(Bool())

            val writeAddressIn  = Input(UInt(5.W))
            val writeAddressOut = Output(UInt(5.W))

            val forwardSignalsRegIn  = Input(new ForwardSignals)
            val forwardSignalsRegOut = Output(new ForwardSignals)

            val forwardSignalsDMemIn  = Input(new ForwardSignals)
            val forwardSignalsDMemOut = Output(new ForwardSignals)
        }
    )

    io.regDataOut            := RegNext(io.regDataIn)
    io.memDataOut            := RegNext(io.memDataIn)
    io.memReadOut            := RegNext(io.memReadIn)
    io.writeAddressOut       := RegNext(io.writeAddressIn)
    io.writeEnableOut        := RegNext(io.writeEnableIn)
    io.forwardSignalsRegOut  := RegNext(io.forwardSignalsRegIn)
    io.forwardSignalsDMemOut := RegNext(io.forwardSignalsDMemIn)
}
