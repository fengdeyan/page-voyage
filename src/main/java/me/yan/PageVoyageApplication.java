package me.yan;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.yan.pojo.ArticleDomain;
import me.yan.service.article.Impl.ArticleServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PageVoyageApplication {

    public static void main(String[] args) {
        SpringApplication.run(PageVoyageApplication.class, args);
    }
}
