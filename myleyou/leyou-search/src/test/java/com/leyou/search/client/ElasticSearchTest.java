package com.leyou.search.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Spu;
import com.leyou.search.LeyouSearchApplication;
import com.leyou.search.pojo.Goods;
import com.leyou.search.reponsitory.GoodsReponsitory;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouSearchApplication.class)
public class ElasticSearchTest {
    @Autowired
    private GoodsReponsitory goodsReponsitory;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;




    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void createIndex(){
        this.elasticsearchTemplate.createIndex(Goods.class);
        this.elasticsearchTemplate.putMapping(Goods.class);

        Integer page=1;
        Integer rows=100;

        do{
            //分批查询spuBo
            PageResult<SpuBo> pageResult = this.goodsClient.querySpuBoByPage(null, true, page, rows);

            //遍历spuBo集合转化为List<Goods>
            List<Goods> goodsList =pageResult.getItems().stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods((Spu) spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;

            }).collect(Collectors.toList());

            this.goodsReponsitory.saveAll(goodsList);

            //获取当前页的数据条数，如果是最后一页，没有100
            rows=pageResult.getItems().size();

            page++;

        }while (rows==100);
    }



}
