import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.runner.RunWith;

/**
 * Created by ramgathreya on 6/20/17.
 */
@RunWith(WildcardPatternSuite.class)
@SuiteClasses({ "**/Test*.class" })
public class Runner {
}