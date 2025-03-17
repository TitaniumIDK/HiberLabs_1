package com.ilya.entity;
import lombok.*;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SpareParts")
public class SparePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

//    @Column(name = "aim_car", nullable = false)
//    private Integer aimCar;

    @Column(name = "price_in", precision = 10, scale = 2)
    private Double priceIn;

    @Column(name = "price_out", precision = 10, scale = 2)
    private Double priceOut;

    //@Column(name = "stock")
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicleId;
}
