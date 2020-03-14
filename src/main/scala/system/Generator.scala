/*
 * Copyright (c) 2020, SeungRyeol Lee
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ztx.rocketchip.system

import freechips.rocketchip.subsystem.RocketTilesKey
import freechips.rocketchip.tile.XLen
import freechips.rocketchip.util.GeneratorApp

import scala.collection.mutable.LinkedHashSet

/** A Generator for platforms containing Rocket Subsystemes */
object Generator extends GeneratorApp {
  override def addTestSuites {
    import freechips.rocketchip.system.DefaultTestSuites._
    import freechips.rocketchip.system.{RegressionTestSuite, TestGeneration}
    val xlen = params(XLen)

    val regressionTests = LinkedHashSet(
      "rv64ud-v-fcvt",
      "rv64ud-p-fdiv",
      "rv64ud-v-fadd",
      "rv64uf-v-fadd",
      "rv64um-v-mul",
      "rv64mi-p-breakpoint",
      "rv64uc-v-rvc",
      "rv64ud-v-structural",
      "rv64si-p-wfi",
      "rv64um-v-divw",
      "rv64ua-v-lrsc",
      "rv64ui-v-fence_i",
      "rv64ud-v-fcvt_w",
      "rv64uf-v-fmin",
      "rv64ui-v-sb",
      "rv64ua-v-amomax_d",
      "rv64ud-v-move",
      "rv64ud-v-fclass",
      "rv64ua-v-amoand_d",
      "rv64ua-v-amoxor_d",
      "rv64si-p-sbreak",
      "rv64ud-v-fmadd",
      "rv64uf-v-ldst",
      "rv64um-v-mulh",
      "rv64si-p-dirty",
      "rv32mi-p-ma_addr",
      "rv32mi-p-csr",
      "rv32ui-p-sh",
      "rv32ui-p-lh",
      "rv32uc-p-rvc",
      "rv32mi-p-sbreak",
      "rv32ui-p-sll")

    // TODO: for now only generate tests for the first core in the first subsystem
    params(RocketTilesKey).headOption.map { tileParams =>
      val coreParams = tileParams.core
      val vm = coreParams.useVM
      val env = if (vm) List("p","v") else List("p")
      coreParams.fpu foreach { case cfg =>
        if (xlen == 32) {
          TestGeneration.addSuites(env.map(rv32uf))
          if (cfg.fLen >= 64)
            TestGeneration.addSuites(env.map(rv32ud))
        } else {
          TestGeneration.addSuite(rv32udBenchmarks)
          TestGeneration.addSuites(env.map(rv64uf))
          if (cfg.fLen >= 64)
            TestGeneration.addSuites(env.map(rv64ud))
        }
      }
      if (coreParams.useAtomics) {
        if (tileParams.dcache.flatMap(_.scratch).isEmpty)
          TestGeneration.addSuites(env.map(if (xlen == 64) rv64ua else rv32ua))
        else
          TestGeneration.addSuites(env.map(if (xlen == 64) rv64uaSansLRSC else rv32uaSansLRSC))
      }
      if (coreParams.useCompressed) TestGeneration.addSuites(env.map(if (xlen == 64) rv64uc else rv32uc))
      val (rvi, rvu) =
        if (xlen == 64) ((if (vm) rv64i else rv64pi), rv64u)
        else            ((if (vm) rv32i else rv32pi), rv32u)

      TestGeneration.addSuites(rvi.map(_("p")))
      TestGeneration.addSuites((if (vm) List("v") else List()).flatMap(env => rvu.map(_(env))))
      TestGeneration.addSuite(benchmarks)

      /* Filter the regression tests based on what the Rocket Chip configuration supports */
      val extensions = {
        val fd = coreParams.fpu.map {
          case cfg if cfg.fLen >= 64 => "fd"
          case _                     => "f"
        }
        val m = coreParams.mulDiv.map{ case _ => "m" }
        fd ++ m ++ Seq( if (coreParams.useRVE)        Some("e") else Some("i"),
                        if (coreParams.useAtomics)    Some("a") else None,
                        if (coreParams.useCompressed) Some("c") else None )
          .flatten
          .mkString("")
      }
      val re = s"""^rv$xlen[usm][$extensions].+""".r
      regressionTests.retain{
        case re() => true
        case _    => false
      }
      TestGeneration.addSuite(new RegressionTestSuite(regressionTests))
    }
  }

  generateFirrtl
  generateAnno
  generateTestSuiteMakefrags
  generateROMs
  generateArtefacts
}
