package FiveStage

import ALUOps._
import chisel3._
import chisel3.util.MuxLookup

class ArithmeticLogicUnit() extends Module {
    val io = IO(new Bundle {
        val op1In   = Input(SInt(32.W))
        val op2In   = Input(SInt(32.W))
        val aluOpIn = Input(UInt(4.W))

        val aluResultOut = Output(SInt(32.W))
    })

    // ALU operation map
    val ALUopMap = Array(
        COPY_B -> (io.op2In),
        COPY_A -> (io.op1In),
        ADD    -> (io.op1In + io.op2In),
        AND    -> (io.op1In & io.op2In),
        OR     -> (io.op1In | io.op2In),
        SUB    -> (io.op1In - io.op2In),
        XOR    -> (io.op1In ^ io.op2In),
        SLL    -> (io.op1In << io.op2In(4, 0)),
        SLT    -> ((io.op1In < io.op2In).zext),
        SRA    -> (io.op1In >> io.op2In(4, 0)),
        SLTU   -> ((io.op1In.asUInt < io.op2In.asUInt).zext),
        SRL    -> ((io.op1In.asUInt >> io.op2In(4, 0).asUInt).zext)
    )

    // Calculates the ALU result
    io.aluResultOut := MuxLookup(io.aluOpIn, 0.S(32.W), ALUopMap)
}
