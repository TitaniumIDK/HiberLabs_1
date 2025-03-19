package com.ilya.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Employees")

public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "date_start", nullable = false)
    private LocalDate dateStart;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @Column(name = "salary", precision = 10, scale = 2)
    private Double salary;

//    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<Schedule> schedules;
}
