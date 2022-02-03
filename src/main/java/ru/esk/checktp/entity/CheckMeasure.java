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
public class CheckMeasure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Measure measure;
    @ManyToOne(fetch = FetchType.EAGER)
    private Checklist checklist;
}
