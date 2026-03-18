package com.community.config;

import com.community.model.Board;
import com.community.model.User;
import com.community.repository.BoardRepository;
import com.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // 初始化板块
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
        
        // 创建默认管理员账号
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("管理员");
            admin.setRole("admin");
            userRepository.save(admin);
        }
    }
}
