package me.yan;

import me.yan.pojo.ArticleDomain;
import me.yan.service.article.ArticleService;
import me.yan.service.web.HotArticleService;
import me.yan.utils.OSSUploadUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private HotArticleService hotArticleService;

    @Autowired
    private OSSUploadUtil ossUploadUtil;

    @Test
    public void testHotCache() throws Exception {
        hotArticleService.clearHotArticleCache();
        List<ArticleDomain> hotArticleList= hotArticleService.getHotArticleList(3);
        System.out.println("热点数据");
        for (ArticleDomain articleDomain : hotArticleList) {
            System.out.println(articleDomain.getTitle());
        }
    }

}
