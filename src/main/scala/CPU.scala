package FiveStage

import chisel3._
import chisel3.core.Input
import chisel3.experimental._

class CPU extends MultiIOModule {

    val testHarness = IO(
        new Bundle {
            val setupSignals = Input(new SetupSignals)
            val testReadouts = Output(new TestReadouts)
            val regUpdates   = Output(new RegisterUpdates)
            val memUpdates   = Output(new MemUpdates)
            val currentPC    = Output(UInt(32.W))
        }
    )

    // Initializes the barriers
    val IFID  = Module(new IFID).io  // IF  ->  ID
    val IDEX  = Module(new IDEX).io  // ID  ->  EX
    val EXIF  = Module(new EXIF).io  // EX  ->  IF
    val EXMEM = Module(new EXMEM).io // EX  ->  MEM
    val MEMWB = Module(new MEMWB).io // MEM ->  WB

    // Initiates the stages
    val ID  = Module(new InstructionDecode)
    val IF  = Module(new InstructionFetch)
    val EX  = Module(new Execute)
    val MEM = Module(new MemoryFetch)
    val WB  = Module(new WriteBack)

    // Setup. You should not change this code
    IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
    ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
    MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals

    testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
    testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek

    // Setup. You should not change this code
    testHarness.regUpdates := ID.testHarness.testUpdates
    testHarness.memUpdates := MEM.testHarness.testUpdates
    testHarness.currentPC  := IF.testHarness.PC

    // IFID BARRIER
    IFID.PCIn          := IF.io.PCOut
    IFID.instructionIn := IF.io.instructionOut

    // ID STAGE
    ID.io.PCIn          := IFID.PCOut
    ID.io.instructionIn := IFID.instructionOut
    ID.io.nop           := IFID.nopOut

    // IDEX BARRIER
    IDEX.PCIn             := ID.io.PCOut
    IDEX.rdIn             := ID.io.rdOut
    IDEX.op1In            := ID.io.op1Out
    IDEX.op2In            := ID.io.op2Out
    IDEX.op1SelectIn      := ID.io.op1SelectOut
    IDEX.op2SelectIn      := ID.io.op2SelectOut
    IDEX.aluOpIn          := ID.io.aluOpOut
    IDEX.data1In          := ID.io.data1Out
    IDEX.data2In          := ID.io.data2Out
    IDEX.registerRs1In    := ID.io.registerRs1Out
    IDEX.registerRs2In    := ID.io.registerRs2Out
    IDEX.branchTypeIn     := ID.io.branchTypeOut
    IDEX.controlSignalsIn := ID.io.controlSignalsOut

    // EX STAGE
    EX.io.PCIn                  := IDEX.PCOut
    EX.io.rdIn                  := IDEX.rdOut
    EX.io.op1In                 := IDEX.op1Out
    EX.io.op2In                 := IDEX.op2Out
    EX.io.aluOpIn               := IDEX.aluOpOut
    EX.io.data1In               := IDEX.data1Out
    EX.io.data2In               := IDEX.data2Out
    EX.io.op1SelectIn           := IDEX.op1SelectOut
    EX.io.op2SelectIn           := IDEX.op2SelectOut
    EX.io.registerRs1In         := IDEX.registerRs1Out
    EX.io.registerRs2In         := IDEX.registerRs2Out
    EX.io.branchTypeIn          := IDEX.branchTypeOut
    EX.io.controlSignalsIn      := IDEX.controlSignalsOut
    EX.io.forwardSignalsMEMReg  := MEMWB.forwardSignalsRegIn
    EX.io.forwardSignalsWBReg   := MEMWB.forwardSignalsRegOut
    EX.io.forwardSignalsMEMDMem := MEMWB.forwardSignalsDMemIn
    EX.io.forwardSignalsWBDMem  := MEMWB.forwardSignalsDMemOut

    // EXIF BARRIER
    EXIF.branchIn := EX.io.branchOut
    EXIF.targetIn := EX.io.targetOut

    // IF STAGE
    IF.io.branchIn := EXIF.branchOut
    IF.io.targetIn := EXIF.targetOut

    // EXMEM BARRIER
    EXMEM.rdIn             := EX.io.rdOut
    EXMEM.dataIn           := EX.io.dataOut
    EXMEM.writeDataIn      := EX.io.writeDataOut
    EXMEM.controlSignalsIn := EX.io.controlSignalsOut

    // MEM STAGE
    MEM.io.rdIn             := EXMEM.rdOut
    MEM.io.dataIn           := EXMEM.dataOut
    MEM.io.writeDataIn      := EXMEM.writeDataOut
    MEM.io.controlSignalsIn := EXMEM.controlSignalsOut

    // MEMWB BARRIER
    MEMWB.writeAddressIn       := MEM.io.rdOut
    MEMWB.writeEnableIn        := MEM.io.regWriteOut
    MEMWB.regDataIn            := MEM.io.regDataOut
    MEMWB.memDataIn            := MEM.io.memDataOut
    MEMWB.memReadIn            := MEM.io.memReadOut
    MEMWB.forwardSignalsRegIn  := MEM.io.forwardSignalsRegOut
    MEMWB.forwardSignalsDMemIn := MEM.io.forwardSignalsDMemOut

    // WB STAGE
    WB.io.regDataIn      := MEMWB.regDataOut
    WB.io.memDataIn      := MEMWB.memDataOut
    WB.io.memReadIn      := MEMWB.memReadOut
    WB.io.writeEnableIn  := MEMWB.writeEnableOut
    WB.io.writeAddressIn := MEMWB.writeAddressOut

    // WB -> ID
    ID.io.writeDataIn          := WB.io.writeDataOut
    ID.io.writeEnableIn        := WB.io.writeEnableOut
    ID.io.writeAddressIn       := WB.io.writeAddressOut
    ID.io.forwardSignalsRegIn  := MEMWB.forwardSignalsRegOut
    ID.io.forwardSignalsDMemIn := MEMWB.forwardSignalsDMemOut

    // Hazard variables
    IF.io.freezeIn := false.B
    IFID.freezeIn  := false.B
    IFID.nop       := false.B
    IDEX.nop       := false.B
    MEM.io.nop     := false.B
    EXMEM.nop      := false.B

    // Handles data hazards
    when(IDEX.controlSignalsOut.memRead && (IDEX.registerRs1In === EXMEM.rdIn || IDEX.registerRs2In === EXMEM.rdIn)) {
        IF.io.freezeIn := true.B
        IFID.freezeIn  := true.B
        IDEX.nop       := true.B
        MEM.io.nop     := true.B
    }

    // Handles control hazards
    when(EXIF.branchOut) {
        IFID.nop := true.B
        IDEX.nop := true.B
    }
}
