package com.meetingintelligence.meetingservice.controller;

import com.meetingintelligence.meetingservice.dto.MeetingDetailResponse;
import com.meetingintelligence.meetingservice.dto.MeetingRequest;
import com.meetingintelligence.meetingservice.dto.MeetingResponse;
import com.meetingintelligence.meetingservice.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    ResponseEntity<MeetingResponse> create(@Valid @RequestBody MeetingRequest request,
                                           @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(meetingService.create(request, email));
    }

    @GetMapping
    List<MeetingResponse> list(@RequestHeader("X-User-Email") String email) {
        return meetingService.list(email);
    }

    @GetMapping("/{id}")
    MeetingDetailResponse get(@PathVariable Long id, @RequestHeader("X-User-Email") String email) {
        return meetingService.get(id, email);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Email") String email) {
        meetingService.delete(id, email);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/analyze")
    ResponseEntity<Map<String, String>> analyze(@PathVariable Long id,
                                                @RequestHeader("X-User-Email") String email) {
        meetingService.requestAnalysis(id, email);
        return ResponseEntity.accepted().body(Map.of("message", "Analysis requested"));
    }
    @PatchMapping("/{meetingId}/action-items/{itemId}/complete")
    MeetingDetailResponse.ActionItemResponse complete(@PathVariable Long meetingId,
                                                      @PathVariable Long itemId,
                                                      @RequestHeader("X-User-Email") String email) {
        return meetingService.completeItem(meetingId, itemId, email);
    }
}