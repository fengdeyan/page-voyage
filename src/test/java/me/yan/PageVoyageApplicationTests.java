package me.yan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.yan.dao.ArticleMapper;
import me.yan.pojo.ArticleDomain;
import me.yan.service.article.ArticleService;
import me.yan.utils.OSSUploadUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Files;

@SpringBootTest
class PageVoyageApplicationTests {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private OSSUploadUtil ossUploadUtil;

    @Test
    void contextLoads() throws Exception {
        File file = new File("C:\\Users\\Lenovo\\Pictures\\联想截图\\联想截图_20251110150114.png");
        byte[] bytes = Files.readAllBytes(file.toPath());
        String upload = ossUploadUtil.upload(bytes, "test2.jpg");
        System.out.println("上传成功后的：" + upload);
    }

}
