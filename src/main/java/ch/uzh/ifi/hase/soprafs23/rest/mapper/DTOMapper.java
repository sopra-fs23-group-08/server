package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.TestGame;
import ch.uzh.ifi.hase.soprafs23.entity.TestPlayer;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TestGameGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TestPlayerWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  UserGetDTO convertEntityToUserGetDTO(User user);


  @Mapping(source = "username", target = "username")
  @Mapping(source = "score", target = "score")
  @Mapping(source = "token", target = "token")
  TestPlayerWsDTO convertEntityToTestPlayerWsDTO(TestPlayer player);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "score", target = "score")
  @Mapping(source = "token", target = "token")
  TestPlayer convertTestPlayerWsDTOtoEntity(TestPlayerWsDTO playerWsDTO);

  @Mapping(source = "id", target = "id")
  TestGameGetDTO convertEntityToTestGameGetDTO(TestGame game);

  @Mapping(source = "bigBlind", target = "bigBlind")
  @Mapping(source = "smallBlind", target = "smallBlind")
  @Mapping(source = "initialBalance", target = "initialBalance")
  @Mapping(source = "playlistUrl", target = "playlistUrl")
  @Mapping(source = "language", target = "language")
  SettingsDTO convertSettingsDTOtoEntity(SettingsDTO settingsDTO);
}
