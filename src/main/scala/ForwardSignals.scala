package FiveStage

import chisel3._

/** Used for forward signals
  */
class ForwardSignals extends Bundle {
    val rd   = UInt(5.W)
    val data = SInt(32.W)

    def valid() = rd =/= 0.U
}
