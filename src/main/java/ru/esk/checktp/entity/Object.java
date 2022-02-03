package ru.esk.checktp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "check_tp", catalog = "postgres")
public class Object {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Type type;
    @Column(name = "number")
    private int number;
    @Column(name = "letter")
    private String letter;

    @Override
    public String toString() {
        return type.getName() + number + (letter.isEmpty() ? "" : letter);
    }
}














