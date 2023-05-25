package com.example.userservice.entity.state;

import com.example.userservice.entity.QRinfo;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class FirstStateInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "first_state_id")
    private Long id;

    @Column(nullable = false)
    private String deliveryName;
    @Column(nullable = false)
    private String deliveryPhoneNumber;
    @Column(nullable = false)
    private String curState;

    @OneToOne(mappedBy = "firstStateInfo")
    private QRinfo qRinfo;
}