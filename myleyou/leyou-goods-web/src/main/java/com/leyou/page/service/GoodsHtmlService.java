package com.leyou.page.service;




import com.leyou.page.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;


@Service
public class GoodsHtmlService
{
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TemplateEngine templateEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);


    /**
     * 创建静态页面
     * @param spuId
     */
    public void creatHtml(Long spuId){
        PrintWriter writer=null;

        try {

                //获取页面数据
                Map<String, Object> spuMap = this.goodsService.loadData(spuId);
                //创建thymeleaf上下文对象
                Context context = new Context();

                //把数据放入上下文对象中
                context.setVariables(spuMap);

                //创建输出流
                File file = new File("H:\\JavaSoft\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
                writer= new PrintWriter(file);

                //执行页面静态化方法
                templateEngine.process("item",context,writer);
        }catch (Exception e){
            LOGGER.error("页面静态化出错：{}"+e,spuId);
        }finally {
            if(writer!=null){
                writer.close();
            }
        }


    }

    public void asynExcute(Long spuId){
        ThreadUtils.execute(()->creatHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                creatHtml(spuId);
            }
        });*/
    }

    public void deleteHtml(Long id) {
        File file = new File("H:\\JavaSoft\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
