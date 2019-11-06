package my.demo.elasticsearch.myelasticsearch;

import my.demo.elasticsearch.myelasticsearch.pojo.Item;
import my.demo.elasticsearch.myelasticsearch.repository.ItemRepository;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyElasticsearchApplication.class)
public class IndexTest {
    @Autowired
    private ItemRepository itemRepository;


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testCreat() {
        //创建索引，会根据Item的Document注解信息来创建
        elasticsearchTemplate.createIndex(Item.class);
        //自动配置映射信息
        elasticsearchTemplate.putMapping(Item.class);

    }


    @Test
    public void testDelete() {
        elasticsearchTemplate.deleteIndex(Item.class);
    }

    @Test
    public void index() {
        Item item = new Item(1L, "小米手机7", " 手机",
                "小米", 3499.00, "http://image.leyou.com/13123.jpg");
        itemRepository.save(item);
    }

    @Test
    public void indexList() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        // 接收对象集合，实现批量新增
        itemRepository.saveAll(list);
    }


    @Test
    public void testQuery(){
        /*Optional<Item> optional = this.itemRepository.findById(1L);
        System.out.println(optional.get());*/

        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("title","小米");
        Iterable<Item> items = this.itemRepository.search(queryBuilder);
        items.forEach(System.out::println);


    }

    @Test
    public void testFind(){


        Iterable<Item> items = this.itemRepository.findAll(
                Sort.by(Sort.Direction.DESC, "price")
        );

        items.forEach(item -> System.out.println(item));
    }

    @Test
    public void queryByPriceBetween(){
        List<Item> list = this.itemRepository.findByPriceBetween(2000.00, 3500.00);
        for(Item item:list){
            System.out.println("item= "+item);
        }
    }


    @Test
    public void testNativeQuery(){

        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //添加基本的分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title","手机"));

        //初始化分页参数
        int page=0;
        int size=3;

        //设置分页参数


        queryBuilder.withPageable(PageRequest.of(page,size));


        Page<Item> items = this.itemRepository.search(queryBuilder.build());

        System.out.println(items.getTotalElements());

        System.out.println(items.getTotalPages());

        //每页大小
        System.out.println(items.getSize());

        //当前页
        System.out.println(items.getNumber());

        // 等同于  items.forEach(item -> System.out.println(item));
        items.forEach(System.out::println);




    }

    @Test
    public void testSort(){

        //构造查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //添加分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category","手机"));

        //排序

        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        Page<Item> items = this.itemRepository.search(queryBuilder.build());

        System.out.println(items.getTotalElements());

        items.forEach(System.out::println);

    }


    @Test
    public  void testAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter((new String[]{""}), null));
        //1.添加一个新的聚合，

        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        //将结果强转为AggregatedPage
        AggregatedPage<Item> aggPage = (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());

        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");

        List<StringTerms.Bucket> buckets = agg.getBuckets();


        for(StringTerms.Bucket bucket:buckets){
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
        }
    }

    @Test
    public void testStuAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand").subAggregation(AggregationBuilders.avg("priceAvg").field("price")));

        AggregatedPage<Item> aggPage= (AggregatedPage<Item>) this.itemRepository.search(queryBuilder.build());

        StringTerms agg = (StringTerms) aggPage.getAggregation("brands");
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        for(StringTerms.Bucket bucket:buckets){
            System.out.println(bucket.getKeyAsString()+ "共"+bucket.getDocCount()+"台");
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("平均售价： "+avg.getValue());
        }




    }
}


