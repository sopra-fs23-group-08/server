package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsWsDTO;
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

  /** DTO -> ENTITY*/
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "token", target = "token")
  Player convertPlayerDTOtoEntity(PlayerDTO playerDTO);

  @Mapping(source = "bigBlind", target = "bigBlind")
  @Mapping(source = "smallBlind", target = "smallBlind")
  @Mapping(source = "initialBalance", target = "initialBalance")
  @Mapping(source = "playlistUrl", target = "playlistUrl")
  @Mapping(source = "language", target = "language")
  Settings convertSettingsWsDTOtoEntity(SettingsWsDTO settingsWsDTO);

  /** ENTITY -> DTO */
  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "token", target = "token")
  PlayerDTO convertEntityToPlayerDTO(Player player);

  @Mapping(source = "title", target = "title")
  @Mapping(source = "thumbnailUrl", target = "thumbnailUrl")
  @Mapping(source = "views", target = "views")
  @Mapping(source = "likes", target = "likes")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "releaseDate", target = "releaseDate")
  VideoDataWsDTO convertEntityToVideoDataWsDTO(VideoData videoData);

  @Mapping(source = "bigBlind", target = "bigBlind")
  @Mapping(source = "smallBlind", target = "smallBlind")
  @Mapping(source = "initialBalance", target = "initialBalance")
  @Mapping(source = "playlistUrl", target = "playlistUrl")
  @Mapping(source = "language", target = "language")
  SettingsWsDTO convertEntityToSettingsWsDTO(Settings settings);
}
