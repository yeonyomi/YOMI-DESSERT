package com.Dessert.share.controller;

import com.Dessert.share.entity.Board;

import com.Dessert.share.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;


    @GetMapping("/board/write")//localhost:8080/board/write
    public String boardWriteForm() {

        return "boardwrite";
    }


    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        // 파일 업로드 관련 코드
        String filePath = "C:\\springboot3\\share\\src\\main\\resources\\static\\files"; // 실제 파일이 저장될 경로
        String fileName = file.getOriginalFilename(); // 업로드된 파일의 이름
        String fullFilePath = filePath + "/" + fileName;

        // 파일 저장 및 업로드 관련 로직 (이 부분은 필요한 서비스 메서드에서 처리)

        // board 객체에 파일 경로 및 파일명 설정
        board.setFilepath(fullFilePath);
        board.setFilename(fileName);

        boardService.write(board, file);

        model.addAttribute("message", "디저트 공유 성공!");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }


    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size=10, sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {

        Page<Board> list = null;

        if(searchKeyword == null){
            list = boardService.boardList(pageable);
        }else{
            list = boardService.boardSearchList(searchKeyword,pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1; /**0에서부터 시작하기 때문에 -1*/
        int startPage = Math.max(nowPage -4, 1);
        int endPage = Math.min(nowPage +5, list.getTotalPages());


        model.addAttribute("list",list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return"boardlist";
    }



    @GetMapping("/board/view")//localhost:8080/board/view?id-1
    public String boardView(Model model, Integer id){
        model.addAttribute("board",boardService.boardView(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id){
        boardService.boardDelete(id);
        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id,Model model){
        model.addAttribute("board",boardService.boardView(id));

        return "boardmodify";
    }


    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, @RequestParam(name = "file", required = false) MultipartFile file) throws Exception {
        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = "C:\\springboot3\\share\\src\\main\\resources\\static\\files"; // 실제 파일이 저장될 경로
            file.transferTo(new File(filePath, fileName));

            boardTemp.setFilepath("/files/" + fileName);
            boardTemp.setFilename(fileName);
        }

        boardService.update(boardTemp);

        return "redirect:/board/list";
    }

}