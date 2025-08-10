package com.yazikochesalna.userservice.controller.externalcontroller;

import com.yazikochesalna.userservice.data.entity.UserElasticsearch;
import com.yazikochesalna.userservice.dto.ElasticsearchResponseDTO;
import com.yazikochesalna.userservice.service.externalservice.ElasticsearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User API")
public class SearchUserController {

    private final ElasticsearchService elasticsearchService;

    @GetMapping("/elasticsearch")
    @Operation(summary = "Получить пользователей по userName, фио", description = "Возвращает максимум 40 первых пользователей")
    public ElasticsearchResponseDTO searchUsers(
            @RequestParam("query")
            @NotBlank
            String query) {
        List<UserElasticsearch> findUsers = elasticsearchService.searchUsers(query);

        return new ElasticsearchResponseDTO(findUsers);
    }

}
