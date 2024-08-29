package com.invoiceissuer.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "Discount")
@Table(name = "Discount")
public class DiscountModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Integer percentage;
    public String due;
    @ManyToOne
    @JoinColumn
    public InvoiceModel invoice;
}
