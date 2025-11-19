package com.github.tecnoguard.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageDTO<T> {
    private List<T> content;
    @Schema(description = "Número da página", example = "1")
    private int pageNumber;
    @Schema(description = "Quantidade de elementos na página", example = "10")
    private int pageSize;
    @Schema(description = "Quantidade total de elementos na lista", example = "20")
    private long totalElements;
    @Schema(description = "Quantidade total de páginas", example = "2")
    private int totalPages;

    public PageDTO(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }

    public PageDTO() {
    }
}