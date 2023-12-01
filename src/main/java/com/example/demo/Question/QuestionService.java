package com.example.demo.Question;

import com.example.demo.Answer.Answer;
import com.example.demo.DataNotFoundException;
import com.example.demo.User.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;


    //  글 생성
    public void create(String subject, String content, SiteUser user){
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(user);

        this.questionRepository.save(question);
    }

    // 글 삭제
    public void delete(Question question){
        this.questionRepository.delete(question);
    }

    // 글 수정
    public void modify(Question question, String subject, String content){
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    // 글 리스트 불러오기
    public List<Question> getList(){
        return questionRepository.findAll();
    }

    // 페이징을 위한 글 불러오기
    public Page<Question> getList(int page, String kw){
        List<Sort.Order> sorts = new ArrayList<>();

        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));

        Specification<Question> spec = search(kw);

        return this.questionRepository.findAll(spec, pageable);
    }
    // 글 찾기
    public Question getQusetion(Integer id){

        Optional<Question> question = questionRepository.findById(id);

        if(question.isPresent()){
            return question.get();
        }
        else{
            throw new DataNotFoundException("question not found");
        }
    }

    public void vote(Question question, SiteUser siteUser){
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    public void View(Question question){
        question.setView(question.getView() + 1);
        this.questionRepository.save(question);
    }


    //  검색
    private Specification<Question> search(String kw){

        return new Specification<Question>() {

            // 이건 뭐여
            private static final long serialVersionUID = 1L;


            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {

                //중복제거
                query.distinct(true);

                Join<Question,SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question,Answer> a = q.join("answerList",JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author",JoinType.LEFT);

                return cb.or(
                            cb.like(q.get("subject"), "%" + kw + "%"),
                            cb.like(q.get("content"), "%" + kw + "%"),
                            cb.like(u1.get("username"), "%" + kw + "%"),
                            cb.like(a.get("content"), "%" + kw + "%"),
                            cb.like(u2.get("username"), "%" + kw + "%")
                        );
            }
        };
    }


}
