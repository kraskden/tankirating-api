package me.fizzika.tankirating.dto.alternativa.online;

import lombok.Data;

import java.util.Map;

@Data
public class AlternativaOnlineDTO {

    Map<String, AlternativaOnlineNode> nodes;

}
