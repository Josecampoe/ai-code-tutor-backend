package com.codeTutor.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO returned to the frontend when fetching code version history.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeSnapshotResponse {

    private Long id;
    private String content;
    private String versionLabel;
    private Integer versionNumber;
    private LocalDateTime createdAt;

    // Which project this snapshot belongs to
    private Long projectId;
}