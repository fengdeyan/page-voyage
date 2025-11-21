package me.yan.controller.admin;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import me.yan.constant.Types;
import me.yan.constant.WebConst;
import me.yan.controller.BaseController;
import me.yan.dto.cond.AttachCond;
import me.yan.pojo.AttachDomain;
import me.yan.pojo.UserDomain;
import me.yan.utils.APIResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/attach")
public class AttachController extends BaseController {
    @GetMapping(value = "")
    public String index(
            @RequestParam(name = "page", required = false, defaultValue = "1")
            int page,
            @RequestParam(name = "limit", required = false, defaultValue = "12")
            int limit,
            HttpServletRequest request
    ){
        List<AttachDomain> atts = attachService.getAtts(null, page, limit);
        request.setAttribute("attachs", atts);
        request.setAttribute("commons", commons);
        return "admin/attach";
    }
    @PostMapping("/uploadfile")
    public void upload(HttpServletRequest request,
                         HttpServletResponse response,
                         @Parameter(name = "editormd-image-file", description = "文件数组", required = true)
                                         @RequestParam(name = "editormd-image-file", required = true)
                         MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        String originalFilename = file.getOriginalFilename();
        String fkey = ossUploadUtil.upload(bytes, originalFilename);
        HttpSession session = request.getSession();
        UserDomain sessionUser = (UserDomain) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
        Integer fid = attachService.upload(originalFilename, Types.IMAGE.getType(), fkey);
        response.getWriter().write( "{\"success\": 1, \"message\":\"上传成功\",\"url\":\"" + fkey + "\"}" );
    }
    @PostMapping(value = "/delete")
    @ResponseBody
    public APIResponse deleteFileInfo(
            @RequestParam(name = "id", required = true)
            Integer id,
            HttpServletRequest request
    ){
        try {
            AttachDomain attAch = attachService.getAttAchById(id);
            attachService.deleteAttAch(id);
            return APIResponse.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
