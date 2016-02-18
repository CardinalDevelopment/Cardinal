package in.twizmwaz.cardinal.module.team;

import in.twizmwaz.cardinal.module.AbstractModule;
import org.bukkit.entity.Player;
import org.jdom2.Document;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeamModule extends AbstractModule {

  private List<Team> teams;

  public TeamModule() {
    super("team");
  }

  @Override
  public boolean loadMatch(Document document) {
    return true;
  }

  @Override
  public void clearMatch() {
    teams.clear();
  }

  @Nullable
  public Team getTeamById(@Nonnull String id) {
    for (Team team : teams) {
      if (team.getId().replaceAll(" ", "").equalsIgnoreCase(id.replaceAll(" ", ""))) {
        return team;
      }
    }
    for (Team team : teams) {
      if (team.getId().replaceAll(" ", "").toLowerCase().startsWith(id.replaceAll(" ", "").toLowerCase())) {
        return team;
      }
    }
    for (Team team : teams) {
      if (team.getId().replaceAll(" ", "-").equalsIgnoreCase(id.replaceAll(" ", "-"))) {
        return team;
      }
    }
    for (Team team : teams) {
      if (team.getId().replaceAll(" ", "-").toLowerCase().startsWith(id.replaceAll(" ", "-").toLowerCase())) {
        return team;
      }
    }
    return null;
  }

  @Nullable
  public Team getTeamByName(@Nonnull String name) {
    for (Team team : teams) {
      if (team.getName().replaceAll(" ", "").toLowerCase().startsWith(name.replaceAll(" ", "").toLowerCase())) {
        return team;
      }
    }
    return null;
  }

  @Nullable
  public Team getTeamByPlayer(@Nonnull Player player) {
    for (Team team : teams) {
      if (team.contains(player)) {
        return team;
      }
    }
    return null;
  }

}
