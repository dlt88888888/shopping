package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam(value = "pid",defaultValue = "0") Long pid){
        if(pid==null ||pid<0){
           return   ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
       List<Category> categories= this.categoryService.queryCategoriesByPid(pid);
        if(CollectionUtils.isEmpty(categories)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categories);

    }

    /**
     * 通过品牌id查询商品分类
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){
        List<Category> list=this.categoryService.queryByBrandId(bid);
        if(list==null || list.size()<1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(list);
    }


    /**
     * 根据商品分类id，查询商品分类名称
     * @param ids
     * @return
     */
    @GetMapping("name")
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids") List<Long> ids){
        List<String> names=this.categoryService.queryNameByIdS(ids);
        if(CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(names);


    }



    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id) {
        List<Category> list = this.categoryService.queryAllByCid3(id);
        if (CollectionUtils.isEmpty(list)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);

    }
}
