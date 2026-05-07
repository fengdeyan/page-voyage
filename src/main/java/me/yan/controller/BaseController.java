package me.yan.controller;

import me.yan.service.article.ArticleService;
import me.yan.service.attach.AttachService;
import me.yan.service.comment.CommentService;
import me.yan.service.meta.MetaService;
import me.yan.service.site.SiteService;
import me.yan.service.user.UserService;
import me.yan.service.web.HotArticleService;
import me.yan.utils.Commons;
import me.yan.utils.MapCache;
import me.yan.utils.OSSUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class BaseController {
    @Autowired
    public ArticleService articleService;
    @Autowired
    public CommentService commentService;
    @Autowired
    public UserService userService;

    @Autowired
    public Commons commons;

    @Autowired
    public SiteService siteService;

    @Autowired
    public MetaService metaService;

    @Autowired
    public OSSUploadUtil ossUploadUtil;

    @Autowired
    public AttachService attachService;

    @Autowired
    public HotArticleService hotArticleService;

    @Autowired
    public KafkaTemplate<String,String> kafkaTemplate;

    public MapCache cache=MapCache.single();
}
