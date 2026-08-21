package com.techfix.api.controllers;

import com.techfix.api.dto.ApiResponse;
import com.techfix.api.dto.BranchDto;
import com.techfix.api.dto.BranchRecommendationRequestDto;
import com.techfix.api.dto.BranchRecommendationResponseDto;
import com.techfix.api.services.BranchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchDto>>> getActiveBranches() {
        try {
            List<BranchDto> branches = branchService.getAllActiveBranches();
            return ResponseEntity.ok(ApiResponse.success("Branches retrieved successfully", branches));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/recommend-branch")
    public ResponseEntity<ApiResponse<BranchRecommendationResponseDto>> recommendBranch(
            @Valid @RequestBody BranchRecommendationRequestDto request) {
        try {
            BranchRecommendationResponseDto recommendation = branchService.getBranchRecommendation(request);
            return ResponseEntity.ok(ApiResponse.success("Recommended branch successfully determined", recommendation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
