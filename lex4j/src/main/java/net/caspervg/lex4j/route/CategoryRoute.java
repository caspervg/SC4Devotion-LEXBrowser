package net.caspervg.lex4j.route;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.caspervg.lex4j.bean.*;
import net.caspervg.lex4j.log.LEX4JLogger;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * Provides routing options for the <i>Category</i> endpoint
 * @see <a href="http://restlet.org/learn/javadocs/2.1/jse/api/org/restlet/data/Status.html">Restlet Status API Javadoc</a>
 * @see <a href="https://github.com/caspervg/SC4Devotion-LEX-API/blob/master/Category.md">LEX API Overview on Github</a>
 */
public class CategoryRoute {

    private ObjectMapper mapper;

    /**
     * Constructs a CategoryRoute. This does not require authentication.
     */
    public CategoryRoute() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Returns the broad categories
     *
     * @return the broad categories
     */
    public List<BroadCategory> getBroadCategories() {
        ClientResource resource = new ClientResource(Route.BROAD_CATEGORY.url());

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), new TypeReference<List<BroadCategory>>() {});
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve broad categories correctly!");
            return null;
        }
    }

    /**
     * Returns the LEX categories
     *
     * @return the LEX categories
     */
    public List<Category> getLEXCategories() {
        ClientResource resource = new ClientResource(Route.LEX_CATEGORY.url());

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), new TypeReference<List<Category>>() {});
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve LEX categories correctly!");
            return null;
        }
    }

    /**
     * Returns the LEX types
     *
     * @return the LEX types
     */
    public List<TypeCategory> getLEXTypes() {
        ClientResource resource = new ClientResource(Route.LEX_TYPE.url());

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), new TypeReference<List<TypeCategory>>() {});
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve LEX types correctly!");
            return null;
        }
    }

    /**
     * Returns the lot groups
     *
     * @return the lot groups
     */
    public List<GroupCategory> getLotGroups() {
        ClientResource resource = new ClientResource(Route.LOTGROUP.url());

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), new TypeReference<List<GroupCategory>>() {});
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve lot groups correctly!");
            return null;
        }
    }

    /**
     * Returns the lot authors (these are people who have uploaded at least one file)
     *
     * @return the lot authors
     */
    public List<Category> getLotAuthors() {
        ClientResource resource = new ClientResource(Route.AUTHORS.url());

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), new TypeReference<List<Category>>() {});
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve broad categories correctly!");
            return null;
        }
    }

    /**
     * Returns all the categories
     *
     * @return all the categories
     */
    public CategoryOverview getCategories() {
        ClientResource resource = new ClientResource(Route.ALL_CATEGORY.url());

        try {
            Representation repr = resource.get();
            return mapper.readValue(repr.getText(), CategoryOverview.class);
        } catch (IOException ex) {
            LEX4JLogger.log(Level.WARNING, "Could not retrieve the categories correctly!");
            return null;
        }
    }
}
