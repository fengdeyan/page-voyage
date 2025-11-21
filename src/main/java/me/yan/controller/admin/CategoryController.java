package me.yan.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import me.yan.constant.Types;
import me.yan.controller.BaseController;
import me.yan.pojo.MetaDomain;
import me.yan.utils.APIResponse;
import me.yan.utils.AdminCommons;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("admin/category")
public class CategoryController extends BaseController {
    private final AdminCommons adminCommons;

    public CategoryController(AdminCommons adminCommons) {
        super();
        this.adminCommons = adminCommons;
    }

    @GetMapping(value = "")
    public String index(HttpServletRequest request){
        List<MetaDomain> categories = metaService.getMetasByType(Types.CATEGORY.getType());
        //遍历categories，为每个category添加随机颜色
        for (MetaDomain category : categories) {
            int i = articleService.countArticlesByCategory(category);
            category.setCount(i);
        }
        request.setAttribute("categories", categories);
        request.setAttribute("adminCommons",adminCommons);
        request.setAttribute("commons",commons);
        return "admin/category";
    }

    @PostMapping(value = "/save")
    @ResponseBody
    public APIResponse addCategory(
            @RequestParam(name = "cname", required = true)
            String cname,
            @RequestParam(name = "mid", required = false)
            Integer mid
    ){
        try {
            metaService.saveMeta(Types.CATEGORY.getType(),cname,mid);

        } catch (Exception e) {
            e.printStackTrace();
            String msg = "分类保存失败";
            return APIResponse.fail(msg);
        }
        return APIResponse.success();
    }
    @PostMapping(value = "delete")
    @ResponseBody
    public APIResponse delete(
            @RequestParam(name = "mid", required = true)
            Integer mid
    ){
        System.out.println("mid = " + mid);
        try {
            metaService.deleteMetaById(mid);

        } catch (Exception e) {
            e.printStackTrace();
            return APIResponse.fail(e.getMessage());
        }
        return  APIResponse.success();
    }
}
