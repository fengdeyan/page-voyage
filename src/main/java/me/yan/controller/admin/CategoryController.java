package me.yan.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import me.yan.constant.Types;
import me.yan.controller.BaseController;
import me.yan.pojo.MetaDomain;
import me.yan.utils.APIResponse;
import me.yan.utils.AdminCommons;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分类管理", description = "文章分类管理相关接口")
@Controller
@RequestMapping("admin/category")
public class CategoryController extends BaseController {
    private final AdminCommons adminCommons;

    public CategoryController(AdminCommons adminCommons) {
        super();
        this.adminCommons = adminCommons;
    }

    @Operation(summary = "分类列表", description = "获取所有文章分类")
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

    @Operation(summary = "保存分类", description = "添加或更新分类")
    @PostMapping(value = "/save")
    @ResponseBody
    public APIResponse addCategory(
            @Parameter(description = "分类名称", required = true) @RequestParam(name = "cname", required = true)
            String cname,
            @Parameter(description = "分类ID（更新时使用）") @RequestParam(name = "mid", required = false)
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

    @Operation(summary = "删除分类", description = "删除指定分类")
    @PostMapping(value = "delete")
    @ResponseBody
    public APIResponse delete(
            @Parameter(description = "分类ID", required = true) @RequestParam(name = "mid", required = true)
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
