package com.codeTutor.backend.dto.response;

import lombok.*;

/**
 * Response from code execution.
 * Contains stdout, stderr and exit code.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RunCodeResponse {

    private String stdout;   // normal output
    private String stderr;   // error output
    private String message;  // compile error or status message
    private Integer exitCode;
    private Double time;     // execution time in seconds
}
