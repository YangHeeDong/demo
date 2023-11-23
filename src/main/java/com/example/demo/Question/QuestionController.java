package com.example.demo.Question;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/create")
    public String questionCreate(){
        return "question_create";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id){

        Question question = this.questionService.getQusetion(id);

        model.addAttribute("question",question);

        return "question_detail";
    }
}
