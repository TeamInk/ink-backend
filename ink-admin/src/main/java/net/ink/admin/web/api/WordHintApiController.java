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
import net.ink.admin.dto.WordHintDto;
import net.ink.admin.dto.mapper.WordHintMapper;
import net.ink.core.question.entity.Question;
import net.ink.core.question.entity.WordHint;
import net.ink.core.question.service.QuestionService;
import net.ink.core.question.service.WordHintService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WordHintApiController {
    private final QuestionService questionService;
    private final WordHintService wordHintService;
    private final WordHintMapper wordHintMapper;

    @AdminLogging
    @PostMapping("/question/{questionId}/word-hint")
    public ResponseEntity<?> addWordHint(@PathVariable Long questionId, @Valid @RequestBody WordHintDto wordHintDto) {
        Question question = questionService.getQuestionById(questionId);
        WordHint wordHint = wordHintMapper.toEntity(wordHintDto);
        question.getWordHints().add(wordHint);
        wordHint.setQuestion(question);
        questionService.update(question);
        return ResponseEntity.ok().build();
    }

    @AdminLogging
    @PutMapping("/word-hint/{hintId}")
    public ResponseEntity<?> updateWordHint(@PathVariable Long hintId, @Valid @RequestBody WordHintDto wordHintDto) {
        wordHintDto.setHintId(hintId);
        wordHintService.update(wordHintMapper.toEntity(wordHintDto));
        return ResponseEntity.ok().build();
    }

    @AdminLogging
    @DeleteMapping("/word-hint/{hintId}")
    public ResponseEntity<?> deleteWordHint(@PathVariable Long hintId) {
        wordHintService.deleteById(hintId);
        return ResponseEntity.ok().build();
    }
}