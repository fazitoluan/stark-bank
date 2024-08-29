package com.invoiceissuer.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "Rule")
@Table(name = "Rule")
public class RuleModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String key;
    public String value;
    @ManyToOne
    @JoinColumn
    public InvoiceModel invoice;
}
