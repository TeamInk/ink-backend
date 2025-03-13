package net.ink.admin.web.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.ink.admin.annotation.AdminLogging;
import net.ink.admin.dto.QuestionDto;
import net.ink.admin.dto.mapper.QuestionMapper;
import net.ink.core.question.entity.Question;
import net.ink.core.question.service.QuestionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuestionApiController {
    private final QuestionService questionService;
    private final QuestionMapper questionMapper;

    @AdminLogging
    @PostMapping("/question")
    public ResponseEntity<?> addQuestion(@Valid @RequestBody QuestionDto questionDto) {
        questionService.create(questionMapper.toEntity(questionDto, null));
        return ResponseEntity.ok().build();
    }

    @AdminLogging
    @PutMapping("/question/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @Valid @RequestBody QuestionDto questionDto) {
        Question question = questionService.getQuestionById(questionId);
        question.setContent(questionDto.getContent());
        question.setKoContent(questionDto.getKoContent());
        questionService.update(question);
        return ResponseEntity.ok().build();
    }

    @AdminLogging
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable("questionId") Long questionId) {
        questionService.deleteById(questionId);
        return ResponseEntity.ok().build();
    }
}