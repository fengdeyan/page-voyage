package me.yan.utils;

import lombok.Data;

@Data
public class CommentResponse {
    private boolean success;
    private String msg;
    private Object data;
    public static CommentResponse success(String msg) {
       CommentResponse commentResponse = new CommentResponse();
       commentResponse.setSuccess(true);
       commentResponse.setMsg(msg);
       return commentResponse;
    }

    public static CommentResponse fail(String msg) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setSuccess(false);
        commentResponse.setMsg(msg);
        return commentResponse;
    }
}
