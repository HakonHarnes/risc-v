package FiveStage

import chisel3._

/*-------------------------------------------+
 |    Barrier between the IF and ID stage    |
 | ----------------------------------------- |
 |       +----+    +------+    +----+        |
 |       | IF | -> | IFID | -> | ID |        |
 |       +----+    +------+    +----+        |
 +-------------------------------------------*/

class IFID extends Module {
    val io = IO(
        new Bundle {
            val freezeIn = Input(Bool())
            val nop      = Input(Bool())

            val PCIn  = Input(UInt(32.W))
            val PCOut = Output(UInt(32.W))

            val instructionIn  = Input(new Instruction)
            val instructionOut = Output(new Instruction)

            val nopOut = Output(Bool())
        }
    )

    io.nopOut         := RegNext(io.nop)
    io.PCOut          := RegNext(Mux(io.freezeIn, RegNext(io.PCIn), io.PCIn))
    io.instructionOut := Mux(RegNext(io.nop), Instruction.NOP, io.instructionIn)
}
