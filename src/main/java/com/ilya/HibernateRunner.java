package com.ilya;

import com.ilya.entity.Company;
import com.ilya.entity.Role;
import com.ilya.entity.User;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
public class HibernateRunner {


    public static void main(String[] args) throws SQLException {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Company.class);
        configuration.configure();

        Company company = Company.builder()
                .name("Yandex")
                .build();

        User user = User.builder()
                .firstname("Petr1")
                .lastname("Petrov1")
                .birthDate(LocalDate.of(2000, 1, 2))
                .role(Role.ADMIN)
                .company(company)
                .build();

        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            Session session1 = sessionFactory.openSession();
            try (session1) {
                Transaction transaction = session1.beginTransaction();

                session1.save(company);
                session1.save(user);

                session1.getTransaction().commit();
                log.atInfo();
            }
        }


    }
}
