package payments.duo.model;

import lombok.Data;

import payments.duo.model.auth.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @Column(name="PAYMENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private BigDecimal amount;
    private Date createdOn;
    @ManyToOne
    @JoinColumn(name ="USER_ID")
    private User user;
    @ManyToOne
    @JoinColumn(name ="CATEGORY_ID")
    private Category category;
}