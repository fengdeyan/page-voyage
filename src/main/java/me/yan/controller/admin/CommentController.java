package me.yan.controller.admin;

import me.yan.controller.BaseController;
import me.yan.dto.CommentDto;
import me.yan.dto.cond.CommentCond;
import me.yan.pojo.CommentDomain;
import me.yan.service.comment.CommentService;
import me.yan.utils.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/comments")
public class CommentController extends BaseController {
    @GetMapping("")
    public String SelectCommentsByCond(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                       @RequestParam(required = false, defaultValue = "500") Integer pageSize,
                                       Model model) {
        CommentCond commentCond = new CommentCond();
        List<CommentDomain> comments = commentService.SelectCommentsByCond(commentCond, pageNum, pageSize);
        model.addAttribute("comments", comments);
        model.addAttribute("commons",commons);
        return "admin/comment_list";
    }
    @PostMapping("/status")
    @ResponseBody
    public APIResponse changeStatus(
            @RequestParam(name = "coid", required = true)
            Integer coid,
            @RequestParam(name = "status", required = true)
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
    @PostMapping(value = "/delete")
    @ResponseBody
    public APIResponse deleteComment(
            @RequestParam(name = "coid", required = true)
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
