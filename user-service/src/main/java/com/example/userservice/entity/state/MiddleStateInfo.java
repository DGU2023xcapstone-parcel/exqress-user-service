package com.example.userservice.entity.state;

import com.example.userservice.entity.QRcode;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Middle_STATE_INFO")
public class MiddleStateInfo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "middle_state_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String qrId;

    @Column(nullable = false)
    private String deliveryName;
    @Column(nullable = false)
    private String deliveryPhoneNumber;
    @Column(nullable = false)
    private String curState;

    @OneToOne(mappedBy = "middleStateInfo")
    private QRcode qRinfo;

    public void assignQRInfo(QRcode qRcode){
        this.qRinfo = qRcode;
    }
}
