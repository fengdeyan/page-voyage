package me.yan.dto;

import lombok.Data;

@Data
public class CommentDto {
    private String username;
    private String content;
    private int articleId;
}
