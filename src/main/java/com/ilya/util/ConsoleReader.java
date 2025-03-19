package com.ilya.util;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleReader implements Runnable {
    private volatile boolean running;
    @Getter
    private String input;

    public ConsoleReader() {
        running = true;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (running) {
                input = reader.readLine();
                if (input != null && !input.isEmpty()) {
                    processCommand(input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processCommand(String command) {
        System.out.println("Received command: " + command);
    }

    public void stop() {
        running = false;
    }
}

//        Vehicle vehicle = Vehicle.builder()
//                .brand("BMW")
//                .model("3")
//                .generation(2018)
//                .build();
//
//        Employee employee = Employee.builder()
//                .name("Yaroslav")
//                .surname("Kozhematko")
//                .dateStart(LocalDate.of(2023, 5, 15))
//                .salary(210_000.1214)
//                .role(Roles.MANAGER)
//                .build();

//        var session1 = configurator.getSession();
//            try (session1) {
//                session1.beginTransaction();
////                new_veh = session1.get(Vehicle.class, 1);
//
//                //var employees = session1.get(Employee.class, 1);
//
////                session1.createQuery("UPDATE Employee e SET e.salary = :newSalary WHERE e.id = :employeeId")
////                        .setParameter("newSalary", 150011.0)
////                        .setParameter("employeeId", 3).executeUpdate();
//
////                System.out.println(session1.createQuery(
////                        "SELECT s FROM Schedule s JOIN Employee e ON e.id = s.emp.id WHERE e.id = 1", Schedule.class)
////                        .list()
////                    );
//
//                var schedules = session1.get(Schedule.class, 1);
//
//                session1.getTransaction().commit();
//            }
//}
