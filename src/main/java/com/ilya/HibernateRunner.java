package com.ilya;

import com.ilya.entity.*;
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

        configuration.addAnnotatedClass(Employee.class);
        configuration.addAnnotatedClass(Order.class);
        configuration.addAnnotatedClass(SparePart.class);
        configuration.addAnnotatedClass(Vehicle.class);

        configuration.configure();

        Vehicle vehicle = Vehicle.builder()
                .brand("BMW")
                .model("3")
                .generation(2018)
                .build();

        Employee employee = Employee.builder()
                .name("Yaroslav")
                .surname("Kozhematko")
                .dateStart(LocalDate.of(2023, 5, 15))
                .salary(210_000.1214)
                .role(Roles.MANAGER)
                .build();

//        SparePart sparePart = SparePart.builder()
//                .name("Air filter")
//                .priceIn(1000.00)
//                .priceOut(1400.00)
//                .stock(6)
//                .vehicleId(new_veh)
//                .build();




        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            Session session1 = sessionFactory.openSession();
            try (session1) {
                session1.beginTransaction();
//                new_veh = session1.get(Vehicle.class, 1);

                //var employees = session1.get(Employee.class, 1);

                session1.createQuery("UPDATE Employee e SET e.salary = :newSalary WHERE e.id = :employeeId")
                        .setParameter("newSalary", 250000.0)
                        .setParameter("employeeId", 1).executeUpdate();





                //session1.update(sparePart);

                session1.getTransaction().commit();
            }
        }


    }
}
