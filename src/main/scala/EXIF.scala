package FiveStage

import chisel3._

/*-------------------------------------------+
 |    Barrier between the ID and EX stage    |
 | ----------------------------------------- |
 |       +----+    +------+    +----+        |
 |       | EX | -> | EXIF | -> | IF |        |
 |       +----+    +------+    +----+        |
 +-------------------------------------------*/

class EXIF extends Module {
    val io = IO(
        new Bundle {
            val targetIn  = Input(UInt(32.W))
            val targetOut = Output(UInt(32.W))

            val branchIn  = Input(Bool())
            val branchOut = Output(Bool())
        }
    )

    io.targetOut := io.targetIn
    io.branchOut := io.branchIn
}
