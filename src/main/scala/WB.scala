package FiveStage

import chisel3._
import chisel3.util._

class WriteBack() extends Module {
    val io = IO(new Bundle {
        val writeAddressIn = Input(UInt(5.W))
        val writeEnableIn  = Input(Bool())

        val regDataIn = Input(UInt(32.W))
        val memDataIn = Input(UInt(32.W))
        val memReadIn = Input(Bool())

        val writeDataOut    = Output(UInt(32.W))
        val writeAddressOut = Output(UInt(5.W))
        val writeEnableOut  = Output(Bool())
    })

    io.writeDataOut    := Mux(io.memReadIn, io.memDataIn, io.regDataIn)
    io.writeAddressOut := io.writeAddressIn
    io.writeEnableOut  := io.writeEnableIn
}
