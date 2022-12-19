package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;


@Entity
@Getter
public final class Url extends Model {

    private final String name;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @WhenCreated
    private Instant createdAt;

    public Url(String name) {
        this.name = name;
    }
}
