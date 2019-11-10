package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationSercice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationSercice specificationSercice;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = this.specificationSercice.queryGroupByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groups);
    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid",required = false) Long gid,//规格参数组group表的   id
            @RequestParam(value = "cid",required = false) Long cid, //
            @RequestParam(value = "generic",required = false) Boolean generic, //是否通用属性
            @RequestParam(value = "searching",required = false)Boolean searching //根据搜索
    ) {
        List<SpecParam> params = this.specificationSercice.queryParams(gid,cid,generic,searching);

        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(params);
    }


    /*@PostMapping("group")
    public ResponseEntity<Void> saveGroup( @RequestBody SpecGroup group){

        this.specificationSercice.saveGroup(group);


        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PutMapping("group")
    public ResponseEntity<Void> updateGroup( @RequestBody SpecGroup group){

        this.specificationSercice.updateGroup(group);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*/

    @RequestMapping("group")
    public ResponseEntity<Void> saveOrUpdateGroup(@RequestBody SpecGroup group, HttpServletRequest request) {

        String method = request.getMethod();
        if (group == null) {
            return ResponseEntity.notFound().build();
        }

        this.specificationSercice.saveOrUpdateGroup(group, method);
        return ResponseEntity.status(HttpStatus.CREATED).build();


    }

    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long id) {

        this.specificationSercice.deleteGroup(id);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> list=this.specificationSercice.querySpecsByCid(cid);

        if(CollectionUtils.isEmpty(list)){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

}
