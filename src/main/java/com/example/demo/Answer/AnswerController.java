package com.example.demo.Answer;

import com.example.demo.Question.Question;
import com.example.demo.Question.QuestionService;
import com.example.demo.User.SiteUser;
import com.example.demo.User.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/answer")
@Controller
public class AnswerController {

    private final AnswerService answerService;
    private final QuestionService questionService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable(value = "id") Integer id,
                               @Valid AnswerForm answerForm,
                               BindingResult bindingResult,
                               Principal principal)
    {
        Question question = this.questionService.getQusetion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if(bindingResult.hasErrors()){
            model.addAttribute(question);
            return "question_detail";
        }

        this.answerService.create(question,answerForm.getContent(),siteUser);
        return String.format("redirect:/question/detail/%s",id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal){

//        여기서 부터 시작해야함
//        Answer answer = this.answerService

        return "";
    }

}
