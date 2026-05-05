package me.yan.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.yan.controller.BaseController;
import me.yan.dto.cond.CommentCond;
import me.yan.pojo.CommentDomain;
import me.yan.utils.APIResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论管理", description = "评论管理相关接口")
@Controller
@RequestMapping("/admin/comments")
public class CommentController extends BaseController {

    @Operation(summary = "评论列表", description = "获取评论分页列表")
    @GetMapping("")
    public String SelectCommentsByCond(@Parameter(description = "页码") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                       @Parameter(description = "每页数量") @RequestParam(required = false, defaultValue = "500") Integer pageSize,
                                       Model model) {
        CommentCond commentCond = new CommentCond();
        List<CommentDomain> comments = commentService.SelectCommentsByCond(commentCond, pageNum, pageSize);
        model.addAttribute("comments", comments);
        model.addAttribute("commons",commons);
        return "admin/comment_list";
    }

    @Operation(summary = "更新评论状态", description = "更新指定评论的状态")
    @PostMapping("/status")
    @ResponseBody
    public APIResponse changeStatus(
            @Parameter(description = "评论ID", required = true) @RequestParam(name = "coid", required = true)
            Integer coid,
            @Parameter(description = "评论状态", required = true) @RequestParam(name = "status", required = true)
            String status
    ){
        try {
            CommentDomain comment = commentService.SelectCommentById(coid);
            if (null != comment){
                commentService.updateCommentStatus(coid, status);
            }else{
                return APIResponse.fail("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return APIResponse.fail(e.getMessage());
        }
        return APIResponse.success();
    }

    @Operation(summary = "删除评论", description = "删除指定评论")
    @PostMapping(value = "/delete")
    @ResponseBody
    public APIResponse deleteComment(
            @Parameter(description = "评论ID", required = true) @RequestParam(name = "coid", required = true)
            Integer coid
    ){

        try {
            CommentDomain comment = commentService.SelectCommentById(coid);

            commentService.deleteComment(coid);
        } catch (Exception e) {
            e.printStackTrace();
            return APIResponse.fail(e.getMessage());
        }
        return APIResponse.success();
    }
}
