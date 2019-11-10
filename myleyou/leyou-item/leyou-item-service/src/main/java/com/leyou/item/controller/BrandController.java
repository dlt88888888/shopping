package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     *  根据一些条件返回页面品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "key", required = false) String key, //搜索条件
            @RequestParam(value = "page", defaultValue = "1") Integer page, //当前页
            @RequestParam(value = "rows",defaultValue = "5") Integer rows, //每页几条数据
            @RequestParam(value = "sortBy" ,required = false) String sortBy,//排序字段
            @RequestParam(value = "desc",required = false) Boolean desc //是否降序
    ) {
        PageResult<Brand> result = this.brandService.queryBrandByPage(key, page, rows, sortBy, desc);
        if(CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand,@RequestParam("cids") List<Long> cids){

        this.brandService.saveBrand(brand,cids);

        return  ResponseEntity.status(HttpStatus.CREATED).build();

    }


    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        this.brandService.updateBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除品牌
     * @param bid
     * @return
     */
    @GetMapping("delete/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") Long bid){

        this.brandService.deleteBrandById(bid);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid")Long cid){
        List<Brand> brands = this.brandService.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brands)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(brands);
    }

    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id){
        Brand brand= this.brandService.queryBrandById(id);
        if (brand==null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(brand);
    }
}
