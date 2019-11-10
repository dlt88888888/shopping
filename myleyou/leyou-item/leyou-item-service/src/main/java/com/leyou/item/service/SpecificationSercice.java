package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMappepr;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecificationSercice {
    @Autowired
    private SpecGroupMappepr specGroupMappepr;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> groupList = this.specGroupMappepr.select(specGroup);
        return groupList;
    }

    /**
     * 根据gid查询规格参数
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParams(Long gid,Long cid,Boolean generic,Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        List<SpecParam> params = specParamMapper.select(specParam);
        return params;
    }


    @Transactional
    public void saveGroup(SpecGroup group) {
        int insert = this.specGroupMappepr.insert(group);
        System.out.println("是否插入成功" + insert);

    }

    @Transactional
    public void deleteGroup(Long id) {
        this.specGroupMappepr.deleteByPrimaryKey(id);
    }

    public void updateGroup(SpecGroup group) {

        int i = this.specGroupMappepr.updateByPrimaryKeySelective(group);
        System.out.println(i);

    }

    @Transactional
    public void saveOrUpdateGroup(SpecGroup group, String method) {
        if("POST".equals(method)){
            int insert = this.specGroupMappepr.insert(group);
            System.out.println("存储成功"+insert);
        }else if("PUT".equals(method)){
            int update = this.specGroupMappepr.updateByPrimaryKeySelective(group);
            System.out.println("更新"+update);
        }

    }

    public List<SpecGroup> querySpecsByCid(Long cid) {

        List<SpecGroup> groups = this.queryGroupByCid(cid);
        groups.forEach(g -> {
            //查询组内参数
            g.setParams(this.queryParams(g.getId(),null,null,null));
        });
        return groups;
    }
}
