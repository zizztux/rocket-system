#
# Copyright (c) 2020, SeungRyeol Lee
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
# 1. Redistributions of source code must retain the above copyright notice, this
#    list of conditions and the following disclaimer.
# 
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
# 
# 3. Neither the name of the copyright holder nor the names of its
#    contributors may be used to endorse or promote products derived from
#    this software without specific prior written permission.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

RELPATH := $(shell realpath --relative-to=$(ROCKETCHIP) $(PWD))


all: emulator

verilog:
	$(MAKE) -C $(ROCKETCHIP)/vsim ROCKETCHIP_ADDONS=$(RELPATH) PROJECT=ztx.rocketchip.system MODEL=TestHarness CONFIG=DefaultConfig run

verilog-debug:
	$(MAKE) -C $(ROCKETCHIP)/vsim ROCKETCHIP_ADDONS=$(RELPATH) PROJECT=ztx.rocketchip.system MODEL=TestHarness CONFIG=DefaultConfig run-debug

emulator:
	$(MAKE) -C $(ROCKETCHIP)/emulator ROCKETCHIP_ADDONS=$(RELPATH) PROJECT=ztx.rocketchip.system MODEL=TestHarness CONFIG=DefaultConfig run

emulator-debug:
	$(MAKE) -C $(ROCKETCHIP)/emulator ROCKETCHIP_ADDONS=$(RELPATH) PROJECT=ztx.rocketchip.system MODEL=TestHarness CONFIG=DefaultConfig run-debug

.PHONY: all verilog verilog-debug emulator emulator-debug