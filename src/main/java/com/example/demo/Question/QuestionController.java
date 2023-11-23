package com.example.demo.Question;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/list")
    public String list(Model model){

        List<Question> questionList = this.questionService.getList();

        model.addAttribute("questionList",questionList);

        return "question_list";
    }

    // 화면 이동 Get
    @GetMapping("/create")
    public String questionCreate(){
        return "question_create";
    }

    // 글 작성 페이지 에서 넘어온 데이터 저장 처리
    @PostMapping("/create")
    public String questionCreate(@RequestParam String subject, @RequestParam String content){

        this.questionService.create(subject, content);
        // 저장 후 리다이렉트
        return "redirect:/question/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id){

        Question question = this.questionService.getQusetion(id);

        model.addAttribute("question",question);

        return "question_detail";
    }
}
