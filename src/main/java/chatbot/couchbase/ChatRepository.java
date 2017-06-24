package chatbot.couchbase;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ramgathreya on 6/23/17.
 */
@Repository
public interface ChatRepository extends CouchbaseRepository <Chat, String> {
}
