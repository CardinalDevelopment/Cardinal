/*
 * Copyright (c) 2016, Kevin Phoenix
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package in.twizmwaz.cardinal.module.apply;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ApplyType {

  ENTER("enter"),
  LEAVE("leave"),
  BLOCK("block"),
  BLOCK_PLACE("block-place"),
  BLOCK_PLACE_AGAINST("block-place-against"),
  BLOCK_BREAK("block-break"),
  BLOCK_PHYSICS("block-physics"),
  USE("use"),
  MOBS("mobs"),
  KIT("filter", "kit"),
  KIT_LEND("filter", "lend-kit"),
  VELOCITY("filter", "velocity");

  public final String filterAttr;
  public final String otherAttr;
  public final boolean filterOnly;

  private ApplyType(String filterAttr) {
    this(filterAttr, null, true);
  }

  private ApplyType(String filterAttr, String otherAttr) {
    this(filterAttr, otherAttr, false);
  }

}
