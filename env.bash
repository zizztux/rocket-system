#!/bin/bash

RISCV=../rocket-tools/.output
ROCKETCHIP=../rocket-chip

export INSTALLED_VERILATOR=$(which verilator)
export RISCV=$(realpath -m $RISCV)
export ROCKETCHIP=$(realpath -m $ROCKETCHIP)
