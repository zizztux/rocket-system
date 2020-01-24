#!/bin/sh

make -C ../rocket-chip/vsim ROCKETCHIP_ADDONS=../rocket-system PROJECT=ztx.rocketchip.system MODEL=RocketSystem CONFIG=DefaultConfig RISCV=/tmp verilog
