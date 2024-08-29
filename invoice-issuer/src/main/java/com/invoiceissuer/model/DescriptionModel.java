package com.invoiceissuer.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "Description")
@Table(name = "Description")
public class DescriptionModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String key;
    public String value;
    @ManyToOne(optional = false)
    @JoinColumn
    public InvoiceModel invoice;
}
