package com.itutorix.workshop.customer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomerSearchDAO {

    private final EntityManager entityManager;
    public static final String FORMATTER = "\"%[%s]%\"";

    public List<Customer> findAllBySimpleQuery(
            String name,
            String email,
            Integer age
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer> criteriaQuery = criteriaBuilder.createQuery(Customer.class);

        // select * from customer
        Root<Customer> root = criteriaQuery.from(Customer.class);
        // prepare the where clause
        Predicate namePredicate = criteriaBuilder
                .like(root.get("name"), FORMATTER.formatted(name));
        Predicate emailPredicate = criteriaBuilder
                .like(root.get("email"), FORMATTER.formatted(email));
        Predicate agePredicate = criteriaBuilder
                .equal(root.get("age"), age);

        Predicate namOrAgePredicate = criteriaBuilder.or(namePredicate, agePredicate);

        criteriaQuery.where(
                criteriaBuilder.and(namOrAgePredicate, emailPredicate)
        );

        TypedQuery<Customer> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Customer> findAllByCriteria(SearchRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer> criteriaQuery = criteriaBuilder.createQuery(Customer.class);

        List<Predicate> predicates = new ArrayList<>();

        Root<Customer> root = criteriaQuery.from(Customer.class);

        if(request.name() != null) {
            Predicate namePredicate = criteriaBuilder
                    .like(root.get("name"), FORMATTER.formatted(request.name()));
            predicates.add(namePredicate);
        }

        if(request.age() != null) {
            Predicate agePredicate = criteriaBuilder
                    .equal(root.get("age"), request.age());
            predicates.add(agePredicate);
        }

        if(request.email() != null) {
            Predicate emailPredicate = criteriaBuilder
                    .like(root.get("email"), FORMATTER.formatted(request.email()));
            predicates.add(emailPredicate);
        }

        criteriaQuery.where(
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );

        TypedQuery<Customer> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
