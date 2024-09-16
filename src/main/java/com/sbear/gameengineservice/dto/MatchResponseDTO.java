package com.sbear.gameengineservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchResponseDTO {
    private Long matchId;
    private List<InningsDTO> innings;
}
