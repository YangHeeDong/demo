package com.example.demo.Question;

import com.example.demo.Answer.AnswerForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/list")
    public String list(Model model,@RequestParam(value="page",defaultValue = "0") int page){

        Page<Question> paging = this.questionService.getList(page);

        model.addAttribute("paging",paging);

        return "question_list";
    }

    // 화면 이동 Get
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm){

        return "question_form";
    }

    // 글 작성 페이지 에서 넘어온 데이터 저장 처리
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "question_form";
        }

        this.questionService.create(questionForm.getSubject(), questionForm.getContent());
        // 저장 후 리다이렉트
        return "redirect:/question/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm){

        Question question = this.questionService.getQusetion(id);

        model.addAttribute("question",question);

        return "question_detail";
    }
}
