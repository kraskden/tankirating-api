package me.fizzika.tankirating.dto.target;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.enums.track.GroupMeta;

@Data
@AllArgsConstructor
public class GroupStatDTO {
    private GroupMeta name;
    private long playedAccounts;
}
