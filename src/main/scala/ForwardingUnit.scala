package FiveStage

import ALUOps._
import chisel3._
import chisel3.util._

/** Forwarding unit: Selects the correct forwarding signals
  */
class ForwardingUnit() extends Module {
    val io = IO(new Bundle {
        val op1In = Input(SInt(32.W))
        val op2In = Input(SInt(32.W))

        val data1In = Input(UInt(32.W))
        val data2In = Input(UInt(32.W))

        val registerRs1In = Input(UInt(5.W))
        val registerRs2In = Input(UInt(5.W))

        val forwardSignalsMEMReg  = Input(new ForwardSignals)
        val forwardSignalsMEMDMem = Input(new ForwardSignals)
        val forwardSignalsWBReg   = Input(new ForwardSignals)
        val forwardSignalsWBDMem  = Input(new ForwardSignals)

        val op1Out = Output(SInt(32.W))
        val op2Out = Output(SInt(32.W))

        val data1Out = Output(UInt(32.W))
        val data2Out = Output(UInt(32.W))
    })

    // OP1
    io.op1Out := MuxCase(
        io.op1In,
        Array(
            (io.forwardSignalsMEMReg.rd === io.registerRs1In && io.forwardSignalsMEMReg.valid)   -> io.forwardSignalsMEMReg.data,
            (io.forwardSignalsMEMDMem.rd === io.registerRs1In && io.forwardSignalsMEMDMem.valid) -> io.forwardSignalsMEMDMem.data,
            (io.forwardSignalsWBReg.rd === io.registerRs1In && io.forwardSignalsWBReg.valid)     -> io.forwardSignalsWBReg.data,
            (io.forwardSignalsWBDMem.rd === io.registerRs1In && io.forwardSignalsWBDMem.valid)   -> io.forwardSignalsWBDMem.data
        )
    )

    // OP2
    io.op2Out := MuxCase(
        io.op2In,
        Array(
            (io.forwardSignalsMEMReg.rd === io.registerRs2In && io.forwardSignalsMEMReg.valid)   -> io.forwardSignalsMEMReg.data,
            (io.forwardSignalsMEMDMem.rd === io.registerRs2In && io.forwardSignalsMEMDMem.valid) -> io.forwardSignalsMEMDMem.data,
            (io.forwardSignalsWBReg.rd === io.registerRs2In && io.forwardSignalsWBReg.valid)     -> io.forwardSignalsWBReg.data,
            (io.forwardSignalsWBDMem.rd === io.registerRs2In && io.forwardSignalsWBDMem.valid)   -> io.forwardSignalsWBDMem.data
        )
    )

    // Data1
    io.data1Out := MuxCase(
        io.data1In,
        Array(
            (io.forwardSignalsMEMReg.rd === io.registerRs1In && io.forwardSignalsMEMReg.valid)   -> io.forwardSignalsMEMReg.data.asUInt,
            (io.forwardSignalsMEMDMem.rd === io.registerRs1In && io.forwardSignalsMEMDMem.valid) -> io.forwardSignalsMEMDMem.data.asUInt,
            (io.forwardSignalsWBReg.rd === io.registerRs1In && io.forwardSignalsWBReg.valid)     -> io.forwardSignalsWBReg.data.asUInt,
            (io.forwardSignalsWBDMem.rd === io.registerRs1In && io.forwardSignalsWBDMem.valid)   -> io.forwardSignalsWBDMem.data.asUInt
        )
    )

    // Data2
    io.data2Out := MuxCase(
        io.data2In,
        Array(
            (io.forwardSignalsMEMReg.rd === io.registerRs2In && io.forwardSignalsMEMReg.valid)   -> io.forwardSignalsMEMReg.data.asUInt,
            (io.forwardSignalsMEMDMem.rd === io.registerRs2In && io.forwardSignalsMEMDMem.valid) -> io.forwardSignalsMEMDMem.data.asUInt,
            (io.forwardSignalsWBReg.rd === io.registerRs2In && io.forwardSignalsWBReg.valid)     -> io.forwardSignalsWBReg.data.asUInt,
            (io.forwardSignalsWBDMem.rd === io.registerRs2In && io.forwardSignalsWBDMem.valid)   -> io.forwardSignalsWBDMem.data.asUInt
        )
    )

}
