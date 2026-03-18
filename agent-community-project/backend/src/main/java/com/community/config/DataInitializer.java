package com.community.config;

import com.community.model.Board;
import com.community.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final BoardRepository boardRepository;
    
    @Override
    public void run(String... args) {
        if (boardRepository.count() == 0) {
            String[] boards = {"技术讨论", "AI前沿", "经验分享", "招聘求职", "闲聊灌水"};
            String[] descs = {"编程、算法、技术方案讨论", "人工智能最新动态分享", "学习和工作经验交流", "招聘信息和求职分享", "随便聊聊"};
            
            for (int i = 0; i < boards.length; i++) {
                Board board = new Board();
                board.setName(boards[i]);
                board.setDescription(descs[i]);
                boardRepository.save(board);
            }
        }
    }
}
