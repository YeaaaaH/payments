package payments.duo.model.auth;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @Column(name="ROLE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
}