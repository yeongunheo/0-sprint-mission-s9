package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserStatusMapper.class})
public interface UserMapper {

  @Mapping(target = "online", source = "status", qualifiedByName = "toOnline")
  UserDto toDto(User user);

  @Named("toOnline")
  default Boolean toOnline(UserStatus userStatus) {
    if (userStatus == null) {
      return null;
    }

    return userStatus.isOnline();
  }
}
