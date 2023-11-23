package com.example.demo.Answer;

import com.example.demo.Question.Question;
import com.example.demo.Question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/answer")
@Controller
public class AnswerController {

    private final AnswerService answerService;
    private final QuestionService QuestionService;

    @PostMapping("/create/{id}")
    public String updateAnswer(Model model, @PathVariable(value = "id") Integer id, @RequestParam String content){
        Question question = QuestionService.getQusetion(id);
        this.answerService.create(question,content);
        return String.format("redirect:/question/detail/%s",id);
    }
}
