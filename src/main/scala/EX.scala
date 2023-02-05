package FiveStage

import chisel3._
import chisel3.util._

class Execute extends Module {
    val io = IO(
        new Bundle {
            val PCIn = Input(UInt(32.W))
            val rdIn = Input(UInt(5.W))

            val op1In   = Input(SInt(32.W))
            val op2In   = Input(SInt(32.W))
            val aluOpIn = Input(UInt(4.W))

            val data1In = Input(UInt(32.W))
            val data2In = Input(UInt(32.W))

            val op1SelectIn = Input(UInt(1.W))
            val op2SelectIn = Input(UInt(1.W))

            val registerRs1In = Input(UInt(5.W))
            val registerRs2In = Input(UInt(5.W))

            val branchTypeIn     = Input(UInt(3.W))
            val controlSignalsIn = Input(new ControlSignals)

            val forwardSignalsMEMReg = Input(new ForwardSignals)
            val forwardSignalsWBReg  = Input(new ForwardSignals)

            val forwardSignalsMEMDMem = Input(new ForwardSignals)
            val forwardSignalsWBDMem  = Input(new ForwardSignals)

            val PCOut = Output(UInt(32.W))
            val rdOut = Output(UInt(5.W))

            val dataOut      = Output(SInt(32.W))
            val writeDataOut = Output(SInt(32.W))

            val targetOut = Output(UInt(32.W))
            val branchOut = Output(Bool())

            val controlSignalsOut = Output(new ControlSignals)

        }
    )

    // Selects correct input signals for the ALU
    val fu = Module(new ForwardingUnit)
    fu.io.op1In                 := io.op1In
    fu.io.op2In                 := io.op2In
    fu.io.data1In               := io.data1In
    fu.io.data2In               := io.data2In
    fu.io.registerRs1In         := io.registerRs1In
    fu.io.registerRs2In         := io.registerRs2In
    fu.io.forwardSignalsMEMReg  := io.forwardSignalsMEMReg
    fu.io.forwardSignalsMEMDMem := io.forwardSignalsMEMDMem
    fu.io.forwardSignalsWBReg   := io.forwardSignalsWBReg
    fu.io.forwardSignalsWBDMem  := io.forwardSignalsWBDMem

    // Performs calculation in ALU
    val alu = Module(new ArithmeticLogicUnit)
    alu.io.op1In   := Mux(io.op1SelectIn === Op1Select.rs1, fu.io.op1Out, io.op1In)
    alu.io.op2In   := Mux(io.op2SelectIn === Op2Select.rs2, fu.io.op2Out, io.op2In)
    alu.io.aluOpIn := io.aluOpIn
    io.dataOut     := alu.io.aluResultOut

    // Branch and jump variables
    io.branchOut := false.B
    io.targetOut := 0.U

    // Handles jump
    when(io.controlSignalsIn.jump) {
        io.branchOut := true.B
        io.targetOut := alu.io.aluResultOut.asUInt
        io.dataOut   := (io.PCIn + 4.U).asSInt
    }

    // Sets the data input for branching
    val data1 = fu.io.data1Out
    val data2 = fu.io.data2Out

    // Handles branching
    when(io.controlSignalsIn.branch) {
        val BranchTypeMap = Array(
            branchType.ltu  -> (data1 < data2),
            branchType.gteu -> (data1 >= data2),
            branchType.beq  -> (data1 === data2),
            branchType.neq  -> (data1 =/= data2),
            branchType.lt   -> (data1.asSInt < data2.asSInt),
            branchType.gte  -> (data1.asSInt >= data2.asSInt)
        )

        io.dataOut   := 0.S
        io.targetOut := alu.io.aluResultOut.asUInt
        io.branchOut := MuxLookup(io.branchTypeIn, true.B, BranchTypeMap)
    }

    // Sets the output
    io.PCOut             := io.PCIn
    io.rdOut             := io.rdIn
    io.writeDataOut      := Mux(fu.io.op2In === fu.io.op2Out, io.data2In.asSInt, fu.io.op2Out)
    io.controlSignalsOut := io.controlSignalsIn
}
