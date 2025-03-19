package com.ilya.util;

import com.ilya.entity.*;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class Configurator {

    private final Configuration configuration;

    public Configurator() {
        configuration = new Configuration();
        configuration.addAnnotatedClass(Employee.class);
        configuration.addAnnotatedClass(Order.class);
        configuration.addAnnotatedClass(SparePart.class);
        configuration.addAnnotatedClass(Vehicle.class);
        configuration.addAnnotatedClass(Schedule.class);
        configuration.configure();
    }

    public Session getSession() {
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        return sessionFactory.openSession();
    }

}
