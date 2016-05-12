package in.twizmwaz.cardinal.command.provider;

import ee.ellytr.command.argument.provider.ArgumentProvider;
import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.module.repository.LoadedMap;
import in.twizmwaz.cardinal.module.repository.RepositoryModule;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LoadedMapProvider implements ArgumentProvider<LoadedMap> {

  @Override
  public LoadedMap getMatch(String input) {
    Map<String, LoadedMap> maps = Cardinal.getModule(RepositoryModule.class).getLoadedMaps();
    Set<String> mapNames = maps.keySet();
    for (String mapName : mapNames.stream().filter(m -> m.startsWith(input)).collect(Collectors.toList())) {
      return maps.get(mapName);
    }
    return null;
  }

  @Override
  public List<String> getSuggestions(String input) {;
    return Cardinal.getModule(RepositoryModule.class).getLoadedMaps().keySet().stream().filter(
        map -> map.toLowerCase().startsWith(input.toLowerCase())).collect(Collectors.toList());

  }
}
