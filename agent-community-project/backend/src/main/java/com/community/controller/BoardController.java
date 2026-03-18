package com.community.controller;

import com.community.model.Board;
import com.community.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    
    private final BoardService boardService;
    
    @GetMapping
    public ResponseEntity<List<Board>> getBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        return ResponseEntity.ok(boardService.createBoard(board));
    }
}
