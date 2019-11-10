package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuBoByPage(
            @RequestParam(value = "key",required = false) String key, //搜索条件
            @RequestParam(value = "saleable",required = false) Boolean saleable, //上下架
            @RequestParam(value = "page",defaultValue = "1") Integer page, //当前页
            @RequestParam(value = "rows",defaultValue = "5") Integer rows  // 每页显示几条数据

    ){
        PageResult<SpuBo> pageResult=this.goodsService.querySpuBoByPage(key,saleable,page,rows);

        if(CollectionUtils.isEmpty(pageResult.getItems())){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pageResult);
    }


    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        this.goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId){
       SpuDetail spuDetail= this.goodsService.querySpuDetailBySpuId(spuId);
         if(spuDetail==null){
             return ResponseEntity.notFound().build();
         }
         return ResponseEntity.ok(spuDetail);
    }


    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id")Long spuId){
        List<Sku> skus=this.goodsService.querySkuBySpuId(spuId);

        if(CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(skus);
    }

    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        this.goodsService.updateGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("goods/delete/{id}")
    public ResponseEntity<Void> deleteGoods(@PathVariable("id")Long id){
        this.goodsService.deleteGoods(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("goods/editSaleable")
    public ResponseEntity<Void> editSaleableBySpuId(@RequestParam("saleable") Boolean saleable,@RequestParam("id") Long id){
        this.goodsService.editSaleableBySpuId(saleable,id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu =this.goodsService.querySpuById(id);
        if(spu==null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(spu);
    }

    /**
     * 根据sku的id查询单个的sku
     * @param id
     * @return
     */

    @GetMapping("sku/{id}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("id") Long id){
        Sku sku = this.goodsService.queryskuById(id);
        if(sku==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(sku);
    }
}
