package FiveStage

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.{BitPat, MuxCase, MuxLookup}

class InstructionDecode extends MultiIOModule {

    // Setup. You should not change this code
    val testHarness = IO(new Bundle {
        val registerSetup = Input(new RegisterSetupSignals)
        val registerPeek  = Output(UInt(32.W))
        val testUpdates   = Output(new RegisterUpdates)
    })

    val io = IO(
        new Bundle {
            val nop = Input(Bool())

            val forwardSignalsRegIn  = Input(new ForwardSignals)
            val forwardSignalsDMemIn = Input(new ForwardSignals)

            val PCIn          = Input(UInt(32.W))
            val instructionIn = Input(new Instruction)

            val writeDataIn    = Input(UInt(32.W))
            val writeAddressIn = Input(UInt(5.W))
            val writeEnableIn  = Input(Bool())

            val PCOut = Output(UInt(32.W))
            val rdOut = Output(UInt(5.W))

            val op1Out   = Output(SInt(32.W))
            val op2Out   = Output(SInt(32.W))
            val aluOpOut = Output(UInt(4.W))

            val op1SelectOut = Output(UInt(1.W))
            val op2SelectOut = Output(UInt(1.W))

            val data1Out = Output(UInt(32.W))
            val data2Out = Output(UInt(32.W))

            val registerRs1Out = Output(UInt(5.W))
            val registerRs2Out = Output(UInt(5.W))

            val branchTypeOut     = Output(UInt(3.W))
            val controlSignalsOut = Output(new ControlSignals)
        }
    )

    val registers = Module(new Registers)
    val decoder   = Module(new Decoder).io

    // Setup. You should not change this code
    registers.testHarness.setup := testHarness.registerSetup
    testHarness.registerPeek    := registers.io.readData1
    testHarness.testUpdates     := registers.testHarness.testUpdates

    // Sets the register values
    registers.io.readAddress1 := io.instructionIn.registerRs1
    registers.io.readAddress2 := io.instructionIn.registerRs2
    registers.io.writeAddress := io.writeAddressIn
    registers.io.writeEnable  := io.writeEnableIn
    registers.io.writeData    := io.writeDataIn

    // Sets decoder input
    decoder.instruction := io.instructionIn

    // Immediate value type map
    val ImmtypeMap = Array(
        ImmFormat.ITYPE -> (io.instructionIn.immediateIType),
        ImmFormat.STYPE -> (io.instructionIn.immediateSType),
        ImmFormat.BTYPE -> (io.instructionIn.immediateBType),
        ImmFormat.UTYPE -> (io.instructionIn.immediateUType),
        ImmFormat.JTYPE -> (io.instructionIn.immediateJType),
        ImmFormat.SHAMT -> (io.instructionIn.immediateZType)
    )

    // Sets the immediate value
    val immediate = MuxLookup(decoder.immType, io.instructionIn.immediateIType, ImmtypeMap)

    // Reads data from decoder
    io.op1Out := Mux(decoder.op1Select === Op1Select.rs1, registers.io.readData1, io.PCIn).asSInt
    io.op2Out := Mux(decoder.op2Select === Op2Select.rs2, registers.io.readData2.asSInt, immediate)

    io.data1Out := registers.io.readData1
    io.data2Out := registers.io.readData2

    // Handles write address = read address
    when(io.writeEnableIn && io.writeAddressIn =/= 0.U) {

        // Register RS1
        when(io.instructionIn.registerRs1 === io.writeAddressIn) {
            when(decoder.op1Select === Op1Select.rs1) {
                io.op1Out := io.writeDataIn.asSInt
            }.otherwise {
                io.data1Out := io.writeDataIn
            }
        }

        // Register RS2
        when(io.instructionIn.registerRs2 === io.writeAddressIn) {
            when(decoder.op2Select === Op2Select.rs2) {
                io.op2Out := io.writeDataIn.asSInt
            }.otherwise {
                io.data2Out := io.writeDataIn
            }
        }
    }

    // Selects the forward signals if valid
    when(io.forwardSignalsRegIn.valid) {
        when(io.instructionIn.registerRs1 === io.forwardSignalsRegIn.rd && decoder.op1Select === Op1Select.rs1) {
            io.op1Out := io.forwardSignalsRegIn.data
        }
        when(io.instructionIn.registerRs2 === io.forwardSignalsRegIn.rd && decoder.op2Select === Op2Select.rs2) {
            io.op2Out := io.forwardSignalsRegIn.data
        }
    }.elsewhen(io.forwardSignalsDMemIn.valid) {
        when(io.instructionIn.registerRs1 === io.forwardSignalsDMemIn.rd && decoder.op1Select === Op1Select.rs1) {
            io.op1Out := io.forwardSignalsDMemIn.data
        }
        when(io.instructionIn.registerRs2 === io.forwardSignalsDMemIn.rd && decoder.op2Select === Op2Select.rs2) {
            io.op2Out := io.forwardSignalsDMemIn.data
        }
    }

    // Sets the output signals
    io.rdOut             := io.instructionIn.registerRd
    io.aluOpOut          := decoder.ALUop
    io.op1SelectOut      := decoder.op1Select
    io.op2SelectOut      := decoder.op2Select
    io.registerRs1Out    := io.instructionIn.registerRs1
    io.registerRs2Out    := io.instructionIn.registerRs2
    io.branchTypeOut     := decoder.branchType
    io.controlSignalsOut := Mux(io.nop, ControlSignals.nop, decoder.controlSignals)
    io.PCOut             := io.PCIn
}
