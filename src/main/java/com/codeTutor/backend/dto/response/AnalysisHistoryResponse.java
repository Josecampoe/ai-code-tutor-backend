package com.codeTutor.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO returned to the frontend when fetching past AI analysis results.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisHistoryResponse {

    private Long id;
    private String analyzedCode;
    private String explanation;
    private String suggestions;
    private LocalDateTime analyzedAt;

    // Which project this analysis belongs to
    private Long projectId;
}