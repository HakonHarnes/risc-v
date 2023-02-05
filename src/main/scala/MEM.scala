package FiveStage

import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule

class MemoryFetch() extends MultiIOModule {

    val testHarness = IO(new Bundle {
        val DMEMsetup = Input(new DMEMsetupSignals)
        val DMEMpeek  = Output(UInt(32.W))

        val testUpdates = Output(new MemUpdates)
    })

    val io = IO(new Bundle {
        val nop              = Input(Bool())
        val rdIn             = Input(UInt(5.W))
        val dataIn           = Input(SInt(32.W))
        val writeDataIn      = Input(SInt(32.W))
        val controlSignalsIn = Input(new ControlSignals)

        val rdOut                 = Output(UInt(5.W))
        val regWriteOut           = Output(Bool())
        val regDataOut            = Output(UInt(32.W))
        val memDataOut            = Output(UInt(32.W))
        val memReadOut            = Output(Bool())
        val forwardSignalsRegOut  = Output(new ForwardSignals)
        val forwardSignalsDMemOut = Output(new ForwardSignals)
    })

    val DMEM = Module(new DMEM)

    DMEM.testHarness.setup  := testHarness.DMEMsetup
    testHarness.DMEMpeek    := DMEM.io.dataOut
    testHarness.testUpdates := DMEM.testHarness.testUpdates

    DMEM.io.dataAddress := io.dataIn.asUInt
    DMEM.io.dataIn      := io.writeDataIn.asUInt
    DMEM.io.writeEnable := io.controlSignalsIn.memWrite

    // Sets the output
    io.rdOut       := RegNext(io.rdIn)
    io.memDataOut  := DMEM.io.dataOut
    io.regDataOut  := RegNext(io.dataIn.asUInt)
    io.regWriteOut := RegNext(io.controlSignalsIn.regWrite)
    io.memReadOut  := RegNext(io.controlSignalsIn.memRead)

    // Forwarding signals: Sets the destination register
    io.forwardSignalsRegOut.rd  := Mux(io.controlSignalsIn.regWrite, io.rdIn, 0.U)
    io.forwardSignalsDMemOut.rd := Mux(RegNext(io.controlSignalsIn.regWrite) && RegNext(io.controlSignalsIn.memRead), RegNext(io.rdIn), 0.U)

    // Forwarding signals: Sets data
    io.forwardSignalsRegOut.data  := io.dataIn
    io.forwardSignalsDMemOut.data := DMEM.io.dataOut.asSInt
}
