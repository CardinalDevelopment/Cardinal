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

package in.twizmwaz.cardinal.module.filter.type.modifiers;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.filter.Filter;
import in.twizmwaz.cardinal.module.filter.FilterModule;
import in.twizmwaz.cardinal.module.filter.FilterState;
import in.twizmwaz.cardinal.module.filter.LoadLateFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TransformFilter implements Filter, LoadLateFilter {

  private final Filter child;
  private final Map<FilterState, FilterState> transform = new HashMap<>();

  /**
   * Will create a transform filter, this filter just transforms one output to another.
   * @param child The child filter to ask for the result.
   * @param allow The output if the child returns allow.
   * @param abstain The output if the child returns abstain.
   * @param deny The output if the child returns deny.
   */
  public TransformFilter(Filter child, FilterState allow, FilterState abstain, FilterState deny) {
    this.child = child;
    transform.put(FilterState.ALLOW, allow);
    transform.put(FilterState.ABSTAIN, abstain);
    transform.put(FilterState.DENY, deny);
  }

  @Override
  public void load(Match match) {
    Cardinal.getModule(FilterModule.class).loadFilters(match, Collections.singleton(child));
  }

  @Override
  public FilterState evaluate(Object... objects) {
    return transform.get(child.evaluate(objects));

  }

}
