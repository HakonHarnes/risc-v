package FiveStage

import chisel3._

/*-------------------------------------------+
 |    Barrier between the ID and EX stage    |
 | ----------------------------------------- |
 |       +----+    +------+    +----+        |
 |       | ID | -> | IDEX | -> | EX |        |
 |       +----+    +------+    +----+        |
 +-------------------------------------------*/

class IDEX extends Module {
    val io = IO(
        new Bundle {
            val nop = Input(Bool())

            val PCIn  = Input(UInt(32.W))
            val PCOut = Output(UInt(32.W))

            val rdIn  = Input(UInt(5.W))
            val rdOut = Output(UInt(5.W))

            val op1In  = Input(SInt(32.W))
            val op1Out = Output(SInt(32.W))

            val op2In  = Input(SInt(32.W))
            val op2Out = Output(SInt(32.W))

            val aluOpIn  = Input(UInt(4.W))
            val aluOpOut = Output(UInt(4.W))

            val data1In  = Input(UInt(32.W))
            val data1Out = Output(UInt(32.W))

            val data2In  = Input(UInt(32.W))
            val data2Out = Output(UInt(32.W))

            val op1SelectIn  = Input(UInt(1.W))
            val op1SelectOut = Output(UInt(1.W))

            val op2SelectIn  = Input(UInt(1.W))
            val op2SelectOut = Output(UInt(1.W))

            val registerRs1In  = Input(UInt(5.W))
            val registerRs1Out = Output(UInt(5.W))

            val registerRs2In  = Input(UInt(5.W))
            val registerRs2Out = Output(UInt(5.W))

            val branchTypeIn  = Input(UInt(3.W))
            val branchTypeOut = Output(UInt(3.W))

            val controlSignalsIn  = Input(new ControlSignals)
            val controlSignalsOut = Output(new ControlSignals)
        }
    )

    io.PCOut             := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.PCIn))
    io.rdOut             := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.rdIn))
    io.op1Out            := Mux(RegNext(io.nop), RegNext(0.S), RegNext(io.op1In))
    io.op2Out            := Mux(RegNext(io.nop), RegNext(0.S), RegNext(io.op2In))
    io.aluOpOut          := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.aluOpIn))
    io.data1Out          := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.data1In))
    io.data2Out          := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.data2In))
    io.op1SelectOut      := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.op1SelectIn))
    io.op2SelectOut      := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.op2SelectIn))
    io.registerRs1Out    := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.registerRs1In))
    io.registerRs2Out    := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.registerRs2In))
    io.branchTypeOut     := Mux(RegNext(io.nop), RegNext(0.U), RegNext(io.branchTypeIn))
    io.controlSignalsOut := Mux(RegNext(io.nop), RegNext(ControlSignals.nop), RegNext(io.controlSignalsIn))
}
