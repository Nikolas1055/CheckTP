package ru.esk.checktp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "check_tp", catalog = "postgres")
public class Checklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false)
    private Timestamp date;
    @Column(nullable = false)
    private double overallGrade;
    @ManyToOne(fetch = FetchType.EAGER)
    private Object object;
    @ManyToOne(fetch = FetchType.EAGER)
    private User master;
    @OneToMany(mappedBy = "checklist", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CheckMeasure> checkMeasureList;
}
