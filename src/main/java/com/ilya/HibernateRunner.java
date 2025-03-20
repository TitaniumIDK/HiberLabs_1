package com.ilya;

import com.ilya.entity.*;
import com.ilya.util.Configurator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                //System.out.println(input);
                if (input != null && !input.isEmpty() && !inProc) {
                    switchCommand(input);
                }
            }
        }
    };

    public static void main(String[] args) throws IOException {
        reader.run();
        initialSout();
        waiting(reader1);
        switchCommand(input);
    }

    public static void initialSout() {
        System.out.println("""

                ------------ Welocome to AUTO SERVICE ------------
                               \s
                TYPE ON OF THE FOLLOWING COMMANDS
                               \s
                1 - ADD NEW CAR\
                               \s
                2 - ADD NEW SPARE PART [NOT DONE YET]\
                               \s
                3 - ADD NEW EMPLOYEE [NOT DONE YET]\
                               \s
                4 - CREATE ORDER (IVAN TASK)\
                               \s
                --------------------------------------------------""");
    }

    public static void switchCommand(String s) throws IOException {
        inProc = true;
        if (s.equalsIgnoreCase("1")) {
            System.out.println("""
                    --------------------------------------------------
                    TYPE NEW AUTO IN NEXT FORMAT\
                                       \s
                    brand-model-generation\
                                      \s
                    --------------------------------------------------""");
            waiting(reader1);
            processCarData(input);
        } else if (s.equalsIgnoreCase("4")) {

            var carsHere = getAvailableCars();

            System.out.println(
                    IntStream.range(0, carsHere.size())
                            .mapToObj(i -> "\n" + (i + 1)
                                    + "-" + carsHere.get(i).getBrand()
                                    + " " + carsHere.get(i).getModel()
                                    + " " + carsHere.get(i).getGeneration())
                            .toList()
            );

            System.out.println("Type ID of your auto");
            waiting(reader1);
            Vehicle wantedCar = carsHere.get(Integer.parseInt(input) - 1);
            var sparePartsHere = getAvailableSpareParts(carsHere.get(Integer.parseInt(input) - 1));

            if (sparePartsHere != null) {
                System.out.println(
                        IntStream.range(0, sparePartsHere.size())
                                .mapToObj(i -> "\n" + (i + 1) + "-" + sparePartsHere.get(i).getName())
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
                    List<Employee> availableEmployees = getAvailableEmployees(Roles.MECHANIC);
                    if (availableEmployees != null) {
                        System.out.println("Available mechanics");
                        System.out.println(IntStream.range(0, availableEmployees.size())
                                .mapToObj(i -> "\n" + (i + 1) + "-" + availableEmployees.get(i).getName())
                                .toList());
                        System.out.println("Type ID of your employee");
                        waiting(reader1);
                        var wantedEmployee = availableEmployees.get(Integer.parseInt(input) - 1);
                        Map<LocalDate, List<BusynessType>> scheduleForEmployee = getScheduleForEmployee(availableEmployees.get(Integer.parseInt(input) - 1));
                        if (scheduleForEmployee != null) {
                            System.out.println("Schedule for " + availableEmployees.get(Integer.parseInt(input) - 1).getName());
                            //System.out.println(scheduleForEmployee);
                            System.out.println("             9:00-12:00(1) 12:00-15:00(2) 15:00-18:00(3)");
                            for (Map.Entry<LocalDate, List<BusynessType>> entry : scheduleForEmployee.entrySet()) {
                                System.out.printf("%s   %-13s %-14s %-13s%n",
                                        entry.getKey(),
                                        entry.getValue().get(0) == BusynessType.FREE ? "FREE" : "----",
                                        entry.getValue().get(1) == BusynessType.FREE ? "FREE" : "----",
                                        entry.getValue().get(2) == BusynessType.FREE ? "FREE" : "----");
                            }
                            System.out.println("Type time in next format:" + "\n" +
                                    "YYYY-MM-DD *number of day part*" + "\nExample: <2024-05-26 3>");
                            waiting(reader1);

                            Pattern pattern = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2}) ([1-3])$");
                            Matcher matcher = pattern.matcher(input);
                            if (matcher.matches()) {
                                LocalDate date = LocalDate.parse(matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3));
                                int number = Integer.parseInt(matcher.group(4));

                                if (scheduleForEmployee.containsKey(date)) {
                                    System.out.println("LocalDate: " + date);
                                    System.out.println("Number: " + number);
                                    if (scheduleForEmployee.get(date)
                                            .get(number - 1)
                                            .equals(BusynessType.FREE)) {
                                        System.out.println("Want to create order?" + "\n"
                                                + date + " " + (number == 1 ? "9-12" : (number == 2) ? "12-15" : "15-18")
                                                + "\n" + "(Y/N)");
                                        waiting(reader1);
                                        if (input.equalsIgnoreCase("Y")) {
                                            CreateOrder(wantedCar, wantedSparePart, wantedEmployee, date, number);
                                        }
                                    } else {
                                        System.out.println("He is not free for that choosing");
                                    }
                                } else {
                                    System.out.println("Incorrect date");
                                }
                            } else {
                                System.out.println("Incorrect input format: " + input);
                            }

                        }
                    }
                }
            }
        } else {
            System.out.println("Unsupported command\n");
        }
        inProc = false;
        initialSout();
    }

    private static void CreateOrder(Vehicle car, SparePart sparePart, Employee employee, LocalDate date, Integer dayPart) {
        var session = configurator.getSession();
        try (session) {
            Order order = Order.builder()
                    .orderDate(LocalDate.now())
                    .vehicle(car)
                    .master(employee)
                    .sparePart(sparePart)
                    .status(Status.NEW)
                    .build();

            session.beginTransaction();
            session.save(order);

            session.createQuery("UPDATE SparePart s SET s.stock = :newStock WHERE s.id = :sparePartId")
                    .setParameter("newStock", sparePart.getStock() - 1)
                    .setParameter("sparePartId", sparePart.getId()).executeUpdate();

            session.createQuery("UPDATE Schedule sch SET " +
                            (dayPart == 1 ? "sch.firstThird " : dayPart == 2 ? "sch.secondThird " : "sch.thirdThird ") +
                            " = :BusyType " + "WHERE sch.emp = :employee AND sch.workDate = :datein")
                    .setParameter("BusyType", BusynessType.BUSY)
                    .setParameter("employee", employee)
                    .setParameter("datein", date)
                    .executeUpdate();

            session.getTransaction().commit();

            System.out.println("-------------YOUR ORDER---------------" +
                    "\n" +
                    "Today: " + LocalDate.now() +
                    "\n" +
                    "Vehicle: " + car.getBrand() + " " + car.getModel() + " " + car.getGeneration() +
                    "\n" +
                    "Spare Part: " + sparePart.getName() +
                    "\n" +
                    "Price: " + sparePart.getPriceOut() + "$" +
                    "\n" +
                    "Mechanic: " + employee.getName() + " " + employee.getSurname() +
                    "\n" +
                    "Repair date: " + date +
                    "\n" +
                    "Time: " + (dayPart == 1 ? "9:00-12:00" : (dayPart == 2) ? "12:00-15:00" : "15:00-18:00") +
                    "\n" +
                    "======================================");
        }
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
                System.out.println("VSE ZANYATO U NEGO");
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

    private static List<Employee> getAvailableEmployees(Roles roles) {
        List<Employee> emp;
        var session = configurator.getSession();
        try (session) {
            session.beginTransaction();
            emp = session.createQuery("FROM Employee WHERE role = :someRole", Employee.class)
                    .setParameter("someRole", roles)
                    .list();
            if (emp.isEmpty()) {
                System.out.println("No " + roles + "found in the database.");
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
}
