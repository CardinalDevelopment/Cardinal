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

package in.twizmwaz.cardinal.module.objective.destroyable;

import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.NameComponent;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import ee.ellytr.chat.component.formattable.ListComponent;
import ee.ellytr.chat.component.formattable.LocalizedComponent;
import ee.ellytr.chat.component.formattable.UnlocalizedComponent;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.component.team.TeamComponent;
import in.twizmwaz.cardinal.event.objective.ObjectiveCompleteEvent;
import in.twizmwaz.cardinal.event.objective.ObjectiveTouchEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.apply.AppliedModule;
import in.twizmwaz.cardinal.module.apply.AppliedRegion;
import in.twizmwaz.cardinal.module.apply.ApplyType;
import in.twizmwaz.cardinal.module.filter.FilterState;
import in.twizmwaz.cardinal.module.filter.type.MaterialFilter;
import in.twizmwaz.cardinal.module.filter.type.TeamFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.AllFilter;
import in.twizmwaz.cardinal.module.filter.type.modifiers.TransformFilter;
import in.twizmwaz.cardinal.module.objective.Objective;
import in.twizmwaz.cardinal.module.objective.OwnedObjective;
import in.twizmwaz.cardinal.module.objective.ProximityMetric;
import in.twizmwaz.cardinal.module.region.Region;
import in.twizmwaz.cardinal.module.region.type.FiniteBlockRegion;
import in.twizmwaz.cardinal.module.scoreboard.displayables.EntryHolder;
import in.twizmwaz.cardinal.module.scoreboard.displayables.EntryUpdater;
import in.twizmwaz.cardinal.module.team.Team;
import in.twizmwaz.cardinal.playercontainer.CompetitorContainer;
import in.twizmwaz.cardinal.util.Channels;
import in.twizmwaz.cardinal.util.Characters;
import in.twizmwaz.cardinal.util.Components;
import in.twizmwaz.cardinal.util.MaterialPattern;
import in.twizmwaz.cardinal.util.Numbers;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class Destroyable extends Objective implements OwnedObjective, EntryUpdater {

  private final String name;
  private final Region region;
  private final MaterialPattern materials;
  private final Team owner;
  private final double completion; // Ranges between 0 and 1.
  private final boolean modeChanges;
  private final boolean showProgress;
  private final boolean repairable;
  private final boolean sparks;
  private final ProximityMetric proximityMetric;
  private final boolean proximityHorizontal;

  private final EntryHolder entryHolder = new EntryHolder();

  private final List<Player> touchedPlayers = new ArrayList<>();
  private final List<Team> touchedTeams = new ArrayList<>();
  private final Map<UUID, Integer> playerContributions = new HashMap<>();

  @Setter
  private int broken;
  private long total;

  private boolean completed;

  /**
   * @param match               The match the destroyable belongs to.
   * @param id                  This destroyable's ID.
   * @param name                This destroyable's name.
   * @param required            Determines if this objective is required to win the match.
   * @param region              The region that contains this destroyable.
   * @param materials           The materials that make up this destroyable.
   * @param owner               The owner of this destroyable.
   * @param completion          The percentage that this monument needs to be broken to be consider complete.
   * @param modeChanges         Determines if this destroyable follows mode changes.
   * @param showProgress        Determines if the progress of this destroyable is shown on the scoreboard.
   * @param repairable          Determines if this destroyable can be repaired.
   * @param sparks              Determines if sparks show when part of the destroyable is broken.
   * @param show                Determines if this destroyable is shown on the scoreboard.
   * @param proximityMetric     The proximity metric that determines how proximity is calculated.
   * @param proximityHorizontal Determines if only horizontal distance is considered when
   *                            calculating proximity.
   */
  public Destroyable(Match match, String id, String name, boolean required, Region region,
                     MaterialPattern materials, Team owner,
                     double completion, boolean modeChanges, boolean showProgress,
                     boolean repairable, boolean sparks, boolean show,
                     ProximityMetric proximityMetric, boolean proximityHorizontal) {
    super(match, id, required, show);
    this.name = name;
    this.materials = materials;
    this.owner = owner;
    this.completion = completion / 100;
    this.modeChanges = modeChanges;
    this.showProgress = showProgress;
    this.repairable = repairable;
    this.sparks = sparks;
    this.proximityMetric = proximityMetric;
    this.proximityHorizontal = proximityHorizontal;

    this.region = FiniteBlockRegion.getFromMaterialPattern(match, region, materials);
    total = this.region.getBlocks().size();

    AppliedModule appliedModule = Cardinal.getModule(AppliedModule.class);
    appliedModule.add(match,
        new AppliedRegion(ApplyType.BLOCK_BREAK, this.region,
            new AllFilter(new MaterialFilter(materials),
                new TransformFilter(new TeamFilter(owner), FilterState.DENY, FilterState.DENY, FilterState.ALLOW)),
            new LocalizedComponent(ChatConstant.getConstant("objective.destroyable.error.own")),
            true),
        true);
  }

  public boolean isTouched(Team team) {
    return touchedTeams.contains(team);
  }

  /**
   * Sets the destroyable as touched for a certain team. Scoreboard entries will be updated.
   * @param team The team that touched the destroyable.
   */
  public void setTouched(Team team) {
    if (!isTouched(team)) {
      touchedTeams.add(team);
      entryHolder.updateEntries();
    }
  }

  public boolean isPartOf(@NonNull Block block) {
    return region.contains(block.getLocation())
        && materials.contains(block.getType(), (int) block.getState().getMaterialData().getData());
  }

  /**
   * Breaks a number of pieces for a certain player. This will automatically show touch messages, and complete the
   * monument if it reaches completion percentage.
   * @param player The player that touched.
   * @param contribution The amount of pieces this player broke.
   */
  public void addBrokenPiecesFor(Player player, int contribution) {
    CompetitorContainer container = match.getPlayingContainer(player);
    if (!isCompleted() && container instanceof Team) {
      Team team = (Team) container;
      setTouched(team);
      if (show && !touchedPlayers.contains(player)) {
        touchedPlayers.add(player);
        Channels.getTeamChannel(match, team).sendPrefixedMessage(
            new LocalizedComponent(ChatConstant.getConstant("objective.destroyable.touched"),
                new TeamComponent(owner),
                new UnlocalizedComponent(name),
                new NameComponent(player)
            )
        );
      }

      UUID uuid = player.getUniqueId();
      playerContributions.putIfAbsent(uuid, 0);
      playerContributions.put(uuid, playerContributions.get(uuid) + contribution);
      broken += contribution;
      if ((double) broken / total >= completion) {
        completed = true;

        Channels.getGlobalChannel(match.getMatchThread()).sendMessage(
            new LocalizedComponentBuilder(
                ChatConstant.getConstant("objective.destroyable.completed"),
                new TeamComponent(owner),
                new UnlocalizedComponent(name),
                getContributionList()
            ).build()
        );

        Bukkit.getPluginManager().callEvent(new ObjectiveCompleteEvent(this, player));
      } else {
        Bukkit.getPluginManager().callEvent(new ObjectiveTouchEvent(this, player));
      }
      entryHolder.updateEntries();
    }
  }

  /**
   * Gets the completion percentage.
   * @return The percentage, always between 0 and 100;
   */
  public int getPercent() {
    if (isCompleted()) {
      return 100;
    }
    return (int) Numbers.between(Math.floor((double) broken / (total * completion) * 100), 0, 100);
  }

  private ListComponent getContributionList() {
    List<BaseComponent> contributions = new ArrayList<>();
    playerContributions.forEach((uuid, amount) -> {
      long percent = Math.round((double) amount / broken * 100);
      contributions.add(new UnlocalizedComponent("{0} ({1}%)",
          Components.getName(Bukkit.getOfflinePlayer(uuid)).build(),
          new UnlocalizedComponentBuilder(percent + "").color(ChatColor.AQUA).build()));
    });
    return new ListComponent(contributions);
  }

  /**
   * Gets the monument prefix for a given viewer team, for a specific attacker.
   * @param viewer The viewer team, null for observers.
   * @param attacker The team attacking the objective. Used to see if the team has a touch or not.
   * @return Color and monument state or percentage. Always between 3 and 6 characters (color + "100%").
   */
  @Override
  public String getPrefix(Team viewer, Team attacker) {
    if (isCompleted()) {
      return ChatColor.GREEN + getCompletionOrCharacter(viewer, Characters.CORE_COMPLETED);
    } else if (isTouched(attacker) && (viewer == null || viewer.equals(attacker))) {
      return ChatColor.YELLOW + getCompletionOrCharacter(viewer, Characters.CORE_TOUCHED);
    } else {
      return ChatColor.RED + getCompletionOrCharacter(viewer, Characters.CORE_INCOMPLETE);
    }
  }

  private String getCompletionOrCharacter(Team viewer, Characters character) {
    return viewer == null || isShowProgress() ? getPercent() + "%" : character + "";
  }

  @Override
  public UnlocalizedComponent getComponent() {
    return new UnlocalizedComponent(name);
  }

}
