package net.caspervg.lex4j.route;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.caspervg.lex4j.auth.Auth;
import net.caspervg.lex4j.bean.Lot;
import net.caspervg.lex4j.log.LEX4JLogger;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Provides routing options for the <i>Search</i> endpoint
 * @see <a href="http://restlet.org/learn/javadocs/2.1/jse/api/org/restlet/data/Status.html">Restlet Status API Javadoc</a>
 * @see <a href="https://github.com/caspervg/SC4Devotion-LEX-API/blob/master/Search.md">LEX API Overview on Github</a>
 */
public class SearchRoute {

    private Auth auth;
    private Map<String, Object> parameters;

    /**
     * Constructs a new SearchRoute. This does not require authentication.
     */
    public SearchRoute() {
        this(null);
    }

    public SearchRoute(Auth auth) {
        this.auth = auth;
        this.parameters = new HashMap<>();
    }

    /**
     * Adds a parameter to the search operation
     *
     * @param filter the filter to be added
     * @param value value of the filter to be added. String.valueOf() of this Object is used for the search.
     */
    public void addFilter(Filter filter, Object value) {
        if (filter.getParameterClass().isInstance(value)) {
           parameters.put(filter.repr(), value);
        } else {
            String msg = "You need to supply the correct parameter for the " +
                    filter + " filter. Expecting a(n) " + filter.getParameterClass().getSimpleName();
            throw new FilterParameterException(msg);
        }
    }

    /**
     * Removes a parameter from the search operation
     *
     * @param filter filter to be removed
     */
    public void removeFilter(Filter filter) {
        parameters.remove(filter.repr());
    }

    /**
     * Removes all parameters from the search operation
     */
    public void clearFilters() {
        this.parameters.clear();
    }

    /**
     * Performs the search operation based on filters that are currently active.
     * Retrieves only publicly available data (no user-dependent information such as the last download date)
     *
     * @return the lots/files that were returned by the search operation
     * @see ExtraLotInfo
     */
    public List<Lot> doSearch() {
        return doSearch(new ExtraLotInfo.PublicExtraInfo());
    }

    /**
     * Performs the search operation based on filters that are currently active.
     *
     * @param extras Which extra lot information should be included in each search result
     * @return the lots/files that were returned by the search operation
     * @see ExtraLotInfo
     */
    public List<Lot> doSearch(ExtraLotInfo extras) {
        ClientResource resource = new ClientResource(Route.SEARCH.url());
        Route.handleExtraInfo(resource, extras, auth);
        Route.addParameters(resource.getReference(), this.parameters);

        try {
            Representation repr = resource.get();
            return new ObjectMapper().readValue(repr.getText(), new TypeReference<List<Lot>>() {});
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve search results correctly!");
            return null;
        }
    }
}
