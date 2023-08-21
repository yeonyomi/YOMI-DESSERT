package com.Dessert.share.service;
import com.Dessert.share.entity.Board;
import com.Dessert.share.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;


    /**글작성 처리*/
    public void write(Board board, MultipartFile file) throws Exception {

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        UUID uuid = UUID.randomUUID(); /**파일이름랜덤*/

        String fileName = uuid + "_" + file.getOriginalFilename();

        File saveFile = new File(projectPath,fileName);

        file.transferTo(saveFile);

        board.setFilename(fileName);
        board.setFilepath("/files/" + fileName); /**파일 저장 경로*/

        boardRepository.save(board);

    }


    /**게시글 리스트 처리*/
    public Page<Board> boardList(Pageable pageable) {

        return boardRepository.findAll(pageable);
    }
    /**게시글리스트 검색*/
    public Page<Board>boardSearchList(String searchKeyword, Pageable pageable){

        return boardRepository.findByTitleContaining(searchKeyword,pageable);
    }


    /**특정게시글 불러오기*/
    public Board boardView(Integer id){
        return boardRepository.findById(id).get();
    }


    /**특정 게시글 삭제*/
    public void boardDelete(Integer id){
        boardRepository.deleteById(id);
    }


}