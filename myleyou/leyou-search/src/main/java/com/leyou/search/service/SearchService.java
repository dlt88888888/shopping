package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.reponsitory.GoodsReponsitory;
import com.leyou.search.pojo.SearchRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;


    @Autowired
    private GoodsReponsitory goodsReponsitory;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        //创建goods对象

        Goods goods = new Goods();

        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());


        //查询分类名称

        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //查询spu下的所有sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuMapList = new ArrayList<>();

        //遍历skus
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            HashMap<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });

        //查询出所有的搜索规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), null, true);

        //查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());

        // 获取通用的规格参数
        // MAPPER  反序列化   eadValue(数据,需要反序列化的类型)   在这里我们使用jackson提供的高级反序列化工具将其反序列化成一个Map集合
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });

        //特殊的规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });

        //定义map接受{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();

        params.forEach(param -> {

            //判断是否通用规格参数
            if (param.getGeneric()) {
                //获取通用规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                //判断是否是数值类型
                if (param.getNumeric()) {
                    //如果是数值的话，判断该数值落在哪个区间
                    value = chooseSegment(value, param);

                }

                //把参数名和值放入结果集中

                paramMap.put(param.getName(), value);


            } else {

                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }

        });


        //设置参数

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);
        return goods;
    }

    private String chooseSegment(String value, SpecParam param) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        for (String segment : param.getSegments().split(",")) {
            String[] segs = segment.split("-");
            //获取数值范围

            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;

            if (segs.length == 2) {

                end = NumberUtils.toDouble(segs[1]);
            }

            //判断是否在范围内

            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + param.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + param.getUnit() + "以下";

                } else {

                    result = segment + param.getUnit();
                }
                break;

            }


        }
        return result;
    }


    /**
     * 根据搜索条件分页
     *
     * @param request
     * @return
     */

    public PageResult<Goods> search(SearchRequest request) {
        String key = request.getKey();

        if (StringUtils.isBlank(key)) {
            return null;
        }

        //构建查询条件
       NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();


        //对key进行全文检索
        //QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);

        BoolQueryBuilder boolQueryBuilder=buildBooleanQueryBuilder(request);


        queryBuilder.withQuery(boolQueryBuilder);

        // 通过sourFilter设置返回的结果字段  id 、skus、subTitle

        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{
                "id", "skus", "subTitle"
        }, null));

        //分页
        Integer page = request.getPage();
        Integer size = request.getSize();

        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        String categoryAggName = "categories";
        String brandAggName = "brands";

        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));

        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));


        //排序
        String sortBy = request.getSortBy();

        Boolean descending = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending ? SortOrder.DESC : SortOrder.ASC));
        }

        //查询、获取结果

        // Page<Goods> goodsPage = this.goodsReponsitory.search(queryBuilder.build());
        //获取结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsReponsitory.search(queryBuilder.build());

        //解析聚合结果集
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));

        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));
        //判断分类的聚合结果集大小，等于1则聚合


        List<Map<String, Object>> specs = null;
        if (categories.size() == 1) {
            specs = getParamAggResult((Long) categories.get(0).get("id"), boolQueryBuilder);
        }


        //总条数
        Long total = goodsPage.getTotalElements();
        //总页数

        int totalPage = (total.intValue() + size - 1) / size;

        //分装结果集
        // return new PageResult<>(total, (long)totalPage, goodsPage.getContent());
        return new SearchResult(goodsPage.getTotalElements(), (long) goodsPage.getTotalPages(), goodsPage.getContent(), categories, brands, specs);
    }


    /**
     * 构建bool查询构建器
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //添加基本查询条件

        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));

        //添加过滤条件

        if(CollectionUtils.isEmpty(request.getFilter())){
            return boolQueryBuilder;
        }

        for (Map.Entry<String, String> entry : request.getFilter().entrySet()) {
            String key=entry.getKey();

            //如果过滤条件是品牌   过滤字段为brandId
            if(StringUtils.equals("品牌",key)){
                key="brandId";
            }else if (StringUtils.equals("分类",key)){
                key="cid3";
            }else {
                //如果是规格参数名
                key="specs."+key+".keyword";
            }

            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }

        return boolQueryBuilder;
    }

    /**
     * 聚合出规格参数 过滤条件
     *
     * @param id
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long id, QueryBuilder basicQuery) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        List<SpecParam> params = this.specificationClient.queryParams(null, id, null, true);
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsReponsitory.search(queryBuilder.build());
        //定义一个集合  收集聚合结果集
        List<Map<String, Object>> paramMapList = new ArrayList<>();
        //解析聚合结果集

        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();

        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {

            HashMap<String, Object> map = new HashMap<>();
            //放入规格参数名
            map.put("k", entry.getKey());
            //收集规格参数值
            List<Object> options = new ArrayList<>();
            //解析每个聚合

            StringTerms terms = (StringTerms) entry.getValue();
            //遍历每个聚合桶  把桶中key 放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket ->
                    options.add(bucket.getKeyAsString()));


            map.put("options", options);
            paramMapList.add(map);
        }

        return paramMapList;

    }


    /**
     * 解析分类
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        //处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;

        //获取所有品牌的桶
        /*List<LongTerms.Bucket> buckets = terms.getBuckets();

        List<Brand> brands=new ArrayList<>();

        buckets.forEach(bucket -> {
            Brand brand = this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
            brands.add(brand);
        });

        return brands;*/

        //新特性写法
        return terms.getBuckets().stream().map(bucket -> this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue())).collect(Collectors.toList());

    }


    /**
     * 解析分类
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        //处理聚合结果集


        LongTerms terms = (LongTerms) aggregation;

        //获取所有的分类id桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        //定义一个品牌集合，搜集所有的品牌对象
        List<Map<String, Object>> categories = new ArrayList<>();
        List<Long> cids = new ArrayList<>();

        //解析所有的id桶  ，查询品牌
        buckets.forEach(bucket -> {
            cids.add(bucket.getKeyAsNumber().longValue());
        });

        List<String> names = this.categoryClient.queryNameByIds(cids);
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }
        return categories;
    }

    public void createIndex(Long id)throws IOException{
        Spu spu = this.goodsClient.querySpuById(id);

        //构建商品
        Goods goods = this.buildGoods(spu);

        //保存数据到索引库
        this.goodsReponsitory.save(goods);
    }

    public void deleteIndex(Long id){
        this.goodsReponsitory.deleteById(id);
    }
}
