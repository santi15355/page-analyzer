package hexlet.code.domain;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public final class Url extends Model {

    @Id
    private long id;

    private String name;

    private Instant createdAt;


}
