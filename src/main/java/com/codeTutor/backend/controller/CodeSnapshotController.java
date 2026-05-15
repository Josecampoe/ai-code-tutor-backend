package com.codeTutor.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.dto.request.SaveCodeSnapshotRequest;
import com.codeTutor.backend.dto.response.CodeSnapshotResponse;
import com.codeTutor.backend.service.CodeSnapshotService;

import jakarta.validation.Valid;

/**
 * REST controller for the code_snapshots table.
 * Manages version history of student code within a project.
 */
@RestController
@RequestMapping("/api/snapshots")
public class CodeSnapshotController {

    @Autowired
    private CodeSnapshotService codeSnapshotService;

    /** POST /api/snapshots — Save a new code version */
    @PostMapping
    public ResponseEntity<CodeSnapshotResponse> saveSnapshot(
            @Valid @RequestBody SaveCodeSnapshotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(codeSnapshotService.saveSnapshot(request));
    }

    /** GET /api/snapshots/project/{projectId} — Get all snapshots for a project */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<CodeSnapshotResponse>> getSnapshotsByProject(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(codeSnapshotService.getSnapshotsByProject(projectId));
    }

    /** GET /api/snapshots/project/{projectId}/latest — Get the most recent snapshot */
    @GetMapping("/project/{projectId}/latest")
    public ResponseEntity<CodeSnapshotResponse> getLatestSnapshot(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(codeSnapshotService.getLatestSnapshot(projectId));
    }
}
