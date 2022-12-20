package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;


@Entity
@Getter
public final class Url extends Model {

    private final String name;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @WhenCreated
    private Instant createdAt;
    @OneToMany(cascade = CascadeType.ALL)
    private List<UrlCheck> urlChecks;

    public Url(String name) {
        this.name = name;
    }

    public Instant getLastCheckDate() {
        if (!urlChecks.isEmpty()) {
            return urlChecks.get(urlChecks.size() - 1).getCreatedAt();
        }
        return null;
    }

    public Integer getLastCheckStatus() {
        if (!urlChecks.isEmpty()) {
            return urlChecks.get(urlChecks.size() - 1).getStatusCode();
        }
        return null;
    }
}
