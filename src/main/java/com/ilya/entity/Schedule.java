package com.ilya.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee emp;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "first_third")
    @Enumerated(EnumType.STRING)
    private BusynessType firstThird;

    @Column(name = "second_third")
    @Enumerated(EnumType.STRING)
    private BusynessType secondThird;

    @Column(name = "third_third")
    @Enumerated(EnumType.STRING)
    private BusynessType thirdThird;
}
