package platform;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SnippetRepository extends CrudRepository<CodeSnippet, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM SNIPPET WHERE TIME <= 0 AND VIEWS <= 0 ORDER BY ID DESC LIMIT 10")
    public List<CodeSnippet> findAllLatest();

    public Optional<CodeSnippet> findByUuid (String uuid);

}
