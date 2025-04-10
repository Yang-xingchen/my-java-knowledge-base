package com.example;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StatsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class AggTest implements CommandLineRunner {

    @Autowired
    private ElasticsearchOperations operations;

    @Override
    public void run(String... args) throws Exception {
        List<AggModule> modules = save();
        Map<String, IntSummaryStatistics> res = aggByJava(modules);
        try {
            aggByEs(res);
        } finally {
            delete();
            log.info("agg complete");
        }
    }

    private List<AggModule> save() {
        Random random = new Random();
        List<AggModule> modules = new ArrayList<>();
        String[] keys = IntStream.range(0, 10).mapToObj(i -> "key" + i).toArray(String[]::new);
        for (int i = 0; i < 1000; i++) {
            AggModule module = new AggModule();
            module.setKey(keys[random.nextInt(10)]);
            module.setValue(random.nextInt(100));
            modules.add(module);
        }
        operations.save(modules);
        return modules;
    }

    private Map<String, IntSummaryStatistics> aggByJava(List<AggModule> modules) {
        return modules.stream().collect(Collectors.groupingBy(AggModule::getKey, Collectors.summarizingInt(AggModule::getValue)));
    }

    /**
     * <pre>
     *     {
     *         "aggs": {
     *             "res": {
     *                 "terms": {"field": "key"},
     *                 "aggs": {
     *                     "stats": {"field": "value"}
     *                 }
     *             }
     *         }
     *     }
     * </pre>
     */
    private void aggByEs(Map<String, IntSummaryStatistics> actual) throws InterruptedException {
        Aggregation aggsRes = Aggregation.of(atb -> atb
                .terms(tb -> tb.field("key.keyword"))
                .aggregations("stats", stb -> stb.
                        stats(tb -> tb.field("value"))
                )
        );
        NativeQuery query = NativeQuery.builder()
                .withAggregation("res", aggsRes)
                .build();

        // 查询
        SearchHits<AggModule> search = operations.search(query, AggModule.class);
        if (search.getTotalHits() < 1000) {
            // 若数据未添加完成，等待200ms后再次查询
            TimeUnit.MILLISECONDS.sleep(200);
            aggByEs(actual);
            return;
        }

        // 结果处理及比较
        StringTermsAggregate res = ((ElasticsearchAggregations) search.getAggregations())
                .aggregationsAsMap()
                .get("res")
                .aggregation()
                .getAggregate()
                .sterms();
        log.info("agg: {}", res);
        res.buckets().array().forEach(bucket -> {
            String key = bucket.key().stringValue();
            StatsAggregate stats = bucket.aggregations().get("stats").stats();
            IntSummaryStatistics statistics = actual.get(key);

            Assertions.assertEquals(statistics.getMax(), stats.max(), key + " fail");
            Assertions.assertEquals(statistics.getMin(), stats.min(), key + " fail");
            Assertions.assertEquals(statistics.getCount(), stats.count(), key + " fail");
            Assertions.assertEquals(statistics.getSum(), stats.sum(), key + " fail");
        });
    }

    private void delete() {
        String queryFormat = "{\"match_all\": {}}";
        DeleteQuery query = DeleteQuery.builder(new StringQuery(queryFormat)).build();
        operations.delete(query, AggModule.class);
    }

}
