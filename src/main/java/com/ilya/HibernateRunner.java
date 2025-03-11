package com.ilya;

import com.ilya.entity.Role;
import com.ilya.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

public class HibernateRunner {
    public static void main(String[] args) throws SQLException {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.configure();

        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User user = User.builder()
                    .username("ivan@gmail.com")
                    .firstname("Ivan")
                    .lastname("Ivanov")
                    .birthDate(LocalDate.of(2000, 1, 18))
                    .age(25)
                    .role(Role.ADMIN)
                    .build();
            session.save(user);
            session.getTransaction().commit();
        }


    }
}
