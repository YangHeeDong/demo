package com.example.demo.Answer;

import com.example.demo.DataNotFoundException;
import com.example.demo.Question.Question;
import com.example.demo.Question.QuestionService;
import com.example.demo.User.SiteUser;
import com.example.demo.User.UserService;
import com.google.gson.Gson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        Answer answer = this.answerService.create(question,answerForm.getContent(),siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s",answer.getQuestion().getId(),answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal){

        Answer answer = this.answerService.getAnswer(id);

        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다");
        }

        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal){

        if(bindingResult.hasErrors()){
            return "answer_form";
        }

        Answer answer = this.answerService.getAnswer(id);

        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다");
        }

        this.answerService.modify(answer,answerForm.getContent());

        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(),answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(@PathVariable("id")Integer id , Principal principal){

        Answer answer = this.answerService.getAnswer(id);

        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제 권한이 없습니다.");
        }

        this.answerService.delete(answer);

        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote")
    @ResponseBody
    public String answerVote(Principal principal, @RequestBody Map<String, Object> paramMap){
        Answer answer = this.answerService.getAnswer(Integer.parseInt((String) paramMap.get("id")));
        SiteUser siteUser =this.userService.getUser(principal.getName());

        this.answerService.vote(answer,siteUser);
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("status","OK");
        map.put("recommend_count",answer.voter.size());


        // 추천 취소는 어떻게 해야 할까 나중에 해보자
        // 현재 구조에서는 여기서 넘겨주는게 좋음
        // 추천 수 넘겨주기

        //        {
        //            status : "OK",
        //            recommned_count : "12",
        //        }

        return gson.toJson(map);
    }

}
