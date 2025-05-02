package com.denisenko.reportservice.service;

import com.denisenko.reportservice.model.AggregationCriteria;
import com.denisenko.reportservice.model.FilterCriteria;
import com.denisenko.reportservice.model.ReportTemplate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

import static com.denisenko.reportservice.model.Operation.GROUP_BY;

@Component
public class ReportCalculator {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Map<String, Object>> calculate(ReportTemplate reportTemplate,
                                               Map<String, Object> parameters,
                                               LocalDateTime startDate,
                                               LocalDateTime endDate,
                                               Class<?> entityClass) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<?> root = query.from(entityClass);

        List<Selection<?>> selections = buildSelections(cb, root, reportTemplate.getAggregations());
        query.multiselect(selections);

        List<Predicate> predicates = buildPredicates(cb, root, reportTemplate.getFilters(), parameters, startDate, endDate);
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Expression<?>> groupByFields = buildGroupByExpressions(root, reportTemplate.getAggregations());
        if (!groupByFields.isEmpty())
            query.groupBy(groupByFields);

        List<Tuple> results = entityManager.createQuery(query).getResultList();
        return convertToMapList(results);
    }

    private List<Selection<?>> buildSelections(CriteriaBuilder cb, Root<?> root, List<AggregationCriteria> aggregations) {
        List<Selection<?>> selections = new ArrayList<>();
        for (AggregationCriteria aggregation : aggregations) {
            String field = aggregation.getField();
            switch (aggregation.getOperation()) {
                case SUM -> selections.add(cb.sum(root.get(field)).alias(field));
                case COUNT -> selections.add(cb.count(root.get(field)).alias(field));
                case GROUP_BY -> selections.add(root.get(field));
                default -> throw new IllegalArgumentException("Unsupported aggregation: " + aggregation.getOperation());
            }
        }
        return selections;
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            Root<?> root,
                                            List<FilterCriteria> filters,
                                            Map<String, Object> parameters,
                                            LocalDateTime startDate,
                                            LocalDateTime endDate) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.between(root.get("createdAt"), startDate, endDate));
        for (FilterCriteria filter : filters) {
            String field = filter.getField();
            Object value = parameters.get(field);
            if (Objects.isNull(value))
                throw new IllegalArgumentException("Missing parameter: " + field);

            switch (filter.getOperator()) {
                case EQUALS -> predicates.add(cb.equal(root.get(field), value));
                case NOT_EQUALS -> predicates.add(cb.notEqual(root.get(field), value));
                case GREATER_THAN -> predicates.add(cb.greaterThan(root.get(field), (Comparable) value));
                case LESS_THAN -> predicates.add(cb.lessThan(root.get(field), (Comparable) value));
                default -> throw new IllegalArgumentException("Unsupported operator: " + filter.getOperator());
            }
        }
        return predicates;
    }

    private List<Expression<?>> buildGroupByExpressions(Root<?> root, List<AggregationCriteria> aggregations) {
        List<Expression<?>> groupByFields = new ArrayList<>();
        for (AggregationCriteria aggregation : aggregations) {
            if (aggregation.getOperation() == GROUP_BY) {
                groupByFields.add(root.get(aggregation.getField()));
            }
        }
        return groupByFields;
    }

    private List<Map<String, Object>> convertToMapList(List<Tuple> results) {
        List<Map<String, Object>> mapped = new ArrayList<>();

        for (Tuple tuple : results) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (TupleElement<?> element : tuple.getElements()) {
                String alias = element.getAlias();
                Object value = tuple.get(alias);
                row.put(alias, value);
            }
            mapped.add(row);
        }

        return mapped;
    }
}
