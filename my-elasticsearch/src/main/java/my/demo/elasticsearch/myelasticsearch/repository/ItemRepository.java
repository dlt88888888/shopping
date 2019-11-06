package my.demo.elasticsearch.myelasticsearch.repository;

import my.demo.elasticsearch.myelasticsearch.pojo.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;


public interface ItemRepository extends ElasticsearchRepository<Item,Long> {

    List<Item> findByPriceBetween (double price1,double price2);
}
