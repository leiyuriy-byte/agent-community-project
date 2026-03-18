package com.community.service;

import com.community.model.Board;
import com.community.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    
    private final BoardRepository boardRepository;
    
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }
    
    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }
}
