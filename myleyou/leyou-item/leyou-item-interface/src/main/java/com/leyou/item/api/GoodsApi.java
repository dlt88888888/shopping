package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface GoodsApi {
    /**
     * 分页查询商品
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key",required = false) String key, //搜索条件
            @RequestParam(value = "saleable",required = false) Boolean saleable, //上下架
            @RequestParam(value = "page",defaultValue = "1") Integer page, //当前页
            @RequestParam(value = "rows",defaultValue = "5") Integer rows  // 每页显示几条数据
    );


    /**
     * 根据sku的id查询sku
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id")Long spuId);


    /**
     * 根据spu商品id查询规格参数详情
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);


    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);


    /**
     * 根据sku的id查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/{id}")
    public Sku querySkuById(@PathVariable("id") Long id);
}
