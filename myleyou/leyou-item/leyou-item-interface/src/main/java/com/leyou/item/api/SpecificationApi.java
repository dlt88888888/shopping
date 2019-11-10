package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@RequestMapping("spec")
public interface SpecificationApi {
    @GetMapping("params")
    public List<SpecParam> queryParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid, //
            @RequestParam(value = "generic",required = false) Boolean generic, //是否通用属性
            @RequestParam(value = "searching",required = false)Boolean searching //根据搜索
    );


    @GetMapping("groups/{cid}")
    public List<SpecGroup> queryGroupByCid(@PathVariable("cid") Long cid);

    @GetMapping("{cid}")
    public List<SpecGroup> querySpecsByCid(@PathVariable("cid") Long cid);

}
