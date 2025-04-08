package com.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Range;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BaseTest implements CommandLineRunner {

    @Autowired
    private ElasticsearchOperations operations;

    @Override
    public void run(String... args) throws Exception {
        String id = save();
        TimeUnit.SECONDS.sleep(1);
        try {
            findById(id);
            findByCriteria(id);
            findByString(id);
            findByNative(id);
        } finally {
            delete(id);
        }
    }

    private String save() {
        Module module = new Module();
        module.setString("base");
        module.setText("this module");
        module.setDateTime(LocalDateTime.now());
        module.setRange(Range.open(1, 5));
        module.setPoint(new Point(1.23, 4.56));
        module.setArray(List.of("a1", "a2", "a3"));
        module.setInner(new Module.Inner("inner", "this is inner"));
        module.setInners(List.of(
                new Module.Inner("inner0", "this is inners[0]"),
                new Module.Inner("inner1", "this is inners[1]"),
                new Module.Inner("inner2", "this is inners[2]")
        ));
        operations.save(module);
        log.info("base save: {}", module.getId());
        return module.getId();
    }

    private void findById(String id) {
        Module module = Objects.requireNonNull(operations.get(id, Module.class));
        log.info("find by id[{}]: {}", id, module);
        Assertions.assertEquals("base", module.getString());
    }

    private void findByCriteria(String id) {
        Criteria criteria = new Criteria("string").is("base");
        SearchHits<Module> search = operations.search(new CriteriaQuery(criteria), Module.class);
        log.info("base search[criteria]: {}", search);
        log.info("base search[criteria] content: {}", search.getSearchHits().stream().map(SearchHit::getContent).toList());
        Assertions.assertTrue(search.getSearchHits().stream().map(SearchHit::getContent).map(Module::getId).anyMatch(id::equals));
    }

    private void findByString(String id) {
        StringQuery query = new StringQuery("""
                {
                    "term": {
                      "string.keyword": {
                        "value": "base"
                      }
                    }
                }
                """);
        SearchHits<Module> search = operations.search(query, Module.class);
        log.info("base search[string]: {}", search);
        log.info("base search[string] content: {}", search.getSearchHits().stream().map(SearchHit::getContent).toList());
        Assertions.assertTrue(search.getSearchHits().stream().map(SearchHit::getContent).map(Module::getId).anyMatch(id::equals));
    }

    private void findByNative(String id) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.term(t -> t.field("string.keyword").value("base")))
                .build();
        SearchHits<Module> search = operations.search(query, Module.class);
        log.info("base search[native]: {}", search);
        log.info("base search[native] content: {}", search.getSearchHits().stream().map(SearchHit::getContent).toList());
        Assertions.assertTrue(search.getSearchHits().stream().map(SearchHit::getContent).map(Module::getId).anyMatch(id::equals));
    }

    private void delete(String id) {
        operations.delete(id, Module.class);
        Assertions.assertNull(operations.get(id, Module.class));
    }

}
