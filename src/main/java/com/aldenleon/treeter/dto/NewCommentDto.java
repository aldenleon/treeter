package com.aldenleon.treeter.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewCommentDto {
    @NotNull
    private Long parentId;

    @Size(max = 1024)
    @NotNull
    private String textContent;

    @NotNull
    private Long rootId;

    @NotNull
    private int maxDepth;

    @NotNull
    private int pageSize;
}
