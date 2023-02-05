package FiveStage

import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule

class InstructionFetch extends MultiIOModule {

    // Setup. You should not change this code.
    val testHarness = IO(
        new Bundle {
            val IMEMsetup = Input(new IMEMsetupSignals)
            val PC        = Output(UInt())
        }
    )

    val io = IO(new Bundle {
        val freezeIn = Input(Bool())
        val targetIn = Input(UInt(32.W))
        val branchIn = Input(Bool())

        val PCOut          = Output(UInt(32.W))
        val instructionOut = Output(new Instruction)
    })

    val IMEM = Module(new IMEM)
    val PC   = RegInit(UInt(32.W), 0.U)

    IMEM.testHarness.setupSignals := testHarness.IMEMsetup
    testHarness.PC                := IMEM.testHarness.requestedAddress

    // Sets the program counter
    io.PCOut                   := PC
    IMEM.io.instructionAddress := Mux(io.freezeIn, RegNext(PC), PC)

    // Increments the program counter
    // Does not increment the program counter during freezes
    // Program counter is set to the target when branching
    PC := MuxCase(PC + 4.U, Array(io.freezeIn -> PC, io.branchIn -> io.targetIn))

    // Sets the instruction
    val instruction = Wire(new Instruction)
    instruction       := IMEM.io.instruction.asTypeOf(new Instruction)
    io.instructionOut := instruction

    // Setup. You should not change this code.
    when(testHarness.IMEMsetup.setup) {
        PC          := 0.U
        instruction := Instruction.NOP
    }
}
