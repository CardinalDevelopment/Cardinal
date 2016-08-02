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

import com.google.common.collect.Lists;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.filter.Filter;
import in.twizmwaz.cardinal.module.filter.FilterModule;
import in.twizmwaz.cardinal.module.filter.FilterState;
import in.twizmwaz.cardinal.module.filter.LoadLateFilter;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RangeFilter implements Filter, LoadLateFilter {

  private final List<Filter> children;
  private final int min;
  private final int max;


  public RangeFilter(int min, int max, Filter... children) {
    this(Lists.newArrayList(children), min, max);
  }

  @Override
  public void load(Match match) {
    Cardinal.getModule(FilterModule.class).loadFilters(match, children);
  }

  @Override
  public FilterState evaluate(Object... objects) {
    int[] states = new int[]{0, 0, 0};
    for (int i = 0; i < children.size(); i++) {
      states[children.get(i).evaluate(objects).ordinal()]++;
      if ((states[1] + states[2] > children.size() - min && states[0] + states[1] > 0) /* Can't reach min anymore */
          || states[0] > max /* Allow bigger than max */ ) {
        return FilterState.DENY;
      }
      if (states[0] >= min && states[0] + (children.size() - 1 - i) >= max) { /* Won't go bigger than max */
        return FilterState.ALLOW;
      }
    }
    return states[0] >= min ? FilterState.ALLOW : FilterState.ABSTAIN;
  }

}
