package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.DatesFilter;
import me.fizzika.tankirating.dto.filter.GroupStatFilter;
import me.fizzika.tankirating.dto.target.GroupStatDTO;
import me.fizzika.tankirating.enums.track.GroupMeta;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.service.tracking.target.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/group")
@Tag(name = "Group", description = "API for groups")
public class GroupController {

    @Resource
    private GroupService groupService;

    @GetMapping("/{group}/stat")
    public GroupStatDTO groupStatistics(@PathVariable String group, @Valid GroupStatFilter filter) {
        GroupMeta groupMeta = GroupMeta.fromName(group)
                .orElseThrow(() -> new ExternalException("Group not found", HttpStatus.NOT_FOUND));
        return groupService.getGroupStatistics(groupMeta, filter);
    }
}
