package me.yan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.yan.pojo.ArticleDomain;
import me.yan.service.article.ArticleService;
import me.yan.utils.OSSUploadUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private OSSUploadUtil ossUploadUtil;

    @Test
    public void getArticlesByCond() throws Exception {
        File file = new File("C:\\Users\\Lenovo\\Pictures\\联想截图\\联想截图_20251110150114.png");
        byte[] bytes = Files.readAllBytes(file.toPath());
        ossUploadUtil.upload(bytes, "test.jpg");
    }
}
