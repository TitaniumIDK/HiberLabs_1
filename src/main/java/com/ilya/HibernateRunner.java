package com.ilya;

import com.ilya.entity.*;
import com.ilya.util.Configurator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;

/*
вопрос в консоли какое авто на ремонт

ввод в консоль марки

далее вопрос по запчасти
с выводом клиенту типов запчастей

клиент вводит в консоль имя запчасти

ответ - наличие и стоимость
и пул работников которые это починят

далее выбирает работника через консоль и ему в ответ выкидывает его слоты
 */

@Slf4j
public class HibernateRunner {
    private static final Configurator configurator = new Configurator();
    private static String input;
    private static boolean inProc = false;
    static BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));

    private static final Runnable reader = new Runnable() {
        @SneakyThrows
        @Override
        public void run() {
            initialSout();
            while (true) {
                input = reader1.readLine();
                System.out.println(input);
                if (input != null && !input.isEmpty() && !inProc) {
                    switchCommand(input);
                }
            }
        }
    };

    public static void main(String[] args) {
        reader.run();
    }

    public static void initialSout() {
        System.out.println("""

                ------------ Welocome to AUTO SERVICE ------------
                                
                TYPE ON OF THE FOLLOWING COMMANDS
                                
                1 - ADD NEW CAR\
                                
                2 - ADD NEW SPARE PART [NOT DONE YET]\
                                
                3 - ADD NEW EMPLOYEE [NOT DONE YET]\
                                
                4 - IVAN TASK\
                                
                --------------------------------------------------""");
    }

    public static void switchCommand(String s) throws IOException {
        inProc = true;
        if (s.equalsIgnoreCase("1")) {
            System.out.println("""
                    --------------------------------------------------
                    TYPE NEW AUTO IN NEXT FORMAT\
                                        
                    brand-model-generation\
                                       
                    --------------------------------------------------""");
            waiting(reader1);
            processCarData(input);
        } else if (s.equalsIgnoreCase("4")) {
            System.out.println("""
                    --------------------------------------------------
                    TYPE AUTO TO REPAIR FROM THIS\
                                       \s
                    --------------------------------------------------""");

            var carsHere = getAvailableCars();
            carsHere.forEach(System.out::println);
            System.out.println("Type ID of your auto");
            waiting(reader1);
            var sparePartsHere = getAvailableSpareParts(carsHere.get(Integer.parseInt(input) - 1));
            //assert sparePartsHere != null;
            System.out.println(
                    IntStream.range(0, sparePartsHere.size())
                            .mapToObj(i -> i+1 + "-" + sparePartsHere.get(i).getName())
                            .toList()
            );
            System.out.println("Type ID of spare part");
            waiting(reader1);
            SparePart wantedSparePart = sparePartsHere.get(Integer.parseInt(input) - 1);
            System.out.println(
                    "SPARE PART: " + wantedSparePart.getName() +
                            "\nCOSTS: " + wantedSparePart.getPriceOut() + " $" +
                            "\nSTOCK: " + wantedSparePart.getStock() + " p."
            );
            System.out.println("Want to continue? (Y/N)");
            waiting(reader1);
            if (input.equalsIgnoreCase("Y")) {

                System.out.println("Available mechanics");
                List<Employee> availableEmployees = getAvailableEmployees(Roles.MECHANIC);
                System.out.println(availableEmployees);
                waiting(reader1);
                System.out.println(
                        getScheduleForEmployee(availableEmployees.get(Integer.parseInt(input) - 1))
                );

            } else {
                inProc = false;
                initialSout();
                return;
            }

        } else {
            System.out.println("Unsupported command\n");
        }
        inProc = false;
        initialSout();
    }

    private static Map<LocalDate, List<BusynessType>> getScheduleForEmployee(Employee employee) {
        Map<LocalDate, List<BusynessType>> schedule;
        var session = configurator.getSession();
        List<Schedule> scheduleAll;
        try (session) {
            session.beginTransaction();
            scheduleAll = session.createQuery("FROM Schedule WHERE emp = :employee", Schedule.class)
                    .setParameter("employee", employee)
                    .list();
            if (scheduleAll.isEmpty()) {
                System.out.println("No mechanics found in the database.");
                return null;
            }
            session.getTransaction().commit();
        }
        schedule = scheduleAll.stream()
                .collect(Collectors.toMap(
                        Schedule::getWorkDate, s -> List.of(s.getFirstThird(), s.getSecondThird(), s.getThirdThird())
                ));
        return schedule;
    }

    private static List<Employee> getAvailableEmployees(Roles roles){
        List<Employee> emp;
        var session = configurator.getSession();
        try (session) {
            session.beginTransaction();
            emp = session.createQuery("FROM Employee WHERE role = :someRole", Employee.class)
                    .setParameter("someRole", roles)
                    .list();
            if (emp.isEmpty()) {
                System.out.println("No mechanics found in the database.");
                return null;
            }
            session.getTransaction().commit();
        }

        return emp;
    }

    private static List<SparePart> getAvailableSpareParts(Vehicle forThatVehicle) {
        List<SparePart> spareParts;
        var session = configurator.getSession();
        try (session) {
            session.beginTransaction();
            spareParts = session.createQuery("FROM SparePart WHERE vehicleId = :searchID", SparePart.class)
                    .setParameter("searchID", forThatVehicle)
                    .list();
            if (spareParts.isEmpty()) {
                System.out.println("No spare parts found in the database.");
                return null;
            }
            session.getTransaction().commit();
        }
        return spareParts;
    }

    private static List<Vehicle> getAvailableCars() {
        var session = configurator.getSession();
        List<Vehicle> cars;
        try (session) {
            session.beginTransaction();
            cars = session.createQuery("FROM Vehicle", Vehicle.class).list();
            if (cars.isEmpty()) {
                System.out.println("No cars found in the database.");
                return null;
            }
            session.getTransaction().commit();
        }
        return cars;
    }

    private static void processCarData(String carData) {
        final Pattern CAR_DATA_PATTERN = Pattern.compile("^([a-zA-Z]+)-([a-zA-Z0-9]+)-([0-9]{4})$");
        Matcher matcher = CAR_DATA_PATTERN.matcher(carData);
        if (matcher.matches()) {
            String brand = matcher.group(1);
            String model = matcher.group(2);
            int generation = Integer.parseInt(matcher.group(3));
            addNewCar(brand, model, generation);
        } else {
            System.out.println("Invalid car data format. Please enter data in the format 'brand-model-generation'.");
        }
    }

    private static void addNewCar(String brand, String model, int generation) {
        var session1 = configurator.getSession();
        try (session1) {
            session1.beginTransaction();
            Vehicle existingCar = session1.createQuery("FROM Vehicle WHERE brand = :brand AND model = :model AND generation = :generation", Vehicle.class)
                    .setParameter("brand", brand)
                    .setParameter("model", model)
                    .setParameter("generation", generation)
                    .uniqueResult();
            if (existingCar == null) {
                Vehicle newCar = new Vehicle();
                newCar.setBrand(brand);
                newCar.setModel(model);
                newCar.setGeneration(generation);
                session1.merge(newCar);
                System.out.println("New car added: " + newCar);
            } else {
                // Обновляем существующую запись
                System.out.println("Car already exists: " + existingCar);
            }

            session1.getTransaction().commit();
        }
    }

    public static void waiting(BufferedReader reader1) throws IOException {
        input = "";
        while (input.isEmpty()) {
            input = reader1.readLine();
        }
    }

//    public static void processCommand(String command) {
//        // Здесь вы можете обрабатывать введенную команду
//        System.out.println("Received command: " + command);
//
//
//        Vehicle vehicle = Vehicle.builder()
//                .brand("BMW")
//                .model("3")
//                .generation(2018)
//                .build();
//
//        try (session1) {
//            session1.beginTransaction();
//            //var schedules = session1.get(Schedule.class, 1);
//            session1.save(vehicle);
//            session1.getTransaction().commit();
//        }
//    }
}
