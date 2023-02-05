package FiveStage

import chisel3._
import chisel3.util.BitPat
import chisel3.util.ListLookup

/** This module is mostly done, but you will have to fill in the blanks in opcodeMap. You may want to add more signals to be decoded in this module depending on your design if you so desire.
  *
  * In the "classic" 5 stage decoder signals such as op1select and immType are not included, however I have added them to my design, and similarily you might find it useful to add more
  */
class Decoder() extends Module {

    val io = IO(new Bundle {
        val instruction = Input(new Instruction)

        val controlSignals = Output(new ControlSignals)
        val branchType     = Output(UInt(3.W))
        val op1Select      = Output(UInt(1.W))
        val op2Select      = Output(UInt(1.W))
        val immType        = Output(UInt(3.W))
        val ALUop          = Output(UInt(4.W))
    })

    import lookup._
    import Op1Select._
    import Op2Select._
    import branchType._
    import ImmFormat._

    val N = 0.asUInt(1.W)
    val Y = 1.asUInt(1.W)

    /** In scala we sometimes (ab)use the `->` operator to create tuples. The reason for this is that it serves as convenient sugar to make maps.
      *
      * This doesn't matter to you, just fill in the blanks in the style currently used, I just want to demystify some of the scala magic.
      *
      * `a -> b` == `(a, b)` == `Tuple2(a, b)`
      */

    /*----------------------------------------------------------------------------------------------+
    | regWrite, memRead, memWrite, branch, jump, branchType, Op1Select, Op2Select, ImmSelect, ALUOp |
    +----------------------------------------------------------------------------------------------*/
    val opcodeMap: Array[(BitPat, List[UInt])] = Array(
        // Arithmetics
        OR   -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.OR),
        ADD  -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.ADD),
        AND  -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.AND),
        SLL  -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.SLL),
        SLT  -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.SLT),
        SRA  -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.SRA),
        SRL  -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.SRL),
        SUB  -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.SUB),
        XOR  -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.XOR),
        SLTU -> List(Y, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.SLTU),

        // Immediates
        AUIPC -> List(Y, N, N, N, N, branchType.DC, PC, imm, ImmFormat.UTYPE, ALUOps.ADD),
        ORI   -> List(Y, N, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.OR),
        ADDI  -> List(Y, N, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.ADD),
        ANDI  -> List(Y, N, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.AND),
        SLLI  -> List(Y, N, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.SLL),
        SLTI  -> List(Y, N, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.SLT),
        SRAI  -> List(Y, N, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.SRA),
        SRLI  -> List(Y, N, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.SRL),
        XORI  -> List(Y, N, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.XOR),
        SLTIU -> List(Y, N, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.SLTU),
        LUI   -> List(Y, N, N, N, N, branchType.DC, Op1Select.DC, imm, ImmFormat.UTYPE, ALUOps.COPY_B),

        // Branch
        BLT  -> List(N, N, N, Y, N, branchType.lt, PC, imm, ImmFormat.BTYPE, ALUOps.ADD),
        BNE  -> List(N, N, N, Y, N, branchType.neq, PC, imm, ImmFormat.BTYPE, ALUOps.ADD),
        BEQ  -> List(N, N, N, Y, N, branchType.beq, PC, imm, ImmFormat.BTYPE, ALUOps.ADD),
        BGE  -> List(N, N, N, Y, N, branchType.gte, PC, imm, ImmFormat.BTYPE, ALUOps.ADD),
        BLTU -> List(N, N, N, Y, N, branchType.ltu, PC, imm, ImmFormat.BTYPE, ALUOps.ADD),
        BGEU -> List(N, N, N, Y, N, branchType.gteu, PC, imm, ImmFormat.BTYPE, ALUOps.ADD),

        // Jump
        JAL  -> List(Y, N, N, N, Y, branchType.jump, PC, imm, ImmFormat.JTYPE, ALUOps.ADD),
        JALR -> List(Y, N, N, N, Y, branchType.jump, rs1, imm, ImmFormat.ITYPE, ALUOps.ADD),

        // Memory
        LW -> List(Y, Y, N, N, N, branchType.DC, rs1, imm, ImmFormat.ITYPE, ALUOps.ADD),
        SW -> List(N, N, Y, N, N, branchType.DC, rs1, imm, ImmFormat.STYPE, ALUOps.ADD)
    )

    val NOP = List(N, N, N, N, N, branchType.DC, rs1, rs2, ImmFormat.DC, ALUOps.DC)

    val decodedControlSignals = ListLookup(io.instruction.asUInt(), NOP, opcodeMap)

    io.controlSignals.regWrite := decodedControlSignals(0)
    io.controlSignals.memRead  := decodedControlSignals(1)
    io.controlSignals.memWrite := decodedControlSignals(2)
    io.controlSignals.branch   := decodedControlSignals(3)
    io.controlSignals.jump     := decodedControlSignals(4)

    io.branchType := decodedControlSignals(5)
    io.op1Select  := decodedControlSignals(6)
    io.op2Select  := decodedControlSignals(7)
    io.immType    := decodedControlSignals(8)
    io.ALUop      := decodedControlSignals(9)
}
