# RISC-V Five Stage Processor
## Introduction

This repository contains the implementation of a 32-bit RISC-V processor using the RISCV32I integer instruction set. The project was developed as part of the TDT4255 course at NTNU. The processor is fully pipelined and has five stages: Fetch, Decode, Execute, Memory and Writeback. The processor uses forwarding to resolve data dependencies between instructions.

![Five stage processor](https://github.com/HakonHarnes/risc-v/blob/master/doc/five-stage.png)

## Performance Improvements

To further enhance the performance of the processor, the following features have been implemented:

### Branch Prediction

Branch prediction is a technique that predicts the outcome of a branch instruction before it is executed. This allows the processor to fetch the instructions from the correct path, reducing the number of pipeline stalls caused by mispredicted branches. The processor uses a simple two-bit predictor to make predictions.

### Fast Branch Handling

Fast branch handling is a technique that reduces the number of cycles required to handle a branch instruction. This is achieved by executing the branch instruction in the Execute stage, instead of waiting for the Memory stage. This reduces the number of pipeline stalls caused by branches and improves the overall performance of the processor.


## Repository Structure

The repository is structured as follows:

- `src/main/scala`: Contains the source code of the processor, written in Scala and Chisel. 
- `src/test`: Contains the testbench and test cases used to verify the functionality of the processor. 
